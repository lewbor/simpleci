var webpack = require("webpack");
var path = require('path');
var ExtractTextPlugin = require("extract-text-webpack-plugin");

var production = process.env.NODE_ENV === 'production';
var node_modules_dir = path.resolve(__dirname, 'node_modules');

// To use lazy less loading with old node engine
require('es6-promise').polyfill();

var loaders = [
    {
        test: /\.js$/,
        exclude: [node_modules_dir],
        loader: "babel-loader",
        query: {
            presets: ["es2015"]
        }
    },

    {test: /\.(nunj|nunjucks)$/, loader: 'nunjucks-loader'},
    {test: /\.less$/, loader: ExtractTextPlugin.extract("style-loader", "css-loader?sourceMap!less-loader?sourceMap")},
    {test: /\.(png|jpg|gif)$/, loader: 'file?name=img/[name].[ext]'},
    {test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: "file?name=img/[name].[ext]"},
    {test: /\.woff(\?v=\d+\.\d+\.\d+)?$/, loader: "file?name=font/[name].[ext]"},
    {test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/, loader: "file?name=font/[name].[ext]"},
    {test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: "file?name=font/[name].[ext]"},
    {test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: "file?name=font/[name].[ext]"}
];

var config = {
    entry: {
        app: "./src/js/main",
        styles: './src/style/main.less'
    },
    output: {
        path: path.join(__dirname, '../web/assets'),
        filename: "[name].js"
    },
    resolve: {
        modulesDirectories: ['node_modules', './src/js']
    },
    devtool: 'source-map',
    module: {
        loaders: loaders
    },
    plugins: [
        new ExtractTextPlugin("[name].css"),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            "window.jQuery": "jquery",
            SockJS: "sockjs-client"
        })
    ]
};

if (production) {
    config.plugins = config.plugins.concat([
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.UglifyJsPlugin({
            mangle: false,
            sourceMap: false
        })
    ]);
}


module.exports = [config];