import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pipe-digital-twin',
    name: 'PipeDigitalTwin',
    component: () => import('../views/PipeDigitalTwin.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pipe-digital-twin-full',
    name: 'PipeDigitalTwinFull',
    component: () => import('../views/PipeDigitalTwinFull.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/user/:id',
    name: 'UserDetail',
    component: () => import('../views/UserDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const hasToken = localStorage.getItem('token')

  if (requiresAuth && !hasToken) {
    next('/login')
  } else {
    next()
  }
})

export default router