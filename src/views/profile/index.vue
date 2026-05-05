<template>
  <div class="app-container">
    <el-row :gutter="20" type="flex" justify="center">
      <el-col :span="12" :xs="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>

          <div class="user-profile">
            <div class="user-avatar-section">
              <div class="avatar-wrapper">
                <img :src="userInfo.avatar" class="user-avatar">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :http-request="handleAvatarUpload"
                >
                  <el-button size="small" type="text" class="change-avatar-btn">
                    <i class="el-icon-camera" /> 修改头像
                  </el-button>
                </el-upload>
              </div>
              <h3>{{ userInfo.name }}</h3>
              <p class="user-role">{{ roleText }}</p>
            </div>

            <el-divider />

            <el-form ref="form" :model="userInfo" :rules="rules" label-width="100px">
              <el-form-item label="用户名" prop="name">
                <el-input v-model="userInfo.name" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="角色">
                <el-input v-model="roleText" disabled />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSave">保存</el-button>
                <el-button @click="handleReset">重置</el-button>
                <el-button @click="handleLogout">退出登录</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Profile',
  data() {
    const validateEmail = (rule, value, callback) => {
      if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }
    const validatePhone = (rule, value, callback) => {
      if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    return {
      userInfo: {
        name: '',
        email: '',
        phone: '',
        avatar: ''
      },
      originalInfo: {},
      rules: {
        name: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles'
    ]),
    roleText() {
      const roleMap = {
        admin: '系统管理员',
        user: '爱心用户',
        organization: '机构管理员'
      }
      return this.roles.map(role => roleMap[role] || role).join(' | ')
    }
  },
  created() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo() {
      this.userInfo = {
        name: this.name,
        email: '',
        phone: '',
        avatar: this.avatar
      }
      this.originalInfo = { ...this.userInfo }
    },
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    handleAvatarUpload(params) {
      const file = params.file
      const reader = new FileReader()
      reader.onload = (e) => {
        this.userInfo.avatar = e.target.result
        this.$store.commit('user/SET_AVATAR', e.target.result)
        this.$message.success('头像上传成功')
      }
      reader.readAsDataURL(file)
    },
    handleSave() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // TODO: 调用 API 保存用户信息
          this.$store.commit('user/SET_NAME', this.userInfo.name)
          this.$store.commit('user/SET_AVATAR', this.userInfo.avatar)
          this.originalInfo = { ...this.userInfo }
          this.$message.success('保存成功')
        }
      })
    },
    handleReset() {
      this.userInfo = { ...this.originalInfo }
      this.$refs.form.clearValidate()
      this.$message.info('已重置')
    },
    async handleLogout() {
      await this.$store.dispatch('user/logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.user-profile {
  padding: 20px;
}

.user-avatar-section {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-bottom: 10px;
  display: block;
}

.avatar-uploader {
  text-align: center;
}

.change-avatar-btn {
  color: #409EFF;
  font-size: 14px;
}

.change-avatar-btn:hover {
  color: #66b1ff;
}

.user-avatar-section h3 {
  margin: 10px 0;
  font-size: 24px;
  color: #303133;
}

.user-role {
  color: #909399;
  font-size: 14px;
}
</style>
