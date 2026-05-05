<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>机构注册</span>
      </div>

      <!-- 已注册：显示审核状态 -->
      <div v-if="registered">
        <el-result
          :icon="statusIcon"
          :title="statusTitle"
          :sub-title="statusSubTitle"
        >
          <template slot="extra">
            <el-button v-if="orgStatus === 2" type="primary" @click="registered = false">重新提交</el-button>
          </template>
        </el-result>
        <div v-if="orgStatus === 2 && rejectReason" style="text-align:center;color:#F56C6C;margin-top:-20px;margin-bottom:20px">
          驳回原因：{{ rejectReason }}
        </div>
      </div>

      <!-- 未注册或重新提交 -->
      <el-form v-else ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="机构名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="机构简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入机构简介" />
        </el-form-item>
        <el-form-item label="资质证明" prop="licenseUrl">
          <el-upload
            action="#"
            :show-file-list="false"
            accept="image/*"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="form.licenseUrl" :src="form.licenseUrl" class="license-preview">
            <div v-else class="upload-placeholder">
              <i class="el-icon-plus" style="font-size:28px;color:#8c939d" />
              <div style="font-size:12px;color:#8c939d;margin-top:6px">点击上传</div>
            </div>
          </el-upload>
          <div style="color:#909399;font-size:12px;margin-top:4px">请上传营业执照或资质证书图片，最大 5MB</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm">提交注册</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { registerOrganization, getOrganizationInfo } from '@/api/organization'

export default {
  name: 'OrganizationRegister',
  data() {
    return {
      registered: false,
      orgStatus: 0,
      rejectReason: '',
      submitting: false,
      form: {
        name: '',
        contact: '',
        phone: '',
        email: '',
        description: '',
        licenseUrl: ''
      },
      rules: {
        name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
        contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        description: [{ required: true, message: '请输入机构简介', trigger: 'blur' }],
        licenseUrl: [{ required: true, message: '请上传资质证明', trigger: 'change' }]
      }
    }
  },
  computed: {
    statusIcon() {
      return { 0: 'warning', 1: 'success', 2: 'error' }[this.orgStatus] || 'warning'
    },
    statusTitle() {
      return { 0: '审核中', 1: '审核通过', 2: '审核未通过' }[this.orgStatus] || '审核中'
    },
    statusSubTitle() {
      return { 0: '您的机构申请已提交，请等待管理员审核', 1: '恭喜，您的机构已通过审核，可以正常使用各项功能', 2: '您的申请未通过审核，请修改后重新提交' }[this.orgStatus] || ''
    }
  },
  created() {
    this.checkRegistered()
  },
  methods: {
    checkRegistered() {
      getOrganizationInfo().then(res => {
        if (res.data) {
          this.registered = true
          this.orgStatus = res.data.status
          this.rejectReason = res.data.rejectReason || ''
        }
      }).catch(() => {
        // 未注册，显示表单
        this.registered = false
      })
    },
    beforeUpload(file) {
      if (!file.type.startsWith('image/')) {
        this.$message.error('只能上传图片文件')
        return false
      }
      if (file.size / 1024 / 1024 > 5) {
        this.$message.error('图片不能超过 5MB')
        return false
      }
      return true
    },
    handleUpload(params) {
      const reader = new FileReader()
      reader.onload = e => {
        this.form.licenseUrl = e.target.result
        this.$refs.form.validateField('licenseUrl')
      }
      reader.readAsDataURL(params.file)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        registerOrganization(this.form).then(() => {
          this.$message.success('注册成功，等待审核')
          this.checkRegistered()
        }).finally(() => { this.submitting = false })
      })
    },
    resetForm() {
      this.$refs.form.resetFields()
      this.form.licenseUrl = ''
    }
  }
}
</script>

<style scoped>
.license-preview {
  width: 240px;
  max-height: 160px;
  object-fit: contain;
  border-radius: 4px;
  display: block;
  border: 1px solid #e8e8e8;
}
.upload-placeholder {
  width: 240px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.upload-placeholder:hover {
  border-color: #409EFF;
}
</style>
