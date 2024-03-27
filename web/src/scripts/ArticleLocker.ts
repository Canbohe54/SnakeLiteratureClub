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
     if (response.status === 200 && response.data.code === 2001) {
       ElMessage({
         showClose: true,
         message: `文章已被锁定，锁定时间${expire/3600}小时。`,
         type: 'success'
       })
     }else {
       errorCallback(response)
     }
   })
}

export const unlockArticle = async (articleId: String, unlockedBy: String) => {
  await SYNC_POST('/article/unlockArticleById', { article_id: articleId, unlocked_by: unlockedBy }, (response: any) =>{
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

