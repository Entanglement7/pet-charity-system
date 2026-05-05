<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>捐赠记录查询</span>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无捐赠记录">
        <el-table-column prop="title" label="物品名称" min-width="130" />
        <el-table-column prop="donorName" label="捐赠人" width="110" />
        <el-table-column prop="category" label="分类" width="100">
          <template slot-scope="scope">
            <el-tag size="small">{{ categoryLabel(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pointsAwarded" label="积分" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.pointsAwarded" style="color:#67C23A">+{{ scope.row.pointsAwarded }}</span>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="捐赠时间" width="160">
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
import { getPublicityRecords } from '@/api/publicity'

const CATEGORY_MAP = {
  pet_food: '主粮', pet_snack: '零食', pet_toy: '玩具',
  pet_bed: '窝垫', pet_medicine: '药品', pet_care: '洗护',
  pet_clothing: '服装', pet_supplies: '其他用品'
}

const STATUS_MAP = {
  1: { label: '已通过', type: 'success' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'info' }
}

export default {
  name: 'PublicityRecords',
  data() {
    return { list: [], loading: false, page: 1, pageSize: 10, total: 0 }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getPublicityRecords({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    categoryLabel(val) { return CATEGORY_MAP[val] || val || '—' },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
