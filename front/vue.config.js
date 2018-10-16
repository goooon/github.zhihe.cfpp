module.exports = {
    devServer: {
        host: '127.0.0.1',
        port: 8081,
        proxy: {
            '/cfpp': {
                target: 'http://127.0.0.1:8080',  // 请求本地 需要xboot后台项目
                ws: true
            },
            '/foo': {
                target: '<other_url>'
            }
        }
    },
    // 打包时不生成.map文件 避免看到源码
    productionSourceMap: false
}