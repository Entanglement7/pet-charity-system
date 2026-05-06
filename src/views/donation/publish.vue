<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>发布捐赠物品</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width:600px">
        <el-form-item label="物品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="物品分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择物品分类" style="width:100%">
            <el-option label="主粮" value="pet_food" />
            <el-option label="零食" value="pet_snack" />
            <el-option label="玩具" value="pet_toy" />
            <el-option label="窝垫" value="pet_bed" />
            <el-option label="药品" value="pet_medicine" />
            <el-option label="洗护" value="pet_care" />
            <el-option label="服装" value="pet_clothing" />
            <el-option label="其他用品" value="pet_supplies" />
          </el-select>
        </el-form-item>
        <el-form-item label="成色" prop="conditionLevel">
          <el-select v-model="form.conditionLevel" placeholder="请选择成色" style="width:100%">
            <el-option label="全新" value="new" />
            <el-option label="九成新" value="like_new" />
            <el-option label="八成新" value="good" />
            <el-option label="七成新" value="fair" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：件、袋、盒、kg" style="width:160px" />
        </el-form-item>
        <el-form-item label="物品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述物品的详细信息，如品牌、新旧程度等" />
        </el-form-item>
        <el-form-item label="物品图片">
          <el-upload
            :auto-upload="false"
            :limit="3"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            list-type="picture-card"
            accept="image/*"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="el-upload__tip">最多上传3张图片，支持jpg、png格式</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm">提 交</el-button>
          <el-button @click="$refs.form.resetFields()">重 置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { publishDonation } from '@/api/donation'

export default {
  name: 'PublishDonation',
  data() {
    return {
      form: {
        title: '',
        category: '',
        conditionLevel: 'new',
        quantity: 1,
        unit: '',
        description: ''
      },
      rules: {
        title: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择物品分类', trigger: 'change' }],
        conditionLevel: [{ required: true, message: '请选择成色', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
        description: [{ required: true, message: '请输入物品描述', trigger: 'blur' }]
      },
      submitting: false,
      fileList: [],
      imageBase64List: []
    }
  },
  methods: {
    handleFileChange(file, fileList) {
      const isImage = file.raw.type.startsWith('image/')
      const isLt5M = file.raw.size / 1024 / 1024 < 5
      if (!isImage) {
        this.$message.error('只能上传图片文件')
        fileList.pop()
        return
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB')
        fileList.pop()
        return
      }
      const reader = new FileReader()
      reader.onload = (e) => {
        this.imageBase64List.push(e.target.result)
      }
      reader.readAsDataURL(file.raw)
    },
    handleFileRemove(file, fileList) {
      const index = this.fileList.indexOf(file)
      if (index > -1) {
        this.imageBase64List.splice(index, 1)
      }
      this.fileList = fileList
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        const data = { ...this.form }
        if (this.imageBase64List.length > 0) {
          data.images = JSON.stringify(this.imageBase64List)
        }
        publishDonation(data)
          .then(() => {
            this.$message.success('发布成功，等待审核')
            this.$router.push('/donation/list')
          })
          .finally(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style>
