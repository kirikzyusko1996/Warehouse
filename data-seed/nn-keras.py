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
# todo: data scaling?
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
          epochs=20000,
          verbose=1,
          validation_data=(X_test, y_test))
score = model.evaluate(X_test, y_test, verbose=0)
print('Test loss:', score[0])
print('Test accuracy:', score[1])

# 194687
x_input = np.array([0,476101,222575,176819,265509,327873,285899,316216,312975,268789,211129,222943,374590,383625,351943,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0])
x_input = x_input.reshape((1, input_size))
yhat = model.predict(x_input, verbose=0)
print(yhat)