<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>申领记录</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无申领记录">
        <el-table-column prop="itemTitle" label="物品名称" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="reason" label="申领理由" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申领时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 3" size="mini" type="success" @click="handleConfirm(scope.row)">确认收货</el-button>
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
  </div>
</template>

<script>
import { getApplicationRecords, confirmApplicationReceipt } from '@/api/application'

const STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '审核通过', type: 'success' },
  2: { label: '已驳回', type: 'danger' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'info' }
}

export default {
  name: 'ApplicationRecords',
  data() {
    return {
      list: [],
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0
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
          this.$message.success('已确认收货')
          this.fetchData()
        })
      }).catch(() => {})
    },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
