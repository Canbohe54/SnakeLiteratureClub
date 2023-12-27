export function getCookie (key: string): string {
  const arr = document.cookie.split('; ')
  for (const item of arr) {
    const loc = item.indexOf('=')
    if (item.substring(0, loc) === key) {
      return unescape(item.substring(loc + 1))
    }
  }
  return ''
}

export function setCookie (key: string, value: string) {
  document.cookie = `${key}=${value};`
}

export function removeCookie (key: string) {
  const expire = new Date()
  expire.setTime(expire.getTime() - 1)
  document.cookie = `${key}=;expire=${expire.toUTCString()}`
}

export function clearCookie () {
  document.cookie = ''
}
