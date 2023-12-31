import { createStore } from 'vuex'
import { getCookie, setCookie, removeCookie, clearCookie } from '@/scripts/cookie'

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
      avatar: '',
      stuGrade: ''
    }
  },
  getters: {
    getToken: state => {
      return getCookie('token')
      // return state.token
    },
    getUserInfo: state => {
      const s = getCookie('userInfo')
      if (s === '') {
        return {}
      }
      return JSON.parse(s)
      // return state.userInfo
    }
  },
  mutations: {
    setToken: (state, token) => {
      setCookie('token', token)
    },
    setUserInfo: (state, userInfo) => {
      const parsedUserInfo = {
        id: userInfo.id,
        name: userInfo.name,
        identity: userInfo.group,
        unit: userInfo.organization,
        introduction: userInfo.introduction,
        email: userInfo.email,
        avatar: userInfo.pictureUrl,
        stuGrade: userInfo.attr === '' ? '' : JSON.parse(userInfo.attr)['grade']
      }
      setCookie('userInfo', JSON.stringify(parsedUserInfo))
    },
    clear: state => {
      setCookie('token', '')
      setCookie('userInfo', '{"identity":"未登录"}')
    }
  },
  actions: {
  },
  modules: {
  }
})
