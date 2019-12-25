from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import classification_report
import pandas as pd

wine = pd.read_csv('series-transformed.csv')

X = wine.drop('output',axis=1).drop('input',axis=1)
y = wine['output']

X_train, X_test, y_train, y_test = train_test_split(X, y)
print(X_test)
scaler = StandardScaler()
# Fit only to the training data
scaler.fit(X_train)

X_train = scaler.transform(X_train)
X_test = scaler.transform(X_test)

input_size = X_train.shape[1]
model = MLPClassifier(
    solver='lbfgs',
    alpha=1e-5,
    hidden_layer_sizes=(int(input_size * 1.5), int(input_size * 1), int(input_size * 0.5)),
    random_state=1
)

model.fit(X_train, y_train)

predictions = model.predict(X_test)
print(predictions)
# print(classification_report(y_test,predictions))