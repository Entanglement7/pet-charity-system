<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>可申领物资</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无可申领物资">
        <el-table-column prop="title" label="物品名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="110">
          <template slot-scope="scope">
            <el-tag size="small">{{ categoryLabel(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remaining" label="剩余数量" width="100" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="description" label="物品描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="发布时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column v-if="!isAdmin" label="操作" width="90" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleApply(scope.row)">申 领</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="page"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right;"
        @size-change="val => { pageSize = val; fetchData() }"
        @current-change="val => { page = val; fetchData() }"
      />
    </el-card>

    <el-dialog title="申领物资" :visible.sync="dialogVisible" width="500px">
      <el-form ref="applyForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物品名称">
          <span>{{ currentItem.title }}</span>
        </el-form-item>
        <el-form-item label="剩余数量">
          <span>{{ currentItem.remaining }} {{ currentItem.unit }}</span>
        </el-form-item>
        <el-form-item label="申领数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :max="currentItem.remaining" />
        </el-form-item>
        <el-form-item label="申领理由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请说明申领理由及用途" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApply">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAvailableItems, applyForItem } from '@/api/application'

const CATEGORY_MAP = {
  pet_food: '主粮', pet_snack: '零食', pet_toy: '玩具',
  pet_bed: '窝垫', pet_medicine: '药品', pet_care: '洗护',
  pet_clothing: '服装', pet_supplies: '其他用品'
}

export default {
  name: 'ApplicationList',
  data() {
    return {
      list: [],
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      currentItem: {},
      form: { quantity: 1, reason: '' },
      rules: {
        quantity: [{ required: true, message: '请输入申领数量', trigger: 'blur' }],
        reason: [{ required: true, message: '请输入申领理由', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.roles.includes('admin')
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      getAvailableItems({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    handleApply(item) {
      this.currentItem = item
      this.form = { quantity: 1, reason: '' }
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.applyForm && this.$refs.applyForm.clearValidate() })
    },
    submitApply() {
      this.$refs.applyForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        applyForItem({ itemId: this.currentItem.id, quantity: this.form.quantity, reason: this.form.reason })
          .then(() => {
            this.$message.success('申领成功，等待审核')
            this.dialogVisible = false
            this.fetchData()
          })
          .finally(() => { this.submitting = false })
      })
    },
    categoryLabel(val) { return CATEGORY_MAP[val] || val || '—' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
