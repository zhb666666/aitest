<template>
  <div class="menus-container">
    <el-card>
      <div class="header">
        <h2>菜单管理</h2>
        <el-button type="primary" @click="showAddDialog">新增菜单</el-button>
      </div>
      <el-tree
        :data="menuTree"
        :props="{ children: 'children', label: 'menuName' }"
        default-expand-all
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span>{{ data.menuName }}</span>
            <span class="tree-node-actions">
              <el-button type="primary" size="small" @click="editMenu(data)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteMenu(data.id)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="menuForm" :rules="rules" ref="menuFormRef" label-width="100px">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName"></el-input>
        </el-form-item>
        <el-form-item label="菜单代码" prop="menuCode">
          <el-input v-model="menuForm.menuCode"></el-input>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="menuForm.path"></el-input>
        </el-form-item>
        <el-form-item label="组件" prop="component">
          <el-input v-model="menuForm.component"></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="menuForm.icon"></el-input>
        </el-form-item>
        <el-form-item label="父菜单" prop="parentId">
          <el-input-number v-model="menuForm.parentId" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="menuForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="menuForm.menuType">
            <el-option label="菜单" value="menu"></el-option>
            <el-option label="按钮" value="button"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="menuForm.status">
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

export default {
  name: 'Menus',
  setup() {
    const dialogVisible = ref(false)
    const loading = ref(false)
    const isEdit = ref(false)
    const menuFormRef = ref(null)
    const menuTree = ref([])

    const menuForm = reactive({
      menuName: '',
      menuCode: '',
      path: '',
      component: '',
      icon: '',
      parentId: 0,
      sort: 0,
      menuType: 'menu',
      status: 1
    })

    const rules = {
      menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

    const fetchMenus = async () => {
      try {
        const response = await api.get('/menus/tree')
        if (response.data.code === 200) {
          menuTree.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载菜单失败')
      }
    }

    const showAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const editMenu = (menu) => {
      isEdit.value = true
      Object.assign(menuForm, menu)
      dialogVisible.value = true
    }

    const submitForm = async () => {
      await menuFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            if (isEdit.value) {
              const response = await api.put(`/menus/${menuForm.id}`, menuForm)
              if (response.data.code === 200) {
                ElMessage.success('更新成功')
                dialogVisible.value = false
                await fetchMenus()
              }
            } else {
              const response = await api.post('/menus', menuForm)
              if (response.data.code === 200) {
                ElMessage.success('创建成功')
                dialogVisible.value = false
                await fetchMenus()
              }
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '操作失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const deleteMenu = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
          type: 'warning'
        })
        const response = await api.delete(`/menus/${id}`)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          await fetchMenus()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const resetForm = () => {
      Object.assign(menuForm, {
        menuName: '',
        menuCode: '',
        path: '',
        component: '',
        icon: '',
        parentId: 0,
        sort: 0,
        menuType: 'menu',
        status: 1
      })
      if (menuFormRef.value) {
        menuFormRef.value.resetFields()
      }
    }

    onMounted(() => {
      fetchMenus()
    })

    return {
      menuTree,
      dialogVisible,
      dialogTitle,
      isEdit,
      menuForm,
      rules,
      menuFormRef,
      loading,
      showAddDialog,
      editMenu,
      submitForm,
      deleteMenu
    }
  }
}
</script>

<style scoped>
.menus-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-node-actions {
  margin-left: 20px;
}
</style>

