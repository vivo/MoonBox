/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

const path = require('path')
const HtmlWebpackInlinePlugin = require('html-webpack-inline-plugin') // 引用 npm html-webpack-inline-plugin包，用来将html中inline标识的<script>,<link>,<img>标签的元素内容压缩进html中
const FileManagerPlugin = require('filemanager-webpack-plugin') // 引用 npm filemanager-webpack-plugin包，用来操作文件，允许复制，归档成 (.zip/.tar/.tar.gz)，移动，删除文件和目录在构建前或者构建前， 文档地址：https://www.npmjs.com/package/filemanager-webpack-plugin
const isProd = process.env.NODE_ENV !== 'local'  // 判断是否是生产环境，这里的生产环境包括线上开发、测试、正式等环境

function resolve (dir) {
  return path.join(__dirname, dir)
}

// 定义一些公用参数，以供项目中使用
const pluginOptions = {
  projectName: 'moonbox',
  host: 'localhost',
  port: 8888,
  cdnPath: './'
}

module.exports = {
  pluginOptions,
  outputDir: 'dist',
  devServer: {
    host: pluginOptions.host,
    port: pluginOptions.port,
    public: `${pluginOptions.host}:${pluginOptions.port}`,
    open: true,
    disableHostCheck: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        ws: false,
        changeOrigin: true
      }
    }
  },

  publicPath: isProd ? pluginOptions.cdnPath : '/',
  lintOnSave: true,
  productionSourceMap: !isProd,
  assetsDir: 'static',
  css: {
    sourceMap: !isProd
  },
  configureWebpack: config => {
    config.resolve = {
      extensions: ['.js', '.vue', '.json'],
      alias: {
        root: resolve(''),
        '@': resolve('src'),
        public: resolve('public'),
        api: resolve('src/api'),
        assets: resolve('src/assets'),
        components: resolve('src/components'),
        utils: resolve('src/utils'),
        router: resolve('src/router'),
        store: resolve('src/store'),
        views: resolve('src/views')
      }
    }
    config.optimization && (config.optimization.splitChunks.minSize = 10000)
    config.plugins.push(new HtmlWebpackInlinePlugin())
    if (isProd) {
      config.plugins.push(
        // Webpack完成捆绑过程后要执行的命令：删除dist目录下ignore目录，prod-md5-zip目录
        new FileManagerPlugin({
          onEnd: [{
            delete: [`./dist/ignore`, './prod-md5-zip']
          }]
        })
      )
    }
    // 单个asset静态资源文件的大小最大为409600B==>400KB,超过400KB则会给出警告
    config.performance = {
      maxAssetSize: 1024 * 400
    }
  },
  chainWebpack: config => {
    config.plugin('html').tap(args => {
      // 将系统配置信息注入到HtmlWebpackInlinePlugin
      Object.assign(args[0], pluginOptions)
      // 设置页面标题的icon
      args[0].inject = true
      args[0].favicon = path.resolve(__dirname, 'public/static/favicon.ico')
      return args
    })
    config.plugin('define').tap(args => {
      args[0]['CONFIG'] = JSON.stringify(pluginOptions)
      return args
    })
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  }
}
