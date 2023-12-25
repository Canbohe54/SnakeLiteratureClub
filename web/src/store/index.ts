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
      console.log(state.token)
    },
    setUserInfo: (state, userInfo) => {
      console.log(userInfo)
      state.userInfo = {
        id: userInfo.id,
        name: userInfo.name,
        identity: userInfo.group,
        unit: userInfo.organization,
        introduction: userInfo.introduction,
        email: userInfo.email,
        avatar: userInfo.pictureUrl
      }
      console.log(state.userInfo)
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
