import {createStore} from 'vuex'

export default createStore({
  state: {
    token: 'tokenNotNull',
    userInfo: {
      name: 'Canbohe54',
      identity: '专家',
      unit: 'South China Normal University',
      introduction: '这个人很懒，什么都没有留下~',
      email: 'Canbohe54@snake.club',
      avatar: 'https://avatars.githubusercontent.com/u/43968296'
    },
    searchKey: ''
  },
  getters: {
    getToken: state => {
      return state.token
    },
    getUserInfo: state => {
      return state.userInfo
    },
    getSearchKey: state => {
      return state.searchKey
    }
  },
  mutations: {
    setToken: (state, token) => {
      state.token = token
    },
    setUserInfo: (state, userInfo) => {
      state.userInfo = userInfo
    },
    setSearchKey: (state, searchKey)=>{
      state.searchKey = searchKey
    },
    clear: state => {
      state.token = ''
      state.userInfo = {
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
