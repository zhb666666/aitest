<template>
  <el-container>
    <el-header>
      <div class="header-content">
        <h1>交易平台</h1>
        <div class="user-info">
          <span>欢迎, {{ username }}</span>
          <el-button type="text" @click="logout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/market">
            <el-icon><Shop /></el-icon>
            <span>交易市场</span>
          </el-menu-item>
          <el-menu-item index="/product/publish">
            <el-icon><Plus /></el-icon>
            <span>发布商品</span>
          </el-menu-item>
          <el-menu-item index="/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/permissions">
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
          <el-menu-item index="/menus">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { Shop, Plus, ShoppingCart, Document, User, UserFilled, Lock, Menu } from '@element-plus/icons-vue'

export default {
  name: 'MainLayout',
  components: {
    Shop,
    Plus,
    ShoppingCart,
    Document,
    User,
    UserFilled,
    Lock,
    Menu
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const store = useStore()

    const username = computed(() => store.state.auth.username)
    const activeMenu = computed(() => route.path)

    const logout = () => {
      store.dispatch('auth/logout')
      router.push('/login')
    }

    return {
      username,
      activeMenu,
      logout
    }
  }
}
</script>

<style scoped>
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-menu {
  height: 100%;
}
</style>

