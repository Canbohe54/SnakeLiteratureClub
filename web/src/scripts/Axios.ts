import axios, {AxiosResponse} from 'axios'

axios.defaults.baseURL = 'http://127.0.0.1:19198'

export function GET(url: string, params: object = {}, onReady?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  axios({
    url: url,
    method: 'GET',
    headers: {'Content-Type': 'multipart/form-data'},
    params: params
  }).then(function (response: AxiosResponse) {
    onReady?.(response)
  }).catch(function (error: any) {
    onError?.(error)
  })
}

export function POST(url: string, data: object = {}, onReady?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  axios({
    url: url,
    method: 'POST',
    headers: {'Content-Type': 'multipart/form-data'},
    data: data
  }).then(function (response: AxiosResponse) {
    onReady?.(response)
  }).catch(function (error: any) {
    onError?.(error)
  })
}

export async function SYNC_GET(url: string, params: object = {}, onReady?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  await (
    axios({
      url: url,
      method: 'GET',
      headers: {'Content-Type': 'multipart/form-data'},
      params: params
    }).then(function (response: AxiosResponse) {
      onReady?.(response)
    }).catch(function (error: any) {
      onError?.(error)
    })
  )
}

export async function SYNC_POST(url: string, data: object = {}, onReady?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  await (
    axios({
      url: url,
      method: 'POST',
      headers: {'Content-Type': 'multipart/form-data'},
      data: data
    }).then(function (response: AxiosResponse) {
      onReady?.(response)
    }).catch(function (error: any) {
      onError?.(error)
    })
  )
}
