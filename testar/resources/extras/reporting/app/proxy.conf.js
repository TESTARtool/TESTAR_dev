const PROXY_CONFIG = {
  "/api/*": {
    "target": process.env.API_SERVER,
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
};

module.exports = PROXY_CONFIG;
