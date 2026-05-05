
const tokens = {
  admin: {
    token: 'admin-token'
  },
  user: {
    token: 'user-token'
  },
  organization: {
    token: 'organization-token'
  }
}

const users = {
  'admin-token': {
    roles: ['admin'],
    introduction: '我是系统管理员',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: '管理员'
  },
  'user-token': {
    roles: ['user'],
    introduction: '我是爱心用户',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: '爱心用户'
  },
  'organization-token': {
    roles: ['organization'],
    introduction: '我是机构管理员',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: '机构管理员'
  }
}

module.exports = [
  // user login
  {
    url: '/vue-element-admin/user/login',
    type: 'post',
    response: config => {
      const { username } = config.body
      const token = tokens[username]

      // mock error
      if (!token) {
        return {
          code: 60204,
          message: '账号或密码不正确'
        }
      }

      return {
        code: 20000,
        data: token
      }
    }
  },

  // get user info
  {
    url: '/vue-element-admin/user/info\.*',
    type: 'get',
    response: config => {
      const { token } = config.query
      const info = users[token]

      // mock error
      if (!info) {
        return {
          code: 50008,
          message: '登录失败，无法获取用户详情'
        }
      }

      return {
        code: 20000,
        data: info
      }
    }
  },

  // user logout
  {
    url: '/vue-element-admin/user/logout',
    type: 'post',
    response: _ => {
      return {
        code: 20000,
        data: 'success'
      }
    }
  }
]
