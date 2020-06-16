import pandas as pd
from datetime import datetime, timedelta
from typing import List, Dict
from flask import Flask, request
import mysql.connector
import json
import numpy as np
from sklearn.metrics import mean_absolute_error


app = Flask(__name__)

config = {
    'user': 'root',
    'password': 'root',
    'host': 'localhost',
    'port': '3306',
    'database': 'warehouse'
}
connection = mysql.connector.connect(**config)

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

    cursor.close()

    real = last_trend[-14:]
    # TODO: import Neural network and make calculation for 21 day
    expected = [649, 472, 557, 487, 745, 315, 0, 687, 513, 567, 474, 771, 324, 0, 621, 455, 561, 472, 711, 329, 0]
    # warehouse_id
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
    app.run(host='0.0.0.0', debug=True)
