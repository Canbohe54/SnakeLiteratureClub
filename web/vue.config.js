const { defineConfig } = require('@vue/cli-service')
const NodePolyfillPlugin = require('node-polyfill-webpack-plugin')
const webpack = require('webpack')
const CopyPlugin = require('copy-webpack-plugin')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  configureWebpack: {
    plugins: [
      new NodePolyfillPlugin(),
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'windows.jQuery': 'jquery',
        Popper: ['popper.js', 'default']
      }),
      new CopyPlugin({
        patterns: [
          {
            from: "src/assets/avatars",
            to: "avatars"
          }
        ]
      })
    ]
  }
})


