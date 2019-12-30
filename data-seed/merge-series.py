import pandas as pd
import random

df = pd.read_csv("series-transformed.csv")

for i in range(5):
    df2 = pd.read_csv("series-transformed.csv")
    df2['input'] = df2['input'] + random.randint(0, 2000)
    df2['output'] = df2['output'] + random.randint(0, 2000)
    df = df.append(df2, ignore_index=True)

df.to_csv('series-transformed-2.csv', encoding='utf-8', index=False)
