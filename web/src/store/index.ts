import { createStore } from 'vuex'

export default createStore({
  state: {
    token: '',
    userInfo: {
      name: 'Canbohe54',
      identity: '专家',
      unit: 'South China Normal University',
      introduction: '这个人很懒，什么都没有留下~',
      email: 'Canbohe54@outlook.com',
      avatar: 'https://avatars.githubusercontent.com/u/43968296'
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
    }
  },
  actions: {
  },
  modules: {
  }
})
