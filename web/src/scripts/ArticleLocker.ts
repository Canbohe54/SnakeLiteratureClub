import { useStore } from 'vuex'
import { SYNC_GET, SYNC_POST} from "@/scripts/Axios";
import { ElMessage } from "element-plus";
import { errorCallback } from "@/scripts/ErrorCallBack";

const store = useStore()
export const lockArticleById = async (articleId: String, lockedBy: String, expire: number) => {
   let data = {
     article_id: articleId,
     expire: expire,
     locked_by: lockedBy
   }
   await SYNC_POST('/article/lockArticleById', data, (response: any) =>{
     let message = ''
     if(expire > 86400){
       message = `文章已被锁定，锁定时间${expire/86400}天。`
     }else {
       message = `文章已被锁定，锁定时间${expire/3600}小时。`
     }
     if (response.status === 200 && response.data.code === 2001) {
       ElMessage({
         showClose: true,
         message: message,
         type: 'success'
       })
     }else {
       errorCallback(response)
     }
   })
}

export const unlockArticle = async (articleId: String, unlockedByToken: String) => {
  await SYNC_POST('/article/unlockArticleById', { article_id: articleId, unlocked_by: unlockedByToken }, (response: any) =>{
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '文章已解除锁定。',
        type: 'success'
      })
    }else {
      errorCallback(response)
    }
  })
}

