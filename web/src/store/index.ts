import { createStore } from 'vuex'
import { getCookie, setCookie, removeCookie, clearCookie } from '@/scripts/cookie'

export default createStore({
  state: {
    token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjExNDUxNCJ9.AzE55n2_JDJolF-UQ94Qgun_szDCqsu_KYDDD6Tcebw',
    userInfo: {
      id: '',
      name: '',
      phone: '',
      identity: '未登录',
      organization: '',
      introduction: '',
      pictureUrl: '',
      attrs:''
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
        return JSON.parse('{"identity":"未登录"}')
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
        phone: userInfo.phone,
        identity: userInfo.identity,
        organization: userInfo.organization,
        introduction: userInfo.introduction,
        pictureUrl: userInfo.pictureUrl,
        attrs: userInfo.attrs
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
