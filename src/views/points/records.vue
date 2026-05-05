<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>兑换记录</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无兑换记录">
        <el-table-column prop="itemName" label="物品名称" min-width="130" />
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="pointsCost" label="消耗积分" width="100">
          <template slot-scope="scope">
            <span style="color:#E6A23C">-{{ scope.row.pointsCost }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryAddress" label="收货地址" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="兑换时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
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
import { getExchangeRecords } from '@/api/points'

const STATUS_MAP = {
  0: { label: '待发货', type: 'info' },
  1: { label: '已发货', type: 'warning' },
  2: { label: '已收货', type: 'success' }
}

export default {
  name: 'ExchangeRecords',
  data() {
    return {
      list: [], loading: false, page: 1, pageSize: 10, total: 0
    }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getExchangeRecords({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
