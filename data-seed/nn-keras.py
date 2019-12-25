from pandas import read_csv
from keras.models import Sequential
from keras.layers import Dense
from keras.wrappers.scikit_learn import KerasRegressor
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import KFold
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import numpy as np

import tensorflow
tensorflow.random.set_seed(123)

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
          epochs=2000,
          verbose=1,
          validation_data=(X_test, y_test))
score = model.evaluate(X_test, y_test, verbose=0)
# print('Test loss:', score[0])
# print('Test accuracy:', score[1])

# 194687
x_input = np.array(scaler.transform([[0,476101,222575,176819,265509,327873,285899,316216,312975,268789,211129,222943,374590,383625,351943,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0]]))
yhat = model.predict(x_input, verbose=0)
print(yhat)

# 203000.00 - 2000 epoch/normalized
# 195594.27 - 20000 epoch/not normalized
# 346240.53 - 20000 epoch/normalized
# yyyyyy.yy - 200000 epoch/normalized