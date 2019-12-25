import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

data = pd.read_csv(r"original-data.csv", delimiter=';')

print(data.head())

data['qty'].hist(bins=20)

plt.show()

data.plot.line(x='date', y='qty', figsize=(8,6))

plt.show()