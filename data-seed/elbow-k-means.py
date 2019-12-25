from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs

from yellowbrick.cluster import KElbowVisualizer

import pandas as pd
import numpy as np

df = pd.read_csv('SELECT_x_y_FROM_warehouse_WHERE_cca2_BY__201912171033.csv')
tuples = [[row[col] for col in df.columns] for row in df.to_dict('records')]
# print(tuples)

# Instantiate the clustering model and visualizer
model = KMeans()
visualizer = KElbowVisualizer(model, k=(1,90))

visualizer.fit(np.array(tuples))        # Fit the data to the visualizer
visualizer.show()        # Finalize and render the figure