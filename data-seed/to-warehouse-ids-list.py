import matplotlib.pyplot as plt
import pandas as pd
import os
import numpy as np
from sklearn.cluster import AgglomerativeClustering

file_path = os.path.join(os.path.dirname(__file__), 'SELECT_id_warehouse_FROM_warehouse_WHERE_cca2_BY__201912171405.csv')
data_frames = pd.read_csv(file_path)
ids = data_frames['id_warehouse']
ids = list(ids)

with open("ids.txt", "w") as text_file:
    for i in ids:
        text_file.write(f'{i},')
