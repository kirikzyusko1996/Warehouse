from typing import List, Dict
from flask import Flask
import mysql.connector
import json
import numpy as np

app = Flask(__name__)


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
    # warehouse_id
    return json.dumps({
        'real': [650, 470, 557, 489, 743, 312, 0, 683, 511, 569, 472, 774, 324, 0],
        'expected': [649, 472, 557, 487, 745, 315, 0, 687, 513, 567, 474, 771, 324, 0, 621, 455, 561, 472, 711, 329, 0],
        'error': 0.019283
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
    # warehouse_id
    return json.dumps({
        'day1': 650,
        'day2': 470,
        'day3': 557,
        'day4': 489,
        'day5': 743,
        'day6': 312,
        'day7': 0,
        'day8': 683,
        'day9': 511,
        'day10': 569,
        'day11': 472,
        'day12': 774,
        'day13': 324,
        'day14': 0,
        'lat': 53.8868861,
        'lng': 27.542942,
        'dailyPrice': 45,
    })


@app.route('/web/web/forecast/tracker')
def tracker() -> str:
    return json.dumps({'favorite_colors': ['red', 'blue', 'green']})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
