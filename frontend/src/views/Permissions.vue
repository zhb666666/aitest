<template>
  <div class="permissions-container">
    <el-card>
      <div class="header">
        <h2>权限管理</h2>
        <el-button type="primary" @click="showAddDialog">新增权限</el-button>
      </div>
      <el-table :data="permissions" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="permissionName" label="权限名称" width="150"></el-table-column>
        <el-table-column prop="permissionCode" label="权限代码" width="200"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="resourceType" label="资源类型" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editPermission(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deletePermission(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="permissionForm" :rules="rules" ref="permissionFormRef" label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName"></el-input>
        </el-form-item>
        <el-form-item label="权限代码" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="permissionForm.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="permissionForm.resourceType">
            <el-option label="菜单" value="menu"></el-option>
            <el-option label="按钮" value="button"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="permissionForm.status">
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
  name: 'Permissions',
  setup() {
    const dialogVisible = ref(false)
    const loading = ref(false)
    const isEdit = ref(false)
    const permissionFormRef = ref(null)
    const permissions = ref([])

    const permissionForm = reactive({
      permissionName: '',
      permissionCode: '',
      description: '',
      resourceType: 'button',
      status: 1
    })

    const rules = {
      permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
      permissionCode: [{ required: true, message: '请输入权限代码', trigger: 'blur' }]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑权限' : '新增权限')

    const fetchPermissions = async () => {
      try {
        const response = await api.get('/permissions')
        if (response.data.code === 200) {
          permissions.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载权限失败')
      }
    }

    const showAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const editPermission = (permission) => {
      isEdit.value = true
      Object.assign(permissionForm, permission)
      dialogVisible.value = true
    }

    const submitForm = async () => {
      await permissionFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            if (isEdit.value) {
              const response = await api.put(`/permissions/${permissionForm.id}`, permissionForm)
              if (response.data.code === 200) {
                ElMessage.success('更新成功')
                dialogVisible.value = false
                await fetchPermissions()
              }
            } else {
              const response = await api.post('/permissions', permissionForm)
              if (response.data.code === 200) {
                ElMessage.success('创建成功')
                dialogVisible.value = false
                await fetchPermissions()
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

    const deletePermission = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
          type: 'warning'
        })
        const response = await api.delete(`/permissions/${id}`)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          await fetchPermissions()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const resetForm = () => {
      Object.assign(permissionForm, {
        permissionName: '',
        permissionCode: '',
        description: '',
        resourceType: 'button',
        status: 1
      })
      if (permissionFormRef.value) {
        permissionFormRef.value.resetFields()
      }
    }

    onMounted(() => {
      fetchPermissions()
    })

    return {
      permissions,
      dialogVisible,
      dialogTitle,
      isEdit,
      permissionForm,
      rules,
      permissionFormRef,
      loading,
      showAddDialog,
      editPermission,
      submitForm,
      deletePermission
    }
  }
}
</script>

<style scoped>
.permissions-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>

