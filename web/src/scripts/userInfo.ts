import { ElMessage } from "element-plus"
import { useRouter } from "vue-router"
import { SYNC_GET } from "./Axios"
const router = useRouter()

export async function toUserPage (id: string) {
    if (id === '' || id === undefined) {
        ElMessage.error('用户不存在')
        return
    }
    await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: id
      }, response => {
        if(response.status === 200 && response.data.statusMsg === 'Success.'){
            router.push(`/user/${id}/article`)
            return
        }
      })
      ElMessage.error('用户不存在')
      return
  }