from pandas import read_csv
from keras.models import Sequential
from keras.layers import Dense
from keras.wrappers.scikit_learn import KerasRegressor
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import KFold
from sklearn.model_selection import train_test_split
import numpy as np

# load dataset
dataframe = read_csv("series-transformed.csv")
dataset = dataframe.values

X = dataset[:, 2:77]
print(X)
y = dataframe['output']

X_train, X_test, y_train, y_test = train_test_split(X, y)

input_size = X.shape[1]

model = Sequential()
model.add(Dense(input_size, input_dim=input_size, kernel_initializer='normal', activation='relu'))
model.add(Dense(int(input_size * 1.5), kernel_initializer='normal', activation='relu'))
model.add(Dense(input_size, kernel_initializer='normal', activation='relu'))
model.add(Dense(int(input_size * 0.5), kernel_initializer='normal', activation='relu'))
model.add(Dense(1, kernel_initializer='normal'))

# Compile model
model.compile(loss='mse', optimizer='adam', metrics=['accuracy'])

model.fit(X_train, y_train,
          epochs=2000,
          verbose=1,
          validation_data=(X_test, y_test))
score = model.evaluate(X_test, y_test, verbose=0)
print('Test loss:', score[0])
print('Test accuracy:', score[1])

# 1828
x_input = np.array([1,1814,1815,1816,1817,1818,1819,1820,1821,1822,1823,1824,1825,1826,1827,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0])
x_input = x_input.reshape((1, input_size))
yhat = model.predict(x_input, verbose=0)
print(yhat)