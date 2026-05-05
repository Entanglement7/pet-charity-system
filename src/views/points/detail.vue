<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>积分明细</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column prop="type" label="类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.amount > 0 ? 'success' : 'warning'">
              {{ scope.row.amount > 0 ? '获得' : '使用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="120">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.amount > 0 ? '#67C23A' : '#E6A23C' }">
              {{ scope.row.amount > 0 ? '+' : '' }}{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="原因" />
        <el-table-column label="时间" width="180">
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script>
import { getPointsDetail } from '@/api/points'

const TYPE_MAP = {
  'donate': { label: '获得', type: 'success' },
  'exchange': { label: '使用', type: 'warning' }
}

export default {
  name: 'PointsDetail',
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
      getPointsDetail({ page: this.page, size: this.pageSize }).then(res => {
        this.list = (res.data?.items || []).map(item => ({
          ...item,
          typeLabel: TYPE_MAP[item.type]?.label || '其他',
          typeColor: TYPE_MAP[item.type]?.type || 'info'
        }))
        this.total = res.data?.total || 0
      }).catch(() => {
        this.$message.error('加载失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchData()
    },
    formatTime(val) {
      return val ? val.replace('T', ' ').substring(0, 19) : '—'
    }
  }
}
</script>
