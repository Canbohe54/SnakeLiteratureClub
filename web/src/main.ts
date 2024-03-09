import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import locale from 'element-plus/es/locale/lang/zh-cn'
import $ from 'jquery'

const VueApp = createApp(App)

VueApp.use(store)
VueApp.use(router)
VueApp.use(ElementPlus,{locale})
VueApp.config.globalProperties.$ = $
VueApp.mount('#app')

