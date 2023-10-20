import { getCookie, setCookie } from './cookie'

export function getToken () {
  const token = getCookie('token')
  if (token != null) {
    return getCookie('token')
  }
  return null
}

export function setToken (token: string) {
  setCookie('token', token)
}

export function checkTokenExist () {
  return getCookie('token') != null
}
