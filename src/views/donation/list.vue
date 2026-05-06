<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>我的捐赠</span>
        <el-button style="float: right" type="primary" size="small" @click="$router.push('/donation/publish')">发布捐赠</el-button>
      </div>
      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无捐赠记录">
        <el-table-column label="图片" width="80">
          <template slot-scope="scope">
            <el-image
              v-if="getFirstImage(scope.row.images)"
              :src="getFirstImage(scope.row.images)"
              style="width: 50px; height: 50px"
              fit="cover"
              :preview-src-list="getImageList(scope.row.images)"
            />
            <i v-else class="el-icon-picture-outline" style="font-size: 24px; color: #C0C4CC"></i>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="物品名称" min-width="140" />
        <el-table-column prop="category" label="分类" width="110">
          <template slot-scope="scope">
            <el-tag size="small">{{ categoryLabel(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pointsAwarded" label="获得积分" width="90">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 1" style="color:#67C23A;font-weight:bold">+{{ scope.row.pointsAwarded }}</span>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="$router.push('/donation/detail/' + scope.row.id)">详情</el-button>
            <el-button v-if="scope.row.status === 0" size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
import { getDonationList, deleteDonation } from '@/api/donation'

const CATEGORY_MAP = {
  pet_food: '主粮', pet_snack: '零食', pet_toy: '玩具',
  pet_bed: '窝垫', pet_medicine: '药品', pet_care: '洗护',
  pet_clothing: '服装', pet_supplies: '其他用品'
}

const STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已驳回', type: 'danger' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'info' }
}

export default {
  name: 'DonationList',
  data() {
    return { list: [], loading: false, page: 1, pageSize: 10, total: 0 }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      getDonationList({ page: this.page, size: this.pageSize })
        .then(res => {
          this.list = res.data.items || []
          this.total = res.data.total || 0
        })
        .finally(() => { this.loading = false })
    },
    handleDelete(row) {
      this.$confirm(`确定删除「${row.title}」吗？`, '提示', {
        type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
      }).then(() => {
        deleteDonation(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchData()
        })
      }).catch(() => {})
    },
    categoryLabel(val) { return CATEGORY_MAP[val] || val || '—' },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' },
    getFirstImage(images) {
      if (!images) return ''
      try {
        const list = JSON.parse(images)
        return list && list.length > 0 ? list[0] : ''
      } catch {
        return ''
      }
    },
    getImageList(images) {
      if (!images) return []
      try {
        return JSON.parse(images)
      } catch {
        return []
      }
    }
  }
}
</script>
