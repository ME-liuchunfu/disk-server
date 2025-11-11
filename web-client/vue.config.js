const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 开发环境下启用完整的源码映射
  chainWebpack: (config) => {
    if (process.env.NODE_ENV === 'development') {
      config.devtool('source-map')
    }
  }
})
