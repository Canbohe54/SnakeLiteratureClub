<template>
    <el-row>
        <el-col :md="12" :sm="24" v-for="articleInfo in articleList" v-if="is_card">
            <el-card class="article-el-card" body-style="padding:0;">
                <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible"
                    :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible" />
            </el-card>
        </el-col>
        <el-col :span="24" v-if="!is_card" v-for="articleInfo in articleList">
            <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible"
                :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible" :viewcountVisible="currentSettings.viewcountVisible"
                class="not-card-info" />
        </el-col>
        <el-col :span="24" v-if="articleList.length === 0">
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

type Option = 'LOBBY' | 'USER'
type AuditStatus = 'ROUGH' | 'SUBMITTED' | 'FAIL_AUDITED' | 'BEING_AUDITED' | 'AUDITED' | 'LOCKED'
type PublishStatus = 'PUBLIC' | 'UNDER_REVIEW' | 'UNDER_RECORD' | 'POST_RECORD' | 'POSTED' | 'FAILED_REVIEW' | 'FAIL_RECORD'

const props = defineProps({
    option: {
        type: Object as () => Option,
        default: 'LOBBY'
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
        default: [
            {
                articleId: 'a3F8c0krY3df',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'AUDITED',
                publishStatus: 'POSTED',
                time: '1919年8月10日',
                received_by: '下北泽日报',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
            {
                articleId: 'a3',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'AUDITED',
                publishStatus: 'PUBLIC',
                time: '1919年8月10日',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
            {
                articleId: 'a4',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'AUDITED',
                publishStatus: 'UNDER_REVIEW',
                time: '1919年8月10日',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
            {
                articleId: 'a5',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'LOCK',
                publishStatus: 'UNDER_REVIEW',
                time: '1919年8月10日',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
            {
                articleId: 'a6',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'SUBMITTED',
                time: '1919年8月10日',
                audit_by: '',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
            {
                articleId: 'a7',
                title: '关于沼气动力学的研究',
                contributor: '田所浩二',
                userId: '114514',
                organization: '下北泽中学',
                grade: '五年级',
                mentor: '野兽先辈',
                description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
                auditStatus: 'FAIL_AUDITED',
                time: '1919年8月10日',
                audit_by: '114514',
                tags: {'体裁': ['生物制沼'], '题材': ['沼气动力学']}
            },
        ]
    }
})

const { option, mode, is_card, articleList } = toRefs(props)
const route = useRoute()
const _articleList = ref({});

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

onMounted(() => {
    Object.assign(currentSettings, modeSettings[props.mode])
})

const pageInfo = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

interface ArticleInfoRequest {
    idList: string[],
    authorList: string[],
    keyword: string,
    tags: string,
    auditStatusList: AuditStatus[],
    publishStatusList: PublishStatus[]
}

interface UrlDecodedArticleInfoRequest {
    idList: string,
    authorList: string,
    keyword: string,
    tags: string,
    auditStatusList: string,
    publishStatusList: string
}

function formRequestParams(option?: Option): ArticleInfoRequest | UrlDecodedArticleInfoRequest {
    option = (option == undefined ? props.option : option)
    let articleInfoRequest: ArticleInfoRequest
    switch (option) {
        case 'LOBBY': {
            articleInfoRequest = {
                idList: [],
                authorList: [],
                keyword: '',
                tags: '',
                auditStatusList: ['AUDITED'],
                publishStatusList: []
            }
            break
        }
        case 'USER': {
            articleInfoRequest = {
                idList: [],
                authorList: ['114514'],
                keyword: 'Article',
                tags: '',
                auditStatusList: [],
                publishStatusList: ['UNDER_RECORD']
            }
        }
    }
    return requestParamsDecode(articleInfoRequest)
}

function requestParamsDecode(requestParam: ArticleInfoRequest): UrlDecodedArticleInfoRequest {
    return {
        idList: requestParam.idList + '',
        authorList: requestParam.authorList + '',
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
        }
    })
}

await getRank()
getArticles()
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
