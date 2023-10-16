import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
const VueApp = createApp(App)

VueApp.use(store)
VueApp.use(router)
VueApp.use(ElementPlus)
VueApp.mount('#app')
