import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
// import HomeView from '../views/HomeView.vue'
import LobbyView from '../views/LobbyView.vue'
import store from '../store/index'

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
    path: '/user/:id',
    name: 'user-info',
    component: () => import('../views/UserCenterView.vue'),
    children: [
      {
        path: 'article',
        name: 'user-info-article',
        component: () => import('../components/userCenter/UserArticleDisplay.vue')
      },
      {
        path: 'favor',
        component: () => import('../components/userCenter/FavoriteDisplay.vue')
      },
      {
        path: 'followed',
        component: () => import('../components/userCenter/FollowedDisplay.vue')
      },
      {
        path: '',
        redirect: { name: 'user-info-article' }
      }
    ]
  },
  {
    path: '/user',
    redirect: '/user/' + store.getters.getUserInfo.id + '/article',
    beforeEnter: (to, from, next) => {
      if (store.getters.getUserInfo.id === '' || store.getters.getUserInfo.id === undefined) {
        next('/login')
      } else {
        next('/user/' + store.getters.getUserInfo.id + '/article')
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
        path: 'verify',
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
  {
    path: '/articleDetail',
    name: 'articleDetail',
    component: () => import('../views/ArticleDetailView.vue')
  },
  {
    path: '/grade',
    name: 'grade',
    component: () => import('../views/GradeEditView.vue')
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
