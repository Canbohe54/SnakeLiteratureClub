import { createStore } from 'vuex'

export default createStore({
  state: {
    token: ''
  },
  getters: {
    getToken: state => {
      return state.token
    }
  },
  mutations: {
    setToken: (state, token) => {
      state.token = token
    }
  },
  actions: {
  },
  modules: {
  }
})
