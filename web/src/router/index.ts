import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
// import HomeView from '../views/HomeView.vue'
import LobbyView from '../views/LobbyView.vue'
import { useStore } from 'vuex'
const store = useStore() // 已连接vuex，可设置路由拦截

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
    path: '/user',
    name: 'user-center',
    component: () => import('../views/UserCenterView.vue')
  },
  {
    path: '/user/:id',
    name: 'user-info',
    component: () => import('../views/UserCenterView.vue')
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
          }
        ]
      }
    ]
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('../views/SearchView.vue')
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
