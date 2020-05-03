const express = require('express');
const proxy = require('http-proxy').createProxyServer({});

// const
const port = 3200;
const host = '0.0.0.0';

// app
const app = express();

app.use((req, res, next) => {
    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', '*');

    // Request methods you wish to allow
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');

    // Request headers you wish to allow
    res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization, credentials');

    // Set to true if you need the website to include cookies in the requests sent
    // to the API (e.g. in case you use sessions)
    res.setHeader('Access-Control-Allow-Credentials', true);

    // Pass to next layer of middleware
    next();
});

app.use('/', (req, res, next) => {
    if (req.url.includes('forecast')) {
        proxy.web(req, res, {
            target: `http://${host}:5000`,
        }, next);
    } else {
        proxy.web(req, res, {
            target: `http://${host}:8081`,
        }, next);
    }
});

app.listen(port, () => {
    console.log(`running on http://${host}:${port}`);
});