import pandas as pd

df = pd.read_csv("series-transformed.csv")
df2 = pd.read_csv("series-transformed.csv")

df2['input'] = df2['input'] + 1000000
df2['output'] = df2['output'] + 1000000

df['location_1'] = 1
df['location_2'] = 0

df2['location_1'] = 0
df2['location_2'] = 1

df = df.append(df2, ignore_index=True)

df.to_csv('series-transformed-2.csv', encoding='utf-8', index=False)
