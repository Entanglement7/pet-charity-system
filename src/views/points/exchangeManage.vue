<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>可兑换物品管理</span>
        <el-button style="float:right" type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增物品</el-button>
      </div>
      <el-table v-loading="loading" :data="list" style="width:100%" empty-text="暂无数据">
        <el-table-column label="图片" width="80">
          <template slot-scope="scope">
            <img v-if="scope.row.image" :src="scope.row.image" style="width:48px;height:48px;object-fit:cover;border-radius:4px;">
            <span v-else style="color:#ccc;font-size:12px">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="物品名称" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="pointsCost" label="所需积分" width="100">
          <template slot-scope="scope">
            <span style="color:#409EFF;font-weight:bold">{{ scope.row.pointsCost }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="page"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top:20px;text-align:right;"
        @size-change="val => { pageSize = val; fetchData() }"
        @current-change="val => { page = val; fetchData() }"
      />
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
      <el-form ref="itemForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="物品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="所需积分" prop="pointsCost">
          <el-input-number v-model="form.pointsCost" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :max="99999" />
        </el-form-item>
        <el-form-item label="物品图片">
          <el-upload
            action="#"
            :show-file-list="false"
            accept="image/*"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="form.image" :src="form.image" class="preview-img">
            <div v-else class="upload-placeholder">
              <i class="el-icon-plus" style="font-size:28px;color:#8c939d" />
              <div style="font-size:12px;color:#8c939d;margin-top:6px">点击上传</div>
            </div>
          </el-upload>
          <div style="color:#909399;font-size:12px;margin-top:4px">支持 jpg/png/gif，建议尺寸 300×200，最大 2MB</div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { addExchangeItem, updateExchangeItem, deleteExchangeItem, getExchangeList } from '@/api/points'

export default {
  name: 'ExchangeItemManage',
  data() {
    return {
      list: [], loading: false, page: 1, pageSize: 10, total: 0,
      dialogVisible: false, dialogTitle: '', submitting: false,
      editId: null,
      form: { name: '', description: '', pointsCost: 100, stock: 10, image: '' },
      rules: {
        name: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
        pointsCost: [{ required: true, message: '请输入所需积分', trigger: 'blur' }],
        stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
      }
    }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getExchangeList({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) { this.$message.error('只能上传图片文件'); return false }
      if (!isLt2M) { this.$message.error('图片不能超过 2MB'); return false }
      return true
    },
    handleUpload(params) {
      const reader = new FileReader()
      reader.onload = e => { this.form.image = e.target.result }
      reader.readAsDataURL(params.file)
    },
    handleAdd() {
      this.editId = null
      this.form = { name: '', description: '', pointsCost: 100, stock: 10, image: '' }
      this.dialogTitle = '新增兑换物品'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.itemForm && this.$refs.itemForm.clearValidate() })
    },
    handleEdit(row) {
      this.editId = row.id
      this.form = { name: row.name, description: row.description || '', pointsCost: row.pointsCost, stock: row.stock, image: row.image || '' }
      this.dialogTitle = '编辑兑换物品'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.itemForm && this.$refs.itemForm.clearValidate() })
    },
    handleDelete(row) {
      this.$confirm(`确定删除「${row.name}」吗？`, '提示', {
        type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
      }).then(() => {
        deleteExchangeItem(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchData()
        })
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.itemForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        const req = this.editId
          ? updateExchangeItem(this.editId, this.form)
          : addExchangeItem(this.form)
        req.then(() => {
          this.$message.success(this.editId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.fetchData()
        }).finally(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
.preview-img {
  width: 148px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}
.upload-placeholder {
  width: 148px;
  height: 100px;
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
