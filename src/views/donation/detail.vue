<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>捐赠详情</span>
        <el-button style="float: right" size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-descriptions v-if="detail.id" :column="2" border>
        <el-descriptions-item label="物品名称">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="物品分类">{{ categoryLabel(detail.category) }}</el-descriptions-item>
        <el-descriptions-item label="成色">{{ conditionLabel(detail.conditionLevel) }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="获得积分">
          <span v-if="detail.pointsAwarded" style="color:#67C23A;font-weight:bold">+{{ detail.pointsAwarded }}</span>
          <span v-else>—</span>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="物品描述" :span="2">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.rejectReason" label="驳回原因" :span="2">
          <el-alert :title="detail.rejectReason" type="error" :closable="false" />
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="logistics" style="margin-top:30px">
        <el-divider>物流信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="快递公司">{{ logistics.company || '—' }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ logistics.trackingNo || '—' }}</el-descriptions-item>
          <el-descriptions-item label="寄件人">{{ logistics.senderName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="寄件地址">{{ logistics.senderAddress || '—' }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ logistics.receiverName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="收件地址">{{ logistics.receiverAddress || '—' }}</el-descriptions-item>
        </el-descriptions>
        <el-steps :active="logisticsStep" finish-status="success" style="margin-top:20px">
          <el-step title="待发货" />
          <el-step title="运输中" />
          <el-step title="已签收" />
        </el-steps>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getDonationDetail, getLogistics } from '@/api/donation'

const CATEGORY_MAP = {
  pet_food: '主粮', pet_snack: '零食', pet_toy: '玩具',
  pet_bed: '窝垫', pet_medicine: '药品', pet_care: '洗护',
  pet_clothing: '服装', pet_supplies: '其他用品'
}

const CONDITION_MAP = {
  new: '全新', like_new: '九成新', good: '八成新', fair: '七成新'
}

const STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已驳回', type: 'danger' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'info' }
}

export default {
  name: 'DonationDetail',
  data() {
    return { detail: {}, logistics: null }
  },
  computed: {
    logisticsStep() {
      if (!this.logistics) return 0
      return this.logistics.status + 1
    }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      const id = this.$route.params.id
      getDonationDetail(id).then(res => {
        this.detail = res.data || {}
        if (this.detail.status >= 3) {
          getLogistics(id).then(r => { this.logistics = r.data }).catch(() => {})
        }
      })
    },
    categoryLabel(val) { return CATEGORY_MAP[val] || val || '—' },
    conditionLabel(val) { return CONDITION_MAP[val] || val || '—' },
    statusLabel(val) { return (STATUS_MAP[val] || {}).label || '未知' },
    statusType(val) { return (STATUS_MAP[val] || {}).type || 'info' },
    formatTime(val) { return val ? val.replace('T', ' ').substring(0, 16) : '—' }
  }
}
</script>
