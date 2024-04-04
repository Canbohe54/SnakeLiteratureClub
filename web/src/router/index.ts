import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import LobbyView from '../views/LobbyView.vue'
import store from '../store/index'
import PostedView from '@/views/PostedView.vue'
import PublicView from '@/views/PublicView.vue'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        component: LobbyView
    },
    {
        path: '/lobby',
        name: 'lobby',
        component: LobbyView
    },
    {
        path: '/posted',
        name: 'posted',
        component: PostedView
    },
    {
        path: '/public',
        name: 'public',
        component: PublicView
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/LoginView.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/RegisterView.vue')
    },
    {
        path: '/test',
        name: 'test',
        component: () => import('../components/article/ArticleAnnotate.vue')
    },
    {
        path: '/user/:id',
        name: 'user-info',
        component: () => import('../views/UserCenterView.vue')
    },
    {
        path: '/user',
        redirect: '/user/' + store.getters.getUserInfo.id,
        beforeEnter: (to, from, next) => {
            if (store.getters.getUserInfo.id === '' || store.getters.getUserInfo.id === undefined) {
                next('/login')
            } else {
                next('/user/' + store.getters.getUserInfo.id)
            }
        }
    },
    {
        path: '/account',
        name: 'account-manage',
        component: () => import('../views/AccountManageView.vue'),
        children: [
            {
                path: 'info',
                component: () => import('../components/accountManage/forms/InfoChangeForm.vue')
            },
            {
                path: 'email',
                component: () => import('../views/AccountManage/EmailChangeView.vue'),
                children: [
                    {
                        path: 'info',
                        component: () => import('../components/accountManage/forms/EmailInfo.vue')
                    },
                    {
                        path: 'verify',
                        component: () => import('../components/accountManage/forms/PasswdConfirmForm.vue')
                    },
                    {
                        path: 'change',
                        component: () => import('../components/accountManage/forms/EmailChangeForm.vue')
                    }
                ]
            },
            {
                path: 'password',
                component: () => import('../views/AccountManage/PasswdChangeView.vue'),
                children: [
                    {
                        path: 'verify',
                        component: () => import('../components/accountManage/forms/PasswdConfirmForm.vue')
                    },
                    {
                        path: 'change',
                        component: () => import('../components/accountManage/forms/PasswdChangeForm.vue'),
                        beforeEnter: (to, from, next) => {
                            // TODO: 更详细的拦截规则
                            if (from.path === '/account/password/verify') {
                                next()
                            } else {
                                next(false)
                            }
                        }
                    }
                ]
            },
            {
                path: 'cancel',
                component: () => import('../views/AccountManage/AccountCancelView.vue'),
                children: [
                    {
                        path: 'verify',
                        component: () => import('../components/accountManage/forms/PasswdConfirmForm.vue')
                    },
                    {
                        path: 'confirm',
                        component: () => import('../components/accountManage/forms/EmailConfirmForm.vue')
                    },
                    {
                        path: 'cancel',
                        component: () => import('../components/accountManage/forms/CancelAccForm.vue')
                    }
                ]
            }
        ]
    },
    {
        path: '/forget',
        name: 'forget-passwd',
        component: () => import('../views/ForgetPasswdView.vue'),
        children: [
            {
                path: 'confirm',
                component: () => import('../components/accountManage/forms/EmailConfirmForm.vue')
            },
            {
                path: 'change',
                component: () => import('../components/accountManage/forms/PasswdChangeForm.vue')
            }
        ]
    },
    {
        path: '/search',
        name: 'search',
        component: () => import('../views/SearchView.vue')
    },
    {
        path: '/articleEditor',
        name: 'articleEditor',
        component: () => import('../views/ArticleEditView.vue')
    },
    // {
    //   path: '/test',
    //   name: 'test',
    //   component: () => import('../views/TestView.vue')
    // },
    {
        path: '/articleDetail',
        name: 'articleDetail',
        component: () => import('../views/ArticleDetailView.vue')
    },
    {
        path: '/receivedArticleDetail',
        name: 'receivedArticleDetail',
        component: () => import('../views/receiving/ReceivedArticleDetailView.vue')
    },
    {
        path: '/receiving',
        name: 'receiving',
        component: () => import('../views/receiving/ReceivingView.vue')
    },
    {
        path: '/articleAuditor',
        name: 'articleAuditor',
        component: () => import('../views/ArticleAuditorView.vue')
    },
    {
        path: '/about',
        name: 'about',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
    },
    {
        path: '/404',
        name: 'not-found',
        component: () => import('../views/NotFoundView.vue')
    },
    {
        path: '/:pathmatch(.*)',
        redirect: '/404'
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router
