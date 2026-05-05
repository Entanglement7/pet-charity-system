<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>物品审核（待审核）</span>
      </div>

      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无待审核物品">
        <el-table-column prop="title" label="物品名称" min-width="160" />
        <el-table-column prop="category" label="分类" width="120">
          <template slot-scope="scope">
            <el-tag size="small">{{ categoryLabel(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="description" label="物品描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="scope">
            <div style="white-space:nowrap">
              <el-button size="mini" type="success" @click="openAudit(scope.row, 'approve')">通 过</el-button>
              <el-button size="mini" type="danger" @click="openAudit(scope.row, 'reject')">驳 回</el-button>
            </div>
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

    <!-- 驳回原因弹窗 -->
    <el-dialog title="驳回原因" :visible.sync="rejectDialogVisible" width="500px">
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="90px">
        <el-form-item label="物品名称">
          <span>{{ currentItem.title }}</span>
        </el-form-item>
        <el-form-item label="驳回原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入驳回原因（必填）" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="rejectDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="submitting" @click="submitReject">确认驳回</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getDonationAuditList, auditDonation } from '@/api/audit'

const CATEGORY_MAP = {
  pet_food: '宠物食品',
  pet_medicine: '宠物药品',
  pet_supplies: '宠物用品',
  pet_clothing: '宠物服装',
  pet_toy: '宠物玩具',
  other: '其他'
}

export default {
  name: 'DonationAudit',
  data() {
    return {
      list: [],
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0,
      rejectDialogVisible: false,
      currentItem: {},
      rejectForm: { reason: '' },
      rejectRules: {
        reason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      getDonationAuditList({ status: 0, page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    openAudit(item, action) {
      if (action === 'approve') {
        this.$confirm(`确认通过「${item.title}」的审核？通过后将自动发放积分给捐赠人。`, '审核确认', {
          confirmButtonText: '确认通过',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitApprove(item)
        }).catch(() => {})
      } else {
        this.currentItem = item
        this.rejectForm.reason = ''
        this.rejectDialogVisible = true
        this.$nextTick(() => { this.$refs.rejectForm && this.$refs.rejectForm.clearValidate() })
      }
    },
    submitApprove(item) {
      this.submitting = true
      auditDonation(item.id, { action: 'approve' })
        .then(() => {
          this.$message.success('审核通过，已自动发放积分')
          this.fetchData()
        })
        .finally(() => { this.submitting = false })
    },
    submitReject() {
      this.$refs.rejectForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        auditDonation(this.currentItem.id, { action: 'reject', reason: this.rejectForm.reason })
          .then(() => {
            this.$message.success('已驳回')
            this.rejectDialogVisible = false
            this.fetchData()
          })
          .finally(() => { this.submitting = false })
      })
    },
    categoryLabel(val) {
      return CATEGORY_MAP[val] || val || '—'
    },
    formatTime(val) {
      if (!val) return '—'
      return val.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>
