import matplotlib.pyplot as plt
import pandas as pd
import os
import numpy as np
from sklearn.cluster import AgglomerativeClustering

file_path = os.path.join(os.path.dirname(__file__), 'SELECT_x_y_FROM_warehouse_WHERE_cca2_RU__201912171059.csv')
data_frames = pd.read_csv(file_path)

x = data_frames['x']
y = data_frames['y']

x = list(x)  # np.array(x)
y = list(y)

# 2
fig, ax = plt.subplots()
ax.scatter(x, y)

plt.show()

X = np.array([[x[0], y[0]]])
for i in range(len(x)):
    X = np.append(X, [[x[i], y[i]]], axis=0)

print(X)

cluster = AgglomerativeClustering(n_clusters=20, affinity='euclidean', linkage='ward')
cluster.fit_predict(X)

plt.scatter(X[:,0], X[:,1], c=cluster.labels_, cmap='rainbow')

plt.show()
