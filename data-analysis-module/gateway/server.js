const express = require('express');
const proxy = require('http-proxy').createProxyServer({});

// const
const port = 3200;
const host = '0.0.0.0';

// app
const app = express();
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