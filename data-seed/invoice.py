import datetime
import pandas as pd

dt = datetime.datetime(2013, 9, 1)
end = datetime.datetime(2018, 1, 1)
step = datetime.timedelta(days=1)

date = []
input = []
output = []

df = pd.read_csv("original-data.csv", delimiter=';')

qty = list(df['qty'])

k = 0
while dt < end:
    date.append(dt.strftime('%Y-%m-%d'))
    input.append(qty[k])
    output.append(qty[k])
    k += 1
    dt += step

data = {
    'date': date,
    'input': input,
    'output': output,
}

df = pd.DataFrame(data)

df.to_csv('series.csv', encoding='utf-8', index=False)