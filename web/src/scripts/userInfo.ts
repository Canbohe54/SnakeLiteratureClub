import { ElMessage } from "element-plus"
import { SYNC_GET } from "./Axios"

export async function toUserPage (id: string) {
    if (id === '' || id === undefined) {
        ElMessage.error('用户不存在')
        return
    }
    await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: id
      }, response => {
        if(response.status === 200 && response.data.code === 2001){
            window.location.href = '/#/user/' + id
        }else {
            ElMessage.error('用户不存在')
        }
      })
      return
  }
