<template>
  <component :is="currentView" v-if="ready" />
</template>

<script>
import { getOrganizationInfo } from '@/api/organization'
import OrganizationInfo from './info'
import OrganizationRegister from './register'

export default {
  name: 'OrganizationManage',
  components: { OrganizationInfo, OrganizationRegister },
  data() {
    return { ready: false, currentView: 'OrganizationRegister' }
  },
  created() {
    getOrganizationInfo().then(res => {
      this.currentView = res.data && res.data.status === 1 ? 'OrganizationInfo' : 'OrganizationRegister'
    }).catch(() => {
      this.currentView = 'OrganizationRegister'
    }).finally(() => {
      this.ready = true
    })
  }
}
</script>
