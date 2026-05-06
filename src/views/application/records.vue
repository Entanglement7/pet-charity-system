<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>申领记录</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无申领记录">
        <el-table-column prop="itemTitle" label="物品名称" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="reason" label="申领理由" min-width="160" show-overflow-tooltip />
        <el-table-column label="申领状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="使用情况" width="110">
          <template slot-scope="scope">
            <template v-if="scope.row.status === 4">
              <el-tag :type="usageStatusType(scope.row.usageStatus)" size="small">{{ usageStatusLabel(scope.row.usageStatus) }}</el-tag>
            </template>
            <span v-else style="color:#ccc;font-size:12px">—</span>
          </template>
        </el-table-column>
        <el-table-column label="申领时间" width="155">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 3" size="mini" type="success" @click="handleConfirm(scope.row)">确认收货</el-button>
            <el-button
              v-else-if="scope.row.status === 4 && canSubmitUsage(scope.row.usageStatus)"
              size="mini"
              type="primary"
              @click="openUsageDialog(scope.row)"
            >填写使用情况</el-button>
            <span v-else style="color:#999;font-size:12px">—</span>
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

    <!-- 填写使用情况弹窗 -->
    <el-dialog title="填写物资使用情况" :visible.sync="usageDialogVisible" width="520px">
      <el-form ref="usageForm" :model="usageForm" :rules="usageRules" label-width="90px">
        <el-form-item label="物品名称">
          <span>{{ currentItem.itemTitle }}</span>
        </el-form-item>
        <el-form-item label="申领数量">
          <span>{{ currentItem.quantity }}</span>
        </el-form-item>
        <el-form-item v-if="currentItem.usageStatus === 3" label="驳回原因">
          <span style="color:#f56c6c">{{ currentItem.usageRejectReason }}</span>
        </el-form-item>
        <el-form-item label="使用说明" prop="report">
          <el-input
            v-model="usageForm.report"
            type="textarea"
            :rows="5"
            placeholder="请详细描述物资的使用情况，例如：用途、受益动物数量、使用时间等"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="usageDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitUsage">提交审核</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getApplicationRecords, confirmApplicationReceipt, submitUsageReport } from '@/api/application'

const STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '审核通过', type: 'success' },
  2: { label: '已驳回', type: 'danger' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'info' }
}

const USAGE_STATUS_MAP = {
  0: { label: '未填写', type: 'info' },
  1: { label: '待审核', type: 'warning' },
  2: { label: '已公示', type: 'success' },
  3: { label: '已驳回', type: 'danger' }
}

export default {
  name: 'ApplicationRecords',
  data() {
    return {
      list: [],
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0,
      usageDialogVisible: false,
      currentItem: {},
      usageForm: { report: '' },
      usageRules: {
        report: [
          { required: true, message: '请填写使用情况说明', trigger: 'blur' },
          { min: 10, message: '说明至少10个字', trigger: 'blur' }
        ]
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
      getApplicationRecords({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    handleConfirm(row) {
      this.$confirm('确认已收到物资？', '确认收货', {
        confirmButtonText: '确认', cancelButtonText: '取消', type: 'success'
      }).then(() => {
        confirmApplicationReceipt(row.id).then(() => {
          this.$message.success('已确认收货，请及时填写物资使用情况')
          this.fetchData()
        })
      }).catch(() => {})
    },
    canSubmitUsage(usageStatus) {
      return usageStatus === 0 || usageStatus === null || usageStatus === undefined || usageStatus === 3
    },
    openUsageDialog(row) {
      this.currentItem = row
      this.usageForm.report = row.usageReport || ''
      this.usageDialogVisible = true
      this.$nextTick(() => { this.$refs.usageForm && this.$refs.usageForm.clearValidate() })
    },
    submitUsage() {
      this.$refs.usageForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        submitUsageReport(this.currentItem.id, this.usageForm.report)
          .then(() => {
            this.$message.success('已提交，等待管理员审核后将在公示页面展示')
            this.usageDialogVisible = false
            this.fetchData()
          })
          .finally(() => { this.submitting = false })
      })
    },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    usageStatusLabel(val) { return (USAGE_STATUS_MAP[val] || USAGE_STATUS_MAP[0]).label },
    usageStatusType(val) { return (USAGE_STATUS_MAP[val] || USAGE_STATUS_MAP[0]).type },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
