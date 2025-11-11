import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/market',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/market',
        name: 'Market',
        component: () => import('../views/Market.vue')
      },
      {
        path: '/product/publish',
        name: 'PublishProduct',
        component: () => import('../views/ProductPublish.vue')
      },
      {
        path: '/cart',
        name: 'Cart',
        component: () => import('../views/Cart.vue')
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue')
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { requiresPermission: 'user:list' }
      },
      {
        path: '/roles',
        name: 'Roles',
        component: () => import('../views/Roles.vue'),
        meta: { requiresPermission: 'role:list' }
      },
      {
        path: '/permissions',
        name: 'Permissions',
        component: () => import('../views/Permissions.vue'),
        meta: { requiresPermission: 'permission:list' }
      },
      {
        path: '/menus',
        name: 'Menus',
        component: () => import('../views/Menus.vue'),
        meta: { requiresPermission: 'menu:list' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = store.state.auth.token
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
