<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>机构信息</span>
        <el-button style="float: right" size="small" type="primary" @click="toggleEdit">
          {{ editMode ? '取消编辑' : '编辑' }}
        </el-button>
      </div>

      <el-alert v-if="orgStatus === 0" title="机构审核中，审核通过后才能申领物资" type="warning" show-icon :closable="false" style="margin-bottom:16px" />
      <el-alert v-if="orgStatus === 2" :title="'审核未通过：' + (rejectReason || '请联系管理员')" type="error" show-icon :closable="false" style="margin-bottom:16px" />

      <el-form ref="form" :model="form" :disabled="!editMode" label-width="120px">
        <el-form-item label="机构名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contact" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="机构简介">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-tag :type="statusTagType">{{ statusTagText }}</el-tag>
        </el-form-item>
        <el-form-item label="资质证明">
          <div v-if="!editMode">
            <img v-if="form.licenseUrl" :src="form.licenseUrl" class="license-preview">
            <span v-else style="color:#ccc">未上传</span>
          </div>
          <div v-else>
            <el-upload
              action="#"
              :show-file-list="false"
              accept="image/*"
              :before-upload="beforeUpload"
              :http-request="handleUpload"
            >
              <img v-if="form.licenseUrl" :src="form.licenseUrl" class="license-preview" style="cursor:pointer">
              <div v-else class="upload-placeholder">
                <i class="el-icon-plus" style="font-size:28px;color:#8c939d" />
                <div style="font-size:12px;color:#8c939d;margin-top:6px">点击上传</div>
              </div>
            </el-upload>
            <div style="color:#909399;font-size:12px;margin-top:4px">点击图片可重新上传，最大 5MB</div>
          </div>
        </el-form-item>
        <el-form-item v-if="editMode">
          <el-button type="primary" :loading="submitting" @click="saveForm">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getOrganizationInfo, updateOrganizationInfo } from '@/api/organization'

export default {
  name: 'OrganizationInfo',
  data() {
    return {
      form: {},
      editMode: false,
      submitting: false,
      orgStatus: 0,
      rejectReason: ''
    }
  },
  computed: {
    statusTagType() {
      return { 0: 'warning', 1: 'success', 2: 'danger' }[this.orgStatus] ?? 'info'
    },
    statusTagText() {
      return { 0: '审核中', 1: '已认证', 2: '未通过' }[this.orgStatus] ?? '未知'
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      getOrganizationInfo().then(res => {
        this.form = res.data || {}
        this.orgStatus = res.data.status ?? 0
        this.rejectReason = res.data.rejectReason || ''
      })
    },
    toggleEdit() {
      this.editMode = !this.editMode
      if (!this.editMode) this.fetchData()
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
      reader.onload = e => { this.form.licenseUrl = e.target.result }
      reader.readAsDataURL(params.file)
    },
    saveForm() {
      this.submitting = true
      updateOrganizationInfo(this.form).then(() => {
        this.$message.success('保存成功')
        this.editMode = false
        this.fetchData()
      }).finally(() => { this.submitting = false })
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
.upload-placeholder:hover { border-color: #409EFF; }
</style>
