<template>
  <div class="dashboard-container">
    <el-row :gutter="gutter">
      <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix"><span>认证状态</span></div>
          <div class="text item verification-item">
            <el-tag :type="statusTagType">{{ statusTagText }}</el-tag>
            <div v-if="orgStatus === 2 && rejectReason" style="color:#F56C6C;font-size:12px;margin-top:8px">{{ rejectReason }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8" :lg="8" :xl="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix"><span>申领统计</span></div>
          <div class="text item">
            <div class="points-value">{{ totalApplications }}</div>
            <div class="points-label">累计申领</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8" :lg="8" :xl="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix"><span>待处理</span></div>
          <div class="text item">
            <div class="points-value">{{ pendingCount }}</div>
            <div class="points-label">待审核申领</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="gutter" class="table-row">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>最近申领</span>
            <el-button style="float:right;padding:3px 0" type="text" @click="$router.push('/application/records')">查看更多</el-button>
          </div>
          <el-table v-loading="loading" :data="recentApplications" style="width:100%">
            <el-table-column prop="itemTitle" label="物品名称" min-width="120" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="appStatusType(scope.row.status)" size="mini">{{ appStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申领时间" width="160" class-name="hide-on-mobile">
              <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOrganizationInfo } from '@/api/organization'
import { getApplicationRecords } from '@/api/application'

export default {
  name: 'OrganizationDashboard',
  data() {
    return {
      orgStatus: 0,
      rejectReason: '',
      totalApplications: 0,
      pendingCount: 0,
      recentApplications: [],
      loading: false
    }
  },
  computed: {
    gutter() {
      return document.documentElement.clientWidth < 768 ? 10 : 20
    },
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
        this.orgStatus = res.data.status ?? 0
        this.rejectReason = res.data.rejectReason || ''
      }).catch(() => {})

      this.loading = true
      getApplicationRecords({ page: 1, size: 5 }).then(res => {
        const items = res.data.items || []
        this.totalApplications = res.data.total || 0
        this.pendingCount = items.filter(i => i.status === 0).length
        this.recentApplications = items
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    appStatusType(status) {
      return { 0: 'warning', 1: 'success', 2: 'danger', 3: '', 4: 'info' }[status] ?? 'info'
    },
    appStatusText(status) {
      return { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已发货', 4: '已完成' }[status] ?? '未知'
    },
    formatTime(val) {
      return val ? val.replace('T', ' ').substring(0, 16) : '—'
    }
  }
}
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.table-row { margin-top: 20px; }
.points-value { font-size: 32px; font-weight: bold; color: #409EFF; text-align: center; }
.points-label { font-size: 14px; color: #909399; text-align: center; margin-top: 10px; }
.text.item { padding: 20px 0; text-align: center; }
.verification-item { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 72px; }
.table-wrapper { overflow-x: auto; }
@media screen and (max-width: 768px) {
  .dashboard-container { padding: 10px; }
  .box-card { margin-bottom: 10px; }
  .points-value { font-size: 24px; }
  .text.item { padding: 12px 0; }
  .table-row { margin-top: 10px; }
  .table-wrapper >>> .hide-on-mobile { display: none; }
}
</style>
