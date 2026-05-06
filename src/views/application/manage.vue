<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>申领管理</span>
      </div>
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="待发货" name="approved">
          <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无待发货申领">
            <el-table-column prop="organizationName" label="申领机构" min-width="140" />
            <el-table-column prop="itemTitle" label="物品名称" min-width="140" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="reason" label="申领理由" min-width="180" show-overflow-tooltip />
            <el-table-column label="申领时间" width="155">
              <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="openShipDialog(scope.row)">发货</el-button>
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
        </el-tab-pane>

        <el-tab-pane label="已发货" name="shipped">
          <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无已发货记录">
            <el-table-column prop="organizationName" label="申领机构" min-width="130" />
            <el-table-column prop="itemTitle" label="物品名称" min-width="130" />
            <el-table-column prop="quantity" label="数量" width="70" />
            <el-table-column label="物流信息" min-width="200">
              <template slot-scope="scope">
                <div v-if="scope.row.logistics">
                  <div>{{ scope.row.logistics.company }} {{ scope.row.logistics.trackingNo }}</div>
                  <div style="color:#999;font-size:12px">{{ scope.row.logistics.receiverName }} {{ scope.row.logistics.receiverPhone }}</div>
                </div>
                <span v-else style="color:#ccc">—</span>
              </template>
            </el-table-column>
            <el-table-column label="发货时间" width="155">
              <template slot-scope="scope">
                {{ scope.row.logistics && scope.row.logistics.shippedAt ? formatTime(scope.row.logistics.shippedAt) : '—' }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template slot-scope="scope">
                <el-tag :type="statusType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
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
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog title="填写物流信息" :visible.sync="shipDialogVisible" width="540px">
      <el-form ref="shipForm" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="申领机构">
          <span>{{ currentItem.organizationName }}</span>
        </el-form-item>
        <el-form-item label="物品名称">
          <span>{{ currentItem.itemTitle }} x {{ currentItem.quantity }}</span>
        </el-form-item>
        <el-form-item label="物流公司" prop="company">
          <el-select v-model="shipForm.company" placeholder="请选择物流公司" style="width:100%">
            <el-option v-for="c in companies" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNo">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="shipForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="receiverPhone">
          <el-input v-model="shipForm.receiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input
            v-model="shipForm.receiverAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入收货地址"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitShip">确认发货</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getApplicationAuditList } from '@/api/audit'
import { updateLogistics, getLogisticsCompanies } from '@/api/logistics'
import { getApplicationLogistics } from '@/api/application'

const STATUS_MAP = {
  1: { label: '待发货', type: 'warning' },
  3: { label: '已发货', type: 'primary' },
  4: { label: '已收货', type: 'success' }
}

export default {
  name: 'ApplicationManage',
  data() {
    return {
      activeTab: 'approved',
      list: [],
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0,
      shipDialogVisible: false,
      currentItem: {},
      shipForm: {
        company: '',
        trackingNo: '',
        receiverName: '',
        receiverPhone: '',
        receiverAddress: ''
      },
      shipRules: {
        company: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
        trackingNo: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
        receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
        receiverPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
      },
      companies: [],
      submitting: false
    }
  },
  created() {
    this.fetchData()
    this.fetchCompanies()
  },
  methods: {
    handleTabChange() {
      this.page = 1
      this.fetchData()
    },
    async fetchData() {
      this.loading = true
      const status = this.activeTab === 'approved' ? 1 : 3
      try {
        const res = await getApplicationAuditList({ status, page: this.page, size: this.pageSize })
        this.list = res.data.items || []
        this.total = res.data.total || 0

        // 如果是已发货标签，获取物流信息
        if (this.activeTab === 'shipped') {
          for (const item of this.list) {
            try {
              const logRes = await getApplicationLogistics(item.id)
              this.$set(item, 'logistics', logRes.data)
            } catch (e) {
              this.$set(item, 'logistics', null)
            }
          }
        }
      } finally {
        this.loading = false
      }
    },
    fetchCompanies() {
      getLogisticsCompanies().then(res => {
        this.companies = res.data || []
      })
    },
    openShipDialog(row) {
      this.currentItem = row
      this.shipForm = {
        company: '',
        trackingNo: '',
        receiverName: '',
        receiverPhone: '',
        receiverAddress: ''
      }
      this.shipDialogVisible = true
      this.$nextTick(() => {
        this.$refs.shipForm && this.$refs.shipForm.clearValidate()
      })
    },
    submitShip() {
      this.$refs.shipForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        const logistics = {
          refType: 'application',
          refId: this.currentItem.id,
          company: this.shipForm.company,
          trackingNo: this.shipForm.trackingNo,
          status: 1,
          receiverName: this.shipForm.receiverName,
          receiverPhone: this.shipForm.receiverPhone,
          receiverAddress: this.shipForm.receiverAddress
        }
        updateLogistics(logistics)
          .then(() => {
            this.$message.success('发货成功')
            this.shipDialogVisible = false
            this.fetchData()
          })
          .finally(() => {
            this.submitting = false
          })
      })
    },
    statusLabel(val) {
      return (STATUS_MAP[val] || {}).label || '未知'
    },
    statusType(val) {
      return (STATUS_MAP[val] || {}).type || 'info'
    },
    formatTime(val) {
      return val ? val.replace('T', ' ').substring(0, 16) : '—'
    }
  }
}
</script>

<style scoped>
</style>
