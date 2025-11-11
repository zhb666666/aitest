<template>
  <div class="roles-container">
    <el-card>
      <div class="header">
        <h2>角色管理</h2>
        <el-button type="primary" @click="showAddDialog">新增角色</el-button>
      </div>
      <el-table :data="roles" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="roleName" label="角色名称" width="150"></el-table-column>
        <el-table-column prop="roleCode" label="角色代码" width="150"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editRole(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteRole(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="roleForm" :rules="rules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName"></el-input>
        </el-form-item>
        <el-form-item label="角色代码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="roleForm.status">
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
  name: 'Roles',
  setup() {
    const dialogVisible = ref(false)
    const loading = ref(false)
    const isEdit = ref(false)
    const roleFormRef = ref(null)
    const roles = ref([])

    const roleForm = reactive({
      roleName: '',
      roleCode: '',
      description: '',
      status: 1
    })

    const rules = {
      roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
      roleCode: [{ required: true, message: '请输入角色代码', trigger: 'blur' }]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

    const fetchRoles = async () => {
      try {
        const response = await api.get('/roles')
        if (response.data.code === 200) {
          roles.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载角色失败')
      }
    }

    const showAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const editRole = (role) => {
      isEdit.value = true
      Object.assign(roleForm, role)
      dialogVisible.value = true
    }

    const submitForm = async () => {
      await roleFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            if (isEdit.value) {
              const response = await api.put(`/roles/${roleForm.id}`, roleForm)
              if (response.data.code === 200) {
                ElMessage.success('更新成功')
                dialogVisible.value = false
                await fetchRoles()
              }
            } else {
              const response = await api.post('/roles', roleForm)
              if (response.data.code === 200) {
                ElMessage.success('创建成功')
                dialogVisible.value = false
                await fetchRoles()
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

    const deleteRole = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
          type: 'warning'
        })
        const response = await api.delete(`/roles/${id}`)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          await fetchRoles()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const resetForm = () => {
      Object.assign(roleForm, {
        roleName: '',
        roleCode: '',
        description: '',
        status: 1
      })
      if (roleFormRef.value) {
        roleFormRef.value.resetFields()
      }
    }

    onMounted(() => {
      fetchRoles()
    })

    return {
      roles,
      dialogVisible,
      dialogTitle,
      isEdit,
      roleForm,
      rules,
      roleFormRef,
      loading,
      showAddDialog,
      editRole,
      submitForm,
      deleteRole
    }
  }
}
</script>

<style scoped>
.roles-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
