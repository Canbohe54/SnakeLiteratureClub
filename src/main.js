import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import App from './App.vue'
import router from './router'

const Vue = createApp(App)
Vue.use(router)
Vue.use(ElementPlus)
Vue.mount('#app')
