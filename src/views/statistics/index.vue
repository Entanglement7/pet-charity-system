<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">注册用户</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalOrgs || 0 }}</div>
          <div class="stat-label">认证机构</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalDonations || 0 }}</div>
          <div class="stat-label">累计捐赠</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalApplications || 0 }}</div>
          <div class="stat-label">累计申领</div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6">
        <div class="stat-card stat-card--warning">
          <div class="stat-value">{{ stats.pendingDonations || 0 }}</div>
          <div class="stat-label">待审核捐赠</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--warning">
          <div class="stat-value">{{ stats.pendingApplications || 0 }}</div>
          <div class="stat-label">待审核申领</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getStatisticsOverview } from '@/api/statistics'

export default {
  name: 'StatisticsIndex',
  data() {
    return { stats: {}}
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      getStatisticsOverview().then(res => {
        this.stats = res.data || {}
      })
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
  padding: 40px 0;
  background: #f5f7fa;
  border-radius: 4px;
}
.stat-card--warning {
  background: #fdf6ec;
}
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}
.stat-card--warning .stat-value {
  color: #E6A23C;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}
</style>
