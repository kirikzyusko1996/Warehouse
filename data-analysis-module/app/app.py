from typing import List, Dict
from flask import Flask
import json

app = Flask(__name__)


def favorite_colors() -> List[Dict]:
    config = {
        'user': 'root',
        'password': 'root',
        'host': 'db',
        'port': '3306',
        'database': 'knights'
    }
    connection = mysql.connector.connect(**config)
    cursor = connection.cursor()
    cursor.execute('SELECT * FROM favorite_colors')
    results = [{name: color} for (name, color) in cursor]
    cursor.close()
    connection.close()

    return results


@app.route('/web/web/forecast/series')
def series() -> str:
    return json.dumps({
        'real': [650, 470, 557, 489, 743, 312, 0, 683, 511, 569, 472, 774, 324, 0],
        'expected': [649, 472, 557, 487, 745, 315, 0, 687, 513, 567, 474, 771, 324, 0, 621, 455, 561, 472, 711, 329, 0],
        'error': 0.019283
    })


@app.route('/web/web/forecast/constructor')
def constructor() -> str:
    return json.dumps({'favorite_colors': ['red', 'blue', 'green']})


@app.route('/web/web/forecast/tracker')
def tracker() -> str:
    return json.dumps({'favorite_colors': ['red', 'blue', 'green']})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
