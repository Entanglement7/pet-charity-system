<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>用户积分列表</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无数据">
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column label="当前积分" min-width="120">
          <template slot-scope="scope">
            <span style="color:#E6A23C;font-weight:bold">{{ scope.row.balance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="累计获得" min-width="120">
          <template slot-scope="scope">
            <span style="color:#67C23A">+{{ scope.row.totalEarned }}</span>
          </template>
        </el-table-column>
        <el-table-column label="累计消费" min-width="120">
          <template slot-scope="scope">
            <span style="color:#909399">-{{ scope.row.totalSpent }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="180">
          <template slot-scope="scope">{{ formatTime(scope.row.updatedAt) }}</template>
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
  </div>
</template>

<script>
import { getUserPointsList } from '@/api/points'

export default {
  name: 'UserPointsList',
  data() {
    return { list: [], loading: false, page: 1, pageSize: 10, total: 0 }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getUserPointsList({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 19) : '—' }
  }
}
</script>
