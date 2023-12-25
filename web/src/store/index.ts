import { createStore } from 'vuex'

export default createStore({
  state: {
    token: '',
    userInfo: {
      id: '',
      name: '',
      identity: '未登录',
      unit: '',
      introduction: '',
      email: '',
      avatar: ''
    }
  },
  getters: {
    getToken: state => {
      return state.token
    },
    getUserInfo: state => {
      return state.userInfo
    }
  },
  mutations: {
    setToken: (state, token) => {
      state.token = token
    },
    setUserInfo: (state, userInfo) => {
      state.userInfo = userInfo
    },
    clear: state => {
      state.token = ''
      state.userInfo = {
        id:'',
        name: '',
        identity: '未登录',
        unit: '',
        introduction: '',
        email: '',
        avatar: ''
      }
    }
  },
  actions: {
  },
  modules: {
  }
})
