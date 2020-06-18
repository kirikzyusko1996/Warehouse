from datetime import datetime, timedelta
from typing import List, Dict
from flask import Flask, request
import mysql.connector
import json
import numpy as np
from sklearn.metrics import mean_absolute_error
from joblib import load


from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import BatchNormalization
from tensorflow.keras import regularizers

# sklearn - 0.22.2.post1, tensorflow - 2.2.0, keras - 2.3.1

import keras.backend.tensorflow_backend as tb
tb._SYMBOLIC_SCOPE.value = True

model = Sequential()

model.add(Dense(int(1774), input_dim=380, kernel_initializer='normal', kernel_regularizer=regularizers.l2(0.001), activation='relu', name='hidden_1'))
model.add(BatchNormalization())

model.add(Dense(int(1183), kernel_initializer='normal', activation='relu', kernel_regularizer=regularizers.l2(0.001), name='hidden_2'))
model.add(BatchNormalization())
model.add(Dense(int(1183), kernel_initializer='normal', activation='relu', kernel_regularizer=regularizers.l2(0.001), name='hidden_21'))
model.add(BatchNormalization())

model.add(Dense(int(591), kernel_initializer='normal', activation='relu', kernel_regularizer=regularizers.l2(0.001), name='hidden_3'))
model.add(BatchNormalization())
model.add(Dense(int(591), kernel_initializer='normal', activation='relu', kernel_regularizer=regularizers.l2(0.001), name='hidden_31'))
model.add(BatchNormalization())

model.add(Dense(1, kernel_initializer='normal', kernel_regularizer=regularizers.l2(0.001), name='output_layer'))

# Compile model
model.compile(loss='mse', optimizer='adam', metrics=['accuracy'])

model.load_weights('model-warehouse-stock-prediction.h5')

app = Flask(__name__)

config = {
    'user': 'root',
    'password': 'root',
    'host': 'localhost',
    'port': '3306',
    'database': 'warehouse'
}
connection = mysql.connector.connect(**config)


def to_one_hot(value, ranges):
    one_hot = []
    for i in range(ranges):  # KMeans was trained on 300 clusters
        one_hot.append(0)

    one_hot[value] = 1

    return one_hot


def favorite_colors() -> List[Dict]:
    config = {
        'user': 'root',
        'password': 'root',
        'host': 'localhost',
        'port': '3306',
        'database': 'warehouse'
    }
    connection = mysql.connector.connect(**config)
    cursor = connection.cursor()
    cursor.execute('SELECT * FROM role')
    results = [{id_role: name} for (id_role, name) in cursor]
    cursor.close()
    connection.close()

    return results


@app.route('/web/web/forecast/series')
def series() -> str:
    id_warehouse = request.args.get('idWarehouse')
    cursor = connection.cursor()

    # ############### LAST 14 DAYS ###############
    date = datetime.today() - timedelta(days=28)  # last 14 as real + 14 for prediction "real"

    # TODO: remove SQL-injection attack
    cursor.execute("""
        SELECT date(goods_status.date) as day, SUM(goods.quantity) as amount FROM goods
        INNER JOIN goods_status ON goods.id_goods = goods_status.id_goods
        INNER JOIN invoice ON invoice.id_invoice = goods.id_outgoing_invoice
        WHERE goods.id_moved_out_status IS NOT NULL AND goods_status.id_goods_status_name = 12 AND invoice.id_warehouse = @warehouse_id AND goods_status.`date` >= '@date'
        GROUP BY day
    """.replace('@warehouse_id', id_warehouse).replace('@date', str(date)))

    results = {}
    last_trend = []

    for (day, amount) in cursor:
        results[str(day)] = float(amount)

    for i in range(28):
        date = datetime.today() - timedelta(days=28 - i + 1)
        key = str(date).split(" ")[0]
        try:
            last_trend.append(results[key])
        except: # key does not exist
            last_trend.append(0)

    real = last_trend[-14:]

    # ############### Clustering ###############
    cursor.execute("""
        SELECT warehouse.x as lng, warehouse.y as ltd, AVG(price_list.daily_price) as daily_price FROM warehouse
        INNER JOIN warehouse_company ON warehouse.id_warehouse_company = warehouse_company.id_warehouse_company
        INNER JOIN price_list ON warehouse_company.id_warehouse_company = price_list.id_warehouse_company
        WHERE warehouse.id_warehouse = @warehouse_id
        GROUP BY warehouse.id_warehouse
    """.replace('@warehouse_id', id_warehouse))

    results = [(lng, ltd, daily_price) for (lng, ltd, daily_price) in cursor]
    (lng, ltd, daily_price) = results[0]

    cursor.close()

    k_means_clustering = load('clustering.joblib')
    cluster = k_means_clustering.predict([[lng, ltd]])[0]

    geo_data = to_one_hot(cluster, 300)

    predictions = []

    data_for_predictions_last_14_days = []
    # predict last 14 days
    for i in range(14):
        # ############### DATA BUILDER ###############
        date = datetime.today() - timedelta(days=14 - i)
        one_hot_weekday = to_one_hot(date.weekday(), 7)
        one_hot_week_in_year = to_one_hot(date.isocalendar()[1], 52)
        store_type = [0, 0, 0, 0]  # mock for now
        assortment = [0, 0, 0]  # mock for now

        last_14_sales = []
        for j in range(14):
            last_14_sales.append(last_trend[i+j])

        # sales_14
        raw_data = one_hot_weekday + one_hot_week_in_year + store_type + assortment + geo_data + last_14_sales

        data_for_predictions_last_14_days.append(raw_data)

    # ############### Scaling ###############
    scaler = load('scaler.pkl')
    scaled_data = scaler.transform(data_for_predictions_last_14_days)

    # ############### Neural Network ###############
    data = np.array(scaled_data)

    predictions = model.predict(data)

    pred = []
    for i in range(14):
        pred.append(predictions[i][0])

    # TODO: calcuate by NN next 7 days
    expected = pred + [621, 455, 561, 472, 711, 329, 0]

    return json.dumps({
        'real': real,
        'expected': expected,
        # TODO: calculate mean absolute percentage error
        'error': mean_absolute_error(real, expected[0:14]),
    })


@app.route('/web/web/forecast/constructor')
def constructor() -> str:
    # warehouse_id
    values = [789, 572, 667, 387, 545, 815, 0, 547, 763, 667, 521, 671, 981, 0, 561, 321, 687, 713, 1015, 415, 0]
    return json.dumps({
        'expected': values,
        'total': int(np.sum(values)),
    })


@app.route('/web/web/forecast/constructor/initial-values')
def initial_values() -> str:
    id_warehouse = request.args.get('idWarehouse')
    cursor = connection.cursor()

    date = datetime.today() - timedelta(days=14)  # last 14 days

    # TODO: remove SQL-injection attack
    cursor.execute("""
        SELECT date(goods_status.date) as day, SUM(goods.quantity) as amount FROM goods
        INNER JOIN goods_status ON goods.id_goods = goods_status.id_goods
        INNER JOIN invoice ON invoice.id_invoice = goods.id_outgoing_invoice
        WHERE goods.id_moved_out_status IS NOT NULL AND goods_status.id_goods_status_name = 12 AND invoice.id_warehouse = @warehouse_id AND goods_status.`date` >= '@date'
        GROUP BY day
    """.replace('@warehouse_id', id_warehouse).replace('@date', str(date)))

    results = {}
    last_trend = []

    for (day, amount) in cursor:
        results[str(day)] = float(amount)

    for i in range(14):
        date = datetime.today() - timedelta(days=14 - i + 1)
        key = str(date).split(" ")[0]
        try:
            last_trend.append(results[key])
        except: # key does not exist
            last_trend.append(0)

    cursor.execute("""
        SELECT warehouse.x as lng, warehouse.y as ltd, AVG(price_list.daily_price) as daily_price FROM warehouse
        INNER JOIN warehouse_company ON warehouse.id_warehouse_company = warehouse_company.id_warehouse_company
        INNER JOIN price_list ON warehouse_company.id_warehouse_company = price_list.id_warehouse_company
        WHERE warehouse.id_warehouse = @warehouse_id
        GROUP BY warehouse.id_warehouse
    """.replace('@warehouse_id', id_warehouse))

    results = [(lng, ltd, daily_price) for (lng, ltd, daily_price) in cursor]
    (lng, ltd, daily_price) = results[0]

    cursor.close()

    # warehouse_id
    return json.dumps({
        'day1': last_trend[0],
        'day2': last_trend[1],
        'day3': last_trend[2],
        'day4': last_trend[3],
        'day5': last_trend[4],
        'day6': last_trend[5],
        'day7': last_trend[6],
        'day8': last_trend[7],
        'day9': last_trend[8],
        'day10': last_trend[9],
        'day11': last_trend[10],
        'day12': last_trend[11],
        'day13': last_trend[12],
        'day14': last_trend[13],
        'lat': float(ltd),
        'lng': float(lng),
        'dailyPrice': float(daily_price),
    })


@app.route('/web/web/forecast/tracker')
def tracker() -> str:
    return json.dumps({'favorite_colors': ['red', 'blue', 'green']})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, threaded=False)
