// 获取assets静态资源
export function getAssetsFile(url: string)  {
  console.log(`../assets/${url}`)
  return new URL(`../assets/${url}`, import.meta.url).href;
}
export function base64ToBlob (base64: string) {
  const binaryStr = atob(base64)
  const len = binaryStr.length
  const bytes = new Uint8Array(len)
  for (let i = 0; i < len ; i++) {
    bytes[i] = binaryStr.charCodeAt(i)
  }
  // const imageUrl = URL.createObjectURL(imageBlob)
  return new Blob([bytes], {type: 'mime'})
}

export const base64ToFile = (baseUrl: any, filename = 'file') => {
  const arr = baseUrl.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const suffix = mime.split('/')[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], `${filename}.${suffix}`, {
    type: mime
  })
}
