<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="申领审核" name="application">
        <el-card style="margin-top:10px">
          <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无待审核申领">
            <el-table-column prop="organizationName" label="申领机构" min-width="150" />
            <el-table-column prop="itemTitle" label="物品名称" min-width="150" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="reason" label="申领理由" min-width="200" show-overflow-tooltip />
            <el-table-column label="申领时间" width="160">
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
            @size-change="val => { pageSize = val; fetchApplicationList() }"
            @current-change="val => { page = val; fetchApplicationList() }"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane name="usage">
        <span slot="label">使用情况审核 <el-badge v-if="usagePendingCount > 0" :value="usagePendingCount" /></span>
        <el-card style="margin-top:10px">
          <el-table v-loading="usageLoading" :data="usageList" style="width: 100%" empty-text="暂无待审核的使用情况">
            <el-table-column prop="organizationName" label="机构名称" min-width="140" />
            <el-table-column prop="itemTitle" label="物品名称" min-width="140" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="使用说明" min-width="200">
              <template slot-scope="scope">
                <el-tooltip :content="scope.row.usageReport" placement="top" effect="light">
                  <span style="cursor:pointer">{{ truncate(scope.row.usageReport) }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template slot-scope="scope">
                <el-tag :type="usageStatusType(scope.row.usageStatus)" size="small">{{ usageStatusLabel(scope.row.usageStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template slot-scope="scope">
                <template v-if="scope.row.usageStatus === 1">
                  <el-button size="mini" type="success" @click="openUsageAudit(scope.row, 'approve')">通过公示</el-button>
                  <el-button size="mini" type="danger" @click="openUsageAudit(scope.row, 'reject')">驳 回</el-button>
                </template>
                <span v-else style="color:#999;font-size:12px">已处理</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :current-page="usagePage"
            :page-sizes="[10, 20, 50]"
            :page-size="usagePageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="usageTotal"
            style="margin-top: 20px; text-align: right;"
            @size-change="val => { usagePageSize = val; fetchUsageList() }"
            @current-change="val => { usagePage = val; fetchUsageList() }"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 申领驳回弹窗 -->
    <el-dialog title="驳回原因" :visible.sync="rejectDialogVisible" width="500px">
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="90px">
        <el-form-item label="申领机构"><span>{{ currentItem.organizationName }}</span></el-form-item>
        <el-form-item label="物品名称"><span>{{ currentItem.itemTitle }}</span></el-form-item>
        <el-form-item label="驳回原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入驳回原因（必填）" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="rejectDialogVisible = false">取 消</el-button>
        <el-button type="danger" :loading="submitting" @click="submitReject">确认驳回</el-button>
      </span>
    </el-dialog>

    <!-- 使用情况审核弹窗 -->
    <el-dialog title="使用情况审核" :visible.sync="usageAuditDialogVisible" width="560px">
      <el-form ref="usageAuditForm" :model="usageAuditForm" :rules="usageAuditRules" label-width="90px">
        <el-form-item label="机构名称"><span>{{ currentUsageItem.organizationName }}</span></el-form-item>
        <el-form-item label="物品名称"><span>{{ currentUsageItem.itemTitle }}</span></el-form-item>
        <el-form-item label="使用说明">
          <div style="background:#f5f7fa;padding:10px;border-radius:4px;line-height:1.7;white-space:pre-wrap">{{ currentUsageItem.usageReport }}</div>
        </el-form-item>
        <template v-if="usageAuditAction === 'reject'">
          <el-form-item label="驳回原因" prop="reason">
            <el-input v-model="usageAuditForm.reason" type="textarea" :rows="3" placeholder="请说明驳回原因" />
          </el-form-item>
        </template>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="usageAuditDialogVisible = false">取 消</el-button>
        <el-button v-if="usageAuditAction === 'approve'" type="success" :loading="submitting" @click="submitUsageAudit">通过并公示</el-button>
        <el-button v-else type="danger" :loading="submitting" @click="submitUsageAudit">确认驳回</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getApplicationAuditList, auditApplication, getUsageReportList, auditUsageReport } from '@/api/audit'

const USAGE_STATUS_MAP = {
  0: { label: '未填写', type: 'info' },
  1: { label: '待审核', type: 'warning' },
  2: { label: '已公示', type: 'success' },
  3: { label: '已驳回', type: 'danger' }
}

export default {
  name: 'ApplicationAudit',
  data() {
    return {
      activeTab: 'application',
      // 申领审核
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
      submitting: false,
      // 使用情况审核
      usageList: [],
      usageLoading: false,
      usagePage: 1,
      usagePageSize: 10,
      usageTotal: 0,
      usagePendingCount: 0,
      usageAuditDialogVisible: false,
      currentUsageItem: {},
      usageAuditAction: 'approve',
      usageAuditForm: { reason: '' },
      usageAuditRules: {
        reason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchApplicationList()
    this.fetchUsagePendingCount()
  },
  methods: {
    handleTabChange(tab) {
      if (tab.name === 'usage') this.fetchUsageList()
    },
    fetchApplicationList() {
      this.loading = true
      getApplicationAuditList({ status: 0, page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    fetchUsagePendingCount() {
      getUsageReportList({ usageStatus: 1, page: 1, size: 1 })
        .then(res => { this.usagePendingCount = res.data.total || 0 })
    },
    fetchUsageList() {
      this.usageLoading = true
      getUsageReportList({ page: this.usagePage, size: this.usagePageSize })
        .then(res => {
          this.usageList = res.data.items || []
          this.usageTotal = res.data.total || 0
        })
        .finally(() => { this.usageLoading = false })
    },
    openAudit(item, action) {
      if (action === 'approve') {
        this.$confirm(
          `确认通过「${item.organizationName}」申领「${item.itemTitle}」x${item.quantity} 的请求？`,
          '审核确认',
          { confirmButtonText: '确认通过', cancelButtonText: '取消', type: 'warning' }
        ).then(() => {
          this.submitting = true
          auditApplication(item.id, { action: 'approve' })
            .then(() => { this.$message.success('审核通过'); this.fetchApplicationList() })
            .finally(() => { this.submitting = false })
        }).catch(() => {})
      } else {
        this.currentItem = item
        this.rejectForm.reason = ''
        this.rejectDialogVisible = true
        this.$nextTick(() => { this.$refs.rejectForm && this.$refs.rejectForm.clearValidate() })
      }
    },
    submitReject() {
      this.$refs.rejectForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        auditApplication(this.currentItem.id, { action: 'reject', reason: this.rejectForm.reason })
          .then(() => {
            this.$message.success('已驳回')
            this.rejectDialogVisible = false
            this.fetchApplicationList()
          })
          .finally(() => { this.submitting = false })
      })
    },
    openUsageAudit(item, action) {
      this.currentUsageItem = item
      this.usageAuditAction = action
      this.usageAuditForm.reason = ''
      this.usageAuditDialogVisible = true
      this.$nextTick(() => { this.$refs.usageAuditForm && this.$refs.usageAuditForm.clearValidate() })
    },
    submitUsageAudit() {
      if (this.usageAuditAction === 'reject') {
        this.$refs.usageAuditForm.validate(valid => {
          if (!valid) return
          this._doUsageAudit()
        })
      } else {
        this._doUsageAudit()
      }
    },
    _doUsageAudit() {
      this.submitting = true
      auditUsageReport(this.currentUsageItem.id, {
        action: this.usageAuditAction,
        reason: this.usageAuditForm.reason
      }).then(() => {
        this.$message.success(this.usageAuditAction === 'approve' ? '已通过，使用情况已在公示页面展示' : '已驳回')
        this.usageAuditDialogVisible = false
        this.fetchUsageList()
        this.fetchUsagePendingCount()
      }).finally(() => { this.submitting = false })
    },
    usageStatusLabel(val) { return (USAGE_STATUS_MAP[val] || USAGE_STATUS_MAP[0]).label },
    usageStatusType(val) { return (USAGE_STATUS_MAP[val] || USAGE_STATUS_MAP[0]).type },
    truncate(str) { return str && str.length > 30 ? str.substring(0, 30) + '…' : str },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
