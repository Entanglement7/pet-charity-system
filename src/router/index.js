import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/login/register'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  // 捐赠管理（用户角色）
  {
    path: '/donation',
    component: Layout,
    redirect: '/donation/list',
    name: 'Donation',
    meta: {
      title: '捐赠管理',
      icon: 'el-icon-present',
      roles: ['user']
    },
    children: [
      {
        path: 'publish',
        component: () => import('@/views/donation/publish'),
        name: 'PublishDonation',
        meta: { title: '发布捐赠', icon: 'edit' }
      },
      {
        path: 'list',
        component: () => import('@/views/donation/list'),
        name: 'DonationList',
        meta: { title: '我的捐赠', icon: 'list' }
      },
      {
        path: 'detail/:id',
        component: () => import('@/views/donation/detail'),
        name: 'DonationDetail',
        meta: { title: '捐赠详情', noCache: true, activeMenu: '/donation/list' },
        hidden: true
      }
    ]
  },

  // 积分管理（用户角色）
  {
    path: '/points',
    component: Layout,
    redirect: '/points/index',
    name: 'Points',
    meta: {
      title: '积分管理',
      icon: 'el-icon-star-off',
      roles: ['user']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/points/index'),
        name: 'PointsIndex',
        meta: { title: '我的积分', icon: 'star' }
      },
      {
        path: 'detail',
        component: () => import('@/views/points/detail'),
        name: 'PointsDetail',
        meta: { title: '积分明细', icon: 'list' }
      },
      {
        path: 'exchange',
        component: () => import('@/views/points/exchange'),
        name: 'PointsExchange',
        meta: { title: '积分兑换', icon: 'shopping' }
      },
      {
        path: 'records',
        component: () => import('@/views/points/records'),
        name: 'ExchangeRecords',
        meta: { title: '兑换记录', icon: 'documentation' }
      }
    ]
  },

  // 积分管理（管理员角色）
  {
    path: '/points-management',
    component: Layout,
    redirect: '/points-management/list',
    name: 'PointsManagement',
    meta: {
      title: '积分管理',
      icon: 'el-icon-coin',
      roles: ['admin']
    },
    children: [
      {
        path: 'list',
        component: () => import('@/views/points/userList'),
        name: 'UserPointsList',
        meta: { title: '用户积分', icon: 'list' }
      },
      {
        path: 'exchange-items',
        component: () => import('@/views/points/exchangeManage'),
        name: 'ExchangeItemManage',
        meta: { title: '兑换物品管理', icon: 'shopping' }
      }
    ]
  },

  // 机构管理（机构角色）
  {
    path: '/organization',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/organization/index'),
        name: 'OrganizationManage',
        meta: { title: '机构管理', icon: 'el-icon-office-building', roles: ['organization'] }
      }
    ]
  },

  // 机构列表（仅管理员）
  {
    path: '/organization-list',
    component: Layout,
    redirect: '/organization-list/index',
    name: 'OrganizationList',
    meta: {
      title: '机构列表',
      icon: 'el-icon-office-building',
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/organization/list'),
        name: 'OrganizationListIndex',
        meta: { title: '机构列表', icon: 'list' }
      }
    ]
  },

  // 物资申领（机构角色 + 管理员可查看物资列表）
  {
    path: '/application',
    component: Layout,
    redirect: '/application/list',
    name: 'Application',
    meta: {
      title: '物资申领',
      icon: 'el-icon-box',
      roles: ['organization', 'admin']
    },
    children: [
      {
        path: 'list',
        component: () => import('@/views/application/list'),
        name: 'ApplicationList',
        meta: { title: '可申领物资', icon: 'list' }
      },
      {
        path: 'records',
        component: () => import('@/views/application/records'),
        name: 'ApplicationRecords',
        meta: { title: '申领记录', icon: 'documentation', roles: ['organization'] }
      },
      {
        path: 'manage',
        component: () => import('@/views/application/manage'),
        name: 'ApplicationManage',
        meta: { title: '申领管理', icon: 'setting', roles: ['admin'] }
      }
    ]
  },

  // 审核管理（管理员角色）
  {
    path: '/audit',
    component: Layout,
    redirect: '/audit/organization',
    name: 'Audit',
    meta: {
      title: '审核管理',
      icon: 'el-icon-s-check',
      roles: ['admin']
    },
    children: [
      {
        path: 'organization',
        component: () => import('@/views/audit/organization'),
        name: 'OrganizationAudit',
        meta: { title: '机构审核', icon: 'peoples' }
      },
      {
        path: 'donation',
        component: () => import('@/views/audit/donation'),
        name: 'DonationAudit',
        meta: { title: '物品审核', icon: 'shopping' }
      },
      {
        path: 'application',
        component: () => import('@/views/audit/application'),
        name: 'ApplicationAudit',
        meta: { title: '申领审核', icon: 'form' }
      }
    ]
  },

  // 公益公示（所有角色）
  {
    path: '/publicity',
    component: Layout,
    redirect: '/publicity/flow',
    name: 'Publicity',
    meta: {
      title: '公益公示',
      icon: 'el-icon-view'
    },
    children: [
      {
        path: 'flow',
        component: () => import('@/views/publicity/flow'),
        name: 'PublicityFlow',
        meta: { title: '物资流向', icon: 'tree' }
      },
      {
        path: 'records',
        component: () => import('@/views/publicity/records'),
        name: 'PublicityRecords',
        meta: { title: '捐赠记录', icon: 'documentation' }
      }
    ]
  },

  // 数据统计（管理员角色）
  {
    path: '/statistics',
    component: Layout,
    redirect: '/statistics/index',
    name: 'Statistics',
    meta: {
      title: '数据统计',
      icon: 'el-icon-data-line',
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/statistics/index'),
        name: 'StatisticsIndex',
        meta: { title: '统计概览', icon: 'chart' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
