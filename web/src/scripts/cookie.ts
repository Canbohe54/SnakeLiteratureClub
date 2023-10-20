export function getCookie (key: string) {
  const arr = document.cookie.split('; ')
  for (const item in arr) {
    const loc = item.indexOf('=')
    if (item.substring(0, loc) === key) {
      return item.substring(loc + 1)
    }
  }
  return null
}

export function setCookie (key: string, value: string) {
  document.cookie = document.cookie + '; ' + key + '=' + value
}
