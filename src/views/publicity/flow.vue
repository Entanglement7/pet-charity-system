<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>物资流向公示</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无物流记录">
        <el-table-column prop="itemName" label="物品名称" min-width="130" />
        <el-table-column prop="donorName" label="捐赠人" width="110" />
        <el-table-column prop="receiverName" label="接收方" width="120" />
        <el-table-column prop="receiverAddress" label="接收地址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="company" label="快递公司" width="110" />
        <el-table-column prop="trackingNo" label="物流单号" width="150" />
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发货时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.shippedAt) }}</template>
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
import { getPublicityFlow } from '@/api/publicity'

const STATUS_MAP = {
  0: { label: '待发货', type: 'info' },
  1: { label: '运输中', type: 'warning' },
  2: { label: '已签收', type: 'success' }
}

export default {
  name: 'PublicityFlow',
  data() {
    return { list: [], loading: false, page: 1, pageSize: 10, total: 0 }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getPublicityFlow({ page: this.page, size: this.pageSize })
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
