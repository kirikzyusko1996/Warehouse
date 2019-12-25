from pandas import read_csv
from keras.models import Sequential
from keras.layers import Dense
import matplotlib.pyplot as plt
from keras.wrappers.scikit_learn import KerasRegressor
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import KFold
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import numpy as np

from numpy.random import seed
seed(1)

import tensorflow
tensorflow.random.set_seed(2)

# load dataset
dataframe = read_csv("series-transformed.csv")
dataset = dataframe.values

X = dataset[:, 2:77]
y = dataframe['output']

X_train, X_test, y_train, y_test = train_test_split(X, y)

scaler = StandardScaler()
scaler.fit(X_train)
# fit scaler on training dataset
X_train = scaler.transform(X_train)
X_test = scaler.transform(X_test)

input_size = X.shape[1]

model = Sequential()
model.add(Dense(input_size, input_dim=input_size, kernel_initializer='normal', activation='relu'))
# model.add(Dense(int(input_size * 2), kernel_initializer='normal', activation='relu'))
model.add(Dense(int(input_size * 1.5), kernel_initializer='normal', activation='relu'))
model.add(Dense(input_size, kernel_initializer='normal', activation='relu'))
model.add(Dense(int(input_size * 0.5), kernel_initializer='normal', activation='relu'))
model.add(Dense(1, kernel_initializer='normal'))

# Compile model
model.compile(loss='mse', optimizer='adam')

model.fit(X_train, y_train,
          epochs=55000,
          verbose=1,
          validation_data=(X_test, y_test))
score = model.evaluate(X_test, y_test, verbose=0)

# 163418
x_input = np.array(scaler.transform([[0,251651,330914,426493,328758,287274,398647,367155,320362,316503,283214,335058,327631,223894,306459,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0]]))
yhat = model.predict(x_input, verbose=0)
print(yhat)

x_input = np.array(scaler.transform(X))
yhat = model.predict(x_input, verbose=0)

sum = 0
index = []
for i in range(len(yhat)):
    percentage = (yhat[i][0]-y[i]) / yhat[i][0] * 100
    print(f'Predicted: {yhat[i][0]}, Real: {y[i]}, error: {yhat[i][0]-y[i]}, percentage: {percentage}')
    sum += abs(percentage)
    index.append(i)

print(f'Average error: {sum / len(yhat)}')

plt.plot(index, y, color='green')
plt.plot(index, yhat, color='red', alpha=0.5)

plt.show()

# 194687
# 203000.00 - 2000 epoch/normalized
# 195594.27 - 20000 epoch/not normalized
# 346240.53 - 20000 epoch/normalized
# 195012.86 - 200000 epoch/normalized

# 163418
# 163348.62 - 55000 epochs/normalized
# 162871.17 - 2000  epochs/normalized
