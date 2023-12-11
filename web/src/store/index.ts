import { createStore } from 'vuex'

export default createStore({
  state: {
    token: 'tokenNotNull',
    userInfo: {
      id: 'u9q35V2CVZQS',
      name: 'Canbohe54',
      identity: '学生',
      unit: 'South China Normal University',
      introduction: '这个人很懒，什么都没有留下~',
      email: 'Canbohe54@snake.club',
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
    },
    clear: state => {
      state.token = ''
      state.userInfo = {
        id:'',
        name: '',
        identity: '',
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
