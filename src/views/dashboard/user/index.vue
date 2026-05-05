<template>
  <div class="dashboard-container">
    <el-row :gutter="gutter">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的积分</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ pointsInfo.total || 0 }}</div>
            <div class="points-label">当前积分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>捐赠统计</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ donationStats.total || 0 }}</div>
            <div class="points-label">累计捐赠</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>兑换统计</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ exchangeStats.total || 0 }}</div>
            <div class="points-label">累计兑换</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>待处理</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ pendingCount || 0 }}</div>
            <div class="points-label">待确认收货</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="gutter" class="table-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>最近捐赠</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/donation/list')">查看更多</el-button>
          </div>
          <div class="table-wrapper">
            <el-table :data="recentDonations" style="width: 100%" :show-header="true">
              <el-table-column prop="name" label="物品名称" min-width="100" />
              <el-table-column prop="status" label="状态" width="80" align="center" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>最近兑换</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/points/records')">查看更多</el-button>
          </div>
          <div class="table-wrapper">
            <el-table :data="recentExchanges" style="width: 100%" :show-header="true">
              <el-table-column prop="name" label="物品名称" min-width="100" />
              <el-table-column prop="points" label="积分" width="80" align="center" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getPointsInfo } from '@/api/points'
import { getDonationList } from '@/api/donation'
import { getExchangeRecords } from '@/api/points'

const STATUS_MAP = {
  0: '待审核',
  1: '已通过',
  2: '已驳回',
  3: '已发货',
  4: '已收货'
}

export default {
  name: 'UserDashboard',
  data() {
    return {
      pointsInfo: {},
      donationStats: {},
      exchangeStats: {},
      pendingCount: 0,
      recentDonations: [],
      recentExchanges: []
    }
  },
  computed: {
    gutter() {
      const width = document.documentElement.clientWidth
      if (width < 768) return 10
      return 20
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      Promise.all([
        getPointsInfo(),
        getDonationList({ page: 1, size: 5 }),
        getExchangeRecords({ page: 1, size: 5 })
      ]).then(([pointsRes, donationRes, exchangeRes]) => {
        const pointsData = pointsRes.data || {}
        this.pointsInfo = { total: pointsData.balance || 0 }

        const donations = donationRes.data?.items || []
        this.recentDonations = donations.map(d => ({
          name: d.title,
          status: STATUS_MAP[d.status] || '未知'
        }))
        this.donationStats = { total: donationRes.data?.total || 0 }

        const exchanges = exchangeRes.data?.items || []
        this.recentExchanges = exchanges.map(e => ({
          name: `兑换物品 #${e.itemId}`,
          points: e.pointsCost || 0
        }))
        this.exchangeStats = { total: exchangeRes.data?.total || 0 }

        this.pendingCount = exchanges.filter(e => e.status === 1).length
      }).catch(() => {
        this.$message.error('加载失败')
      })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.table-row {
  margin-top: 20px;
}

.points-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
}

.points-label {
  font-size: 14px;
  color: #909399;
  text-align: center;
  margin-top: 10px;
}

.text.item {
  padding: 20px 0;
}

.table-wrapper {
  overflow-x: auto;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }

  .box-card {
    margin-bottom: 10px;
  }

  .box-card .el-card__header {
    padding: 10px 12px;
    font-size: 13px;
  }

  .points-value {
    font-size: 24px;
  }

  .points-label {
    font-size: 12px;
    margin-top: 5px;
  }

  .text.item {
    padding: 12px 0;
  }

  .table-row {
    margin-top: 10px;
  }

  .table-row .el-col {
    margin-bottom: 10px;
  }

  .table-row .el-card__header {
    padding: 10px 12px;
  }

  .table-row .el-card__header span {
    font-size: 13px;
  }

  .table-row .el-card__body {
    padding: 10px;
  }

  .table-wrapper >>> .hide-on-mobile {
    display: none;
  }
}

/* 小屏幕优化 */
@media screen and (max-width: 480px) {
  .dashboard-container {
    padding: 8px;
  }

  .points-value {
    font-size: 22px;
  }

  .box-card .el-card__header span {
    font-size: 12px;
  }
}
</style>
