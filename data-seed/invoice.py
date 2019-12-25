import datetime
import pandas as pd

dt = datetime.datetime(1999, 1, 1)
end = datetime.datetime(2019, 12, 31)
step = datetime.timedelta(days=1)

date = []
input = []
output = []

k = 0
while dt < end:
    date.append(dt.strftime('%Y-%m-%d'))
    input.append(k)
    output.append(k)
    k += 1
    dt += step

data = {
    'date': date,
    'input': input,
    'output': output,
}

df = pd.DataFrame(data)

df.to_csv('series.csv', encoding='utf-8', index=False)