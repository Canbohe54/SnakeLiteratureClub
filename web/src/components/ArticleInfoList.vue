<template>
    <el-row>
        <el-col :md="12" :sm="24" v-for="articleInfo in _articleList" v-if="is_card">
            <el-card class="article-el-card" body-style="padding:0;">
                <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible"
                    :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible" :menuVisible="currentSettings.menuVisible" />
            </el-card>
        </el-col>
        <el-col :span="24" v-if="!is_card" v-for="articleInfo in _articleList">
            <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible"
                :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible" :viewcountVisible="currentSettings.viewcountVisible"
                class="not-card-info" />
        </el-col>
        <el-col :span="24" v-if="_articleList.length === 0">
            <el-empty description="暂无文章" />
        </el-col>
    </el-row>
    <el-pagination class="article_list_pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total">
    </el-pagination>
</template>

<script setup lang="ts">
import ArticleInfo from '@/components/article/ArticleInfo.vue';
import { onMounted, reactive, ref, toRefs } from 'vue';
import { SYNC_GET } from "@/scripts/Axios";
import { useRoute } from "vue-router";
import { PageInfo, SnachResponse } from "@/scripts/types/ResponseObject";
import { AttributeAddableObject } from "@/scripts/ArticleTagFilter";
import { Article } from "@/scripts/types/models";
import {errorCallback} from "@/scripts/ErrorCallBack";

type Option = 'STATIC' | 'LOBBY' | 'SEARCH' | 'USER_PUBLIC_LIST' | 'USER_PRIVATE_LIST' | 'AUDIT_LIST'  | 'RECEIVING' | 'RECEIVED' | 'FAILED_REVIEW' | 'POST_RECORD' | 'BEING_AUDITED' | 'FAIL_AUDITED'
type AuditStatus = 'ROUGH' | 'SUBMITTED' | 'FAIL_AUDITED' | 'BEING_AUDITED' | 'AUDITED' | 'LOCKED'
type PublishStatus = 'PUBLIC' | 'UNDER_REVIEW' | 'UNDER_RECORD' | 'POST_RECORD' | 'POSTED' | 'FAILED_REVIEW' | 'FAILED_RECORD'

const props = defineProps({
    option: {
        type: Object as () => Option,
        default: 'LOBBY'
    },
    tags: {
        type: Object as () => AttributeAddableObject,
        default: ''
    },
    wd: {
        type: String,
        default: ''
    },
    mode: {
        type: String,
        default: 'USER_OWN'
    },
    is_card: {
        type: Boolean,
        default: true
    },
    articleList: {
        type: Array<Article>,
        default: []
    }
})

const { option, tags, wd, mode, is_card, articleList } = toRefs(props)
const route = useRoute()
const _articleList = ref<Article[]>([]);

async function getRank() {
  let params = {
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
  }
  await SYNC_GET('/like/getRank', params, async (response) => {
    if (response.status === 200 && response.data.message === 'Success.') {
      pageInfo.currentPage = response.data.data.RankingByLikeAndViewCount.pageNum
      pageInfo.pageSize = response.data.data.RankingByLikeAndViewCount.pageSize
      pageInfo.total = response.data.data.RankingByLikeAndViewCount.total
      _articleList.value = response.data.data.RankingByLikeAndViewCount.list
      let articleLikeAndViewCountMap = response.data.data.articleLikeAndViewCountMap
      for (let article in _articleList.value) {
        _articleList.value[article].viewcount = articleLikeAndViewCountMap[_articleList.value[article].id].viewCount
      }
        console.log(response.data.data.RankingByLikeAndViewCount.list)
      console.log(_articleList.value)
    } else {
      errorCallback(response)
    }
  })
}

const currentSettings = reactive({
    statusVisible: true,
    iconVisible: true,
    tagsVisible: true,
    viewcountVisible: true,
    is_card: true,
    menuVisible: true
})

const modeSettings: AttributeAddableObject = {
    USER_PUBLIC_LIST: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true,
        menuVisible: false
    },
    USER_PRIVATE_LIST: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: false,
        is_card: false
    },
    AUDIT_LIST: {
        statusVisible: true,
        iconVisible: false,
        tagsVisible: true,
        viewcountVisible: false,
        is_card: false
    },
    LOBBY: {
        statusVisible: true,
        iconVisible: false,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
    },
    RECEIVING: {
        statusVisible: false,
        iconVisible: false,
        tagsVisible: true,
        viewcountVisible: false,
        is_card: false
    },
    FAILED_REVIEW: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
    },
    POST_RECORD: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
    },
    BEING_AUDITED: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
    },
    FAIL_AUDITED: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
    }
}

const pageInfo = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

interface ArticleInfoRequest {
    idList: string[],
    authorList: string[],
    receiverList: string[],
    auditorList: string[],
    keyword: string,
    tags: string,
    auditStatusList: AuditStatus[],
    publishStatusList: PublishStatus[]
}

interface UrlDecodedArticleInfoRequest {
    idList: string,
    authorList: string,
    receiverList: string,
    auditorList: string,
    keyword: string,
    tags: string,
    auditStatusList: string,
    publishStatusList: string
}

function formRequestParams(option?: Option): ArticleInfoRequest | UrlDecodedArticleInfoRequest {
    option = (option == undefined ? props.option : option)
    let articleInfoRequest: ArticleInfoRequest
    switch (option) {
        case 'STATIC':
            return
        case 'LOBBY': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: []
            }
            break
        }
        case 'USER_PUBLIC_LIST': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: ['PUBLIC', 'POSTED']
            }
            break
        }
        case 'SEARCH': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [],
                auditorList: [],
                keyword: wd.value,
                tags: tags.value,
                auditStatusList: ['AUDITED'],
                publishStatusList: ['POSTED', 'PUBLIC']
            }
            break
        }
        case 'AUDIT_LIST': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [],
                auditorList: [route.params.id],
                keyword: '',
                tags: '',
                auditStatusList: ['BEING_AUDITED'],
                publishStatusList: []
            }
            break
        }
        case 'RECEIVING': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [route.params.id],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: ['UNDER_RECORD','UNDER_REVIEW','POST_RECORD']
              }
              break
        }
        case 'RECEIVED': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [route.params.id],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: ['POSTED']
              }
              break
        }
        case 'USER_PRIVATE_LIST': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['ROUGH'],
                publishStatusList: []
            }
            break
        }
        case 'FAILED_REVIEW': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: ['FAILED_REVIEW']
            }
            break
        }
        case 'POST_RECORD': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: ['POST_RECORD']
              }
              break
        }
        case 'BEING_AUDITED': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['BEING_AUDITED','SUBMITTED'],
                publishStatusList: []
            }
            break
        }
        case 'FAIL_AUDITED': {
            articleInfoRequest = {
                idList: [],
                authorList: [route.params.id],
                receiverList: [],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['FAIL_AUDITED'],
                publishStatusList: []
            }
            break
        }
    }
    return requestParamsDecode(articleInfoRequest)
}

function requestParamsDecode(requestParam: ArticleInfoRequest): UrlDecodedArticleInfoRequest {
    return {
        idList: requestParam.idList + '',
        authorList: requestParam.authorList + '',
        receiverList: requestParam.receiverList + '',
        auditorList: requestParam.auditorList + '',
        keyword: requestParam.keyword + '',
        tags: requestParam.tags + '',
        auditStatusList: requestParam.auditStatusList + '',
        publishStatusList: requestParam.publishStatusList + '',
    }
}

function getArticles() {
    let params = formRequestParams()
    Object.assign(params, {
        pageNum: pageInfo.currentPage,
        pageSize: pageInfo.pageSize
    })
    $.post({
        url: 'http://localhost:19198/article/getArticles',
        async: false,
        enctype: 'multipart/form-data',
        data: params,
        success: (data: SnachResponse<PageInfo<Article>>) => {
            // TODO: on success
            // console.log(data)
            pageInfo.total = data.data.total
            // pageInfo.currentPage = data.data.pageNum
            // pageInfo.pageSize = data.data.pageSize
            _articleList.value = data.data.list
        }
    })
}

// 监听 page size 改变的事件
function handleSizeChange(newSize: any) {
  pageInfo.pageSize = newSize
  if ( props.mode === 'STATIC' ){
    getRank()
    return
  }
  getArticles()
}

// 监听 页码值 改变的事件
function handleCurrentChange(newPage: any) {
  pageInfo.currentPage = newPage
  // getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
  if ( props.mode === 'STATIC' ){
    getRank()
    return
  }
  getArticles()
}

onMounted(() => {
    Object.assign(currentSettings, modeSettings[props.mode])
    if ( props.mode === 'STATIC' ){
      getRank()
      return
    }
    if (articleList.value.length > 0) {
        _articleList.value = articleList.value
    } else {
        getArticles()
    }
})
</script>

<style scoped>
.article-el-card {
    margin: 10px;
    border-radius: 10px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.article-el-card:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.2);
}

.not-card-info {
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;
}

.not-card-info:hover {
    background-color: #fafafa;
}

.article_list_pagination {
    margin: 20px 0 10px 0;
    display: flex;
    justify-content: center;
}
</style>
