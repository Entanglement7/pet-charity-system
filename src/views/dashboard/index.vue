<template>
  <div>
    <component :is="dashboardComponent" />
  </div>
</template>

<script>
import UserDashboard from './user/index'
import OrganizationDashboard from './organization/index'
import AdminDashboard from './admin/index'
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  components: {
    UserDashboard,
    OrganizationDashboard,
    AdminDashboard
  },
  computed: {
    ...mapGetters(['roles']),
    dashboardComponent() {
      if (this.roles.includes('admin')) {
        return 'AdminDashboard'
      } else if (this.roles.includes('organization')) {
        return 'OrganizationDashboard'
      } else {
        return 'UserDashboard'
      }
    }
  }
}
</script>
