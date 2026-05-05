<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="card-header">
        <span class="title">积分兑换</span>
        <span class="points-tip">当前积分：{{ currentPoints }}</span>
      </div>
      <el-row :gutter="20">
        <el-col v-for="item in exchangeList" :key="item.id" :xs="12" :sm="12" :md="8" :lg="6" :xl="6">
          <el-card :body-style="{ padding: '0px' }" class="exchange-item">
            <img :src="item.image || 'https://via.placeholder.com/300x200'" class="image">
            <div class="item-body">
              <span class="item-name">{{ item.name }}</span>
              <div class="bottom clearfix">
                <span class="points-required">{{ item.pointsCost }} 积分</span>
                <el-button type="text" class="button" @click="handleExchange(item)">兑换</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog title="填写收货地址" :visible.sync="dialogVisible" :width="dialogWidth" custom-class="exchange-dialog">
      <el-form ref="addressForm" :model="addressForm" :rules="addressRules" :label-width="formLabelWidth">
        <el-form-item label="兑换物品">
          <span>{{ currentItem.name }}</span>
        </el-form-item>
        <el-form-item label="消耗积分">
          <span style="color:#409EFF;font-weight:bold">{{ currentItem.pointsCost }} 积分</span>
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="addressForm.address" type="textarea" :rows="3" placeholder="请填写详细收货地址（姓名+手机号+地址）" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitExchange">确认兑换</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getExchangeList, exchangeItem, getPointsInfo } from '@/api/points'

export default {
  name: 'PointsExchange',
  data() {
    return {
      exchangeList: [],
      currentPoints: 0,
      dialogVisible: false,
      currentItem: {},
      addressForm: { address: '' },
      addressRules: {
        address: [{ required: true, message: '请填写收货地址', trigger: 'blur' }]
      },
      submitting: false,
      screenWidth: document.documentElement.clientWidth
    }
  },
  computed: {
    dialogWidth() {
      return this.screenWidth < 768 ? '92%' : '480px'
    },
    formLabelWidth() {
      return this.screenWidth < 768 ? '80px' : '90px'
    }
  },
  created() { this.fetchData() },
  mounted() {
    this.handleResize = () => { this.screenWidth = document.documentElement.clientWidth }
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    fetchData() {
      Promise.all([
        getPointsInfo(),
        getExchangeList({ page: 1, size: 20 })
      ]).then(([pointsRes, exchangeRes]) => {
        this.currentPoints = pointsRes.data?.balance || 0
        this.exchangeList = exchangeRes.data?.items || []
      }).catch(() => { this.$message.error('加载失败') })
    },
    handleExchange(item) {
      if (this.currentPoints < item.pointsCost) {
        this.$message.error('积分不足，无法兑换')
        return
      }
      this.currentItem = item
      this.addressForm.address = ''
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.addressForm && this.$refs.addressForm.clearValidate() })
    },
    submitExchange() {
      this.$refs.addressForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        exchangeItem({ itemId: this.currentItem.id, quantity: 1, deliveryAddress: this.addressForm.address })
          .then(() => {
            this.$message.success('兑换成功')
            this.currentPoints -= this.currentItem.pointsCost
            this.dialogVisible = false
          })
          .catch(err => { this.$message.error(err.message || '兑换失败') })
          .finally(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 6px; }
.card-header .title { font-weight: bold; }
.card-header .points-tip { color: #409EFF; font-size: 14px; }
.exchange-item { margin-bottom: 20px; }
.image { width: 100%; height: 200px; display: block; object-fit: cover; }
.item-body { padding: 14px; }
.item-name { font-size: 16px; font-weight: bold; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bottom { margin-top: 13px; line-height: 12px; }
.points-required { color: #409EFF; font-weight: bold; }
.button { padding: 0; float: right; }

@media screen and (max-width: 768px) {
  .app-container { padding: 10px !important; }
  .image { height: 130px; }
  .item-body { padding: 10px; }
  .item-name { font-size: 14px; }
  .bottom { margin-top: 8px; font-size: 13px; }
  .exchange-item { margin-bottom: 12px; }
  .card-header .title { font-size: 15px; }
  .card-header .points-tip { font-size: 13px; }
}

@media screen and (max-width: 480px) {
  .image { height: 110px; }
  .item-name { font-size: 13px; }
  .bottom { font-size: 12px; }
  .points-required { font-size: 12px; }
}
</style>

