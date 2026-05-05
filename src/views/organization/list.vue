<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>机构列表</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无机构数据">
        <el-table-column prop="name" label="机构名称" min-width="150" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
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
import { getOrganizationAuditList } from '@/api/audit'

const STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' }
}

export default {
  name: 'OrganizationList',
  data() {
    return { list: [], loading: false, page: 1, pageSize: 10, total: 0 }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getOrganizationAuditList({ page: this.page, size: this.pageSize })
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
