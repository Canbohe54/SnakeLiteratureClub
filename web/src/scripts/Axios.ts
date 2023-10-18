import axios, { AxiosResponse } from 'axios'

axios.defaults.baseURL = 'http://127.0.0.1:19198'

export function GET (url: string, headers: object = {}, params: object = {}, onReady?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  axios({
    url: url,
    method: 'GET',
    headers: headers,
    params: params
  }).then(function (response: AxiosResponse) {
    onReady?.(response)
  }).catch(function (error: any) {
    onError?.(error)
  })
}

export function POST (url: string, headers: object = {}, data: object = {}, callback?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  axios({
    url: url,
    method: 'POST',
    headers: headers,
    data: data
  }).then(function (response: AxiosResponse) {
    callback?.(response)
  }).catch(function (error: any) {
    console.log(error)
  })
}

export function $POST (url: string, headers: object = {}, data: object = {}, callback?: (response: AxiosResponse) => void, onError?: (error?: any) => void) {
  axios.post(url, data).then(function (response: AxiosResponse) {
    callback?.(response)
  }).catch(function (error: any) {
    console.log(error)
  })
}
