<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>我的积分</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="points-card">
            <div class="points-value">{{ pointsInfo.total || 0 }}</div>
            <div class="points-label">当前积分</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="points-card">
            <div class="points-value">{{ pointsInfo.earned || 0 }}</div>
            <div class="points-label">累计获得</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="points-card">
            <div class="points-value">{{ pointsInfo.used || 0 }}</div>
            <div class="points-label">累计使用</div>
          </div>
        </el-col>
      </el-row>
      <el-divider />
      <el-button type="primary" @click="$router.push('/points/detail')">积分明细</el-button>
      <el-button type="success" @click="$router.push('/points/exchange')">积分兑换</el-button>
    </el-card>
  </div>
</template>

<script>
import { getPointsInfo } from '@/api/points'

export default {
  name: 'PointsIndex',
  data() {
    return {
      pointsInfo: {}
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      getPointsInfo().then(res => {
        const data = res.data || {}
        this.pointsInfo = {
          total: data.balance || 0,
          earned: data.totalEarned || 0,
          used: data.totalSpent || 0
        }
      }).catch(() => {
        this.$message.error('加载失败')
      })
    }
  }
}
</script>

<style scoped>
.points-card {
  text-align: center;
  padding: 30px 0;
  background: #f5f7fa;
  border-radius: 4px;
}
.points-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}
.points-label {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}
</style>
