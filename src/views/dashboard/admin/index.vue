<template>
  <div class="dashboard-container">
    <el-row :gutter="gutter">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>待审核机构</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ auditStats.organization || 0 }}</div>
            <div class="points-label">待处理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>待审核物品</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ auditStats.donation || 0 }}</div>
            <div class="points-label">待处理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>待审核申领</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ auditStats.application || 0 }}</div>
            <div class="points-label">待处理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>系统用户</span>
          </div>
          <div class="text item">
            <div class="points-value">{{ systemStats.users || 0 }}</div>
            <div class="points-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="gutter" class="table-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>待审核机构</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/audit/organization')">查看更多</el-button>
          </div>
          <div class="table-wrapper">
            <el-table :data="pendingOrganizations" style="width: 100%">
              <el-table-column prop="name" label="机构名称" min-width="120" />
              <el-table-column prop="type" label="机构类型" width="100" class-name="hide-on-mobile" />
              <el-table-column prop="applyTime" label="申请时间" width="160" class-name="hide-on-mobile" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>待审核物品</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/audit/donation')">查看更多</el-button>
          </div>
          <div class="table-wrapper">
            <el-table :data="pendingDonations" style="width: 100%">
              <el-table-column prop="name" label="物品名称" min-width="120" />
              <el-table-column prop="category" label="分类" width="80" />
              <el-table-column prop="donor" label="捐赠人" width="100" class-name="hide-on-mobile" />
              <el-table-column prop="createTime" label="发布时间" width="160" class-name="hide-on-mobile" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getStatisticsOverview } from '@/api/statistics'
import { getOrganizationAuditList } from '@/api/audit'
import { getDonationAuditList } from '@/api/audit'
import { getApplicationAuditList } from '@/api/audit'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      auditStats: {},
      systemStats: {},
      pendingOrganizations: [],
      pendingDonations: []
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
        getStatisticsOverview(),
        getOrganizationAuditList({ status: 0, page: 1, size: 5 }),
        getDonationAuditList({ status: 0, page: 1, size: 5 }),
        getApplicationAuditList({ status: 0, page: 1, size: 5 })
      ]).then(([statsRes, orgRes, donationRes, appRes]) => {
        const stats = statsRes.data || {}
        this.auditStats = {
          organization: stats.pendingOrganizations || 0,
          donation: stats.pendingDonations || 0,
          application: stats.pendingApplications || 0
        }
        this.systemStats = { users: stats.totalUsers || 0 }

        this.pendingOrganizations = (orgRes.data?.items || []).map(item => ({
          name: item.name,
          type: this.orgTypeText(item.type),
          applyTime: this.formatTime(item.createdAt)
        }))

        this.pendingDonations = (donationRes.data?.items || []).map(item => ({
          name: item.title,
          category: this.categoryText(item.category),
          donor: '用户',
          createTime: this.formatTime(item.createdAt)
        }))
      }).catch(() => {
        this.$message.error('加载失败')
      })
    },
    orgTypeText(type) {
      const map = { rescue: '救助站', hospital: '医院', charity: '公益', other: '其他' }
      return map[type] || type || '—'
    },
    categoryText(cat) {
      const map = { pet_food: '主粮', pet_snack: '零食', pet_toy: '玩具', pet_bed: '窝垫', pet_medicine: '药品', pet_care: '洗护', pet_clothing: '服装', pet_supplies: '其他' }
      return map[cat] || cat || '—'
    },
    formatTime(val) {
      return val ? val.replace('T', ' ').substring(0, 19) : '—'
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
    padding: 12px 15px;
    font-size: 14px;
  }

  .points-value {
    font-size: 28px;
  }

  .points-label {
    font-size: 12px;
    margin-top: 5px;
  }

  .text.item {
    padding: 15px 0;
  }

  .table-row {
    margin-top: 10px;
  }

  .table-row .el-col {
    margin-bottom: 10px;
  }

  .table-row .el-card__header {
    padding: 12px 15px;
  }

  .table-row .el-card__header span {
    font-size: 14px;
  }

  .table-row .el-card__body {
    padding: 10px;
  }
}

/* 隐藏移动端不必要的列 */
@media screen and (max-width: 768px) {
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
    font-size: 24px;
  }

  .box-card .el-card__header span {
    font-size: 13px;
  }
}
</style>
