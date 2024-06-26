import { ElMessage } from 'element-plus'

export function errorCallback(response: any) {
  console.log(response)
  if (response.status === 200) {
      ElMessage({
        showClose: true,
        message: response.data.message,
        grouping: true,
        type: 'error'
      })
  } else {
    ElMessage({
      showClose: true,
      message: 'Network Error!',
      grouping: true,
      type: 'error'
    })
  }
}
export function errorMessage(msg: string) {
  ElMessage({
    showClose: true,
    message: msg,
    type: 'error'
  })
}
