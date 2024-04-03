<template>
    <el-row>
        <el-col :md="12" :sm="24" v-for="articleInfo in _articleList" v-if="is_card">
            <el-card class="article-el-card" body-style="padding:0;">
                <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible"
                    :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible" />
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
import { SnachResponse } from "@/scripts/types/ResponseObject";
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'

type Option = 'STATIC' | 'LOBBY' | 'SEARCH' | 'USER_PUBLIC_LIST' | 'AUDIT_LIST' | 'RECEIVED'
type AuditStatus = 'ROUGH' | 'SUBMITTED' | 'FAIL_AUDITED' | 'BEING_AUDITED' | 'AUDITED' | 'LOCKED'
type PublishStatus = 'PUBLIC' | 'UNDER_REVIEW' | 'UNDER_RECORD' | 'POST_RECORD' | 'POSTED' | 'FAILED_REVIEW' | 'FAIL_RECORD'

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
        type: Array,
        default: []
    }
})

const { option, tags, wd, mode, is_card, articleList } = toRefs(props)
const route = useRoute()
const _articleList = ref([]);

async function getRank() {
  let params = {
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
  }
  await (SYNC_GET('/like/getRank', params, async (response) => {
    if (response.status === 200 && response.data.message === 'Success.') {
      console.log(response.data.data.RankingByLikeAndViewCount.list)
      pageInfo.total = response.data.data.total
      // todo
      articleList.value = response.data.data.RankingByLikeAndViewCount.list
    } else {
      console.log(response)
    }
  }))
}

const currentSettings = reactive({
    statusVisible: true,
    iconVisible: true,
    tagsVisible: true,
    viewcountVisible: true,
    is_card: true
})

const modeSettings = {
    USER_PUBLIC_LIST: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true,
        viewcountVisible: true,
        is_card: true
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
    RECEIVED: {
        statusVisible: false,
        iconVisible: false,
        tagsVisible: true,
        viewcountVisible: false,
        is_card: false
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
        case 'RECEIVED': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                receiverList: [route.params.id],
                auditorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: []
              }
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
    console.log(params)
    $.post({
        url: 'http://localhost:19198/article/getArticles',
        async: false,
        enctype: 'multipart/form-data',
        data: params,
        success: (data: SnachResponse<object>) => {
            // TODO: on success
            console.log(data)
            pageInfo.total = data.data.total
            pageInfo.currentPage = data.data.pageNum
            pageInfo.pageSize = data.data.pageSize
            _articleList.value = data.data.list
        }
    })
}

// 监听 page size 改变的事件
function handleSizeChange(newSize: any) {
  pageInfo.pageSize = newSize
  getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
}

// 监听 页码值 改变的事件
function handleCurrentChange(newPage: any) {
  pageInfo.currentPage = newPage
  getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
}

onMounted(() => {
    Object.assign(currentSettings, modeSettings[props.mode])
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
