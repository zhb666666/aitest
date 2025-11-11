<template>
  <div class="users-container">
    <el-card>
      <div class="header">
        <h2>用户管理</h2>
        <el-button type="primary" @click="showAddDialog">新增用户</el-button>
      </div>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="phone" label="手机" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status">
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
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'Users',
  setup() {
    const store = useStore()
    const dialogVisible = ref(false)
    const loading = ref(false)
    const isEdit = ref(false)
    const userFormRef = ref(null)

    const users = computed(() => store.state.user.users)

    const userForm = reactive({
      username: '',
      password: '',
      email: '',
      phone: '',
      status: 1
    })

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

    const fetchUsers = async () => {
      await store.dispatch('user/fetchUsers')
    }

    const showAddDialog = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const editUser = (user) => {
      isEdit.value = true
      Object.assign(userForm, user)
      dialogVisible.value = true
    }

    const submitForm = async () => {
      await userFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            if (isEdit.value) {
              await store.dispatch('user/updateUser', { id: userForm.id, userData: userForm })
              ElMessage.success('更新成功')
            } else {
              await store.dispatch('user/createUser', userForm)
              ElMessage.success('创建成功')
            }
            dialogVisible.value = false
            await fetchUsers()
          } catch (error) {
            ElMessage.error(error.message || '操作失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const deleteUser = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
          type: 'warning'
        })
        await store.dispatch('user/deleteUser', id)
        ElMessage.success('删除成功')
        await fetchUsers()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const resetForm = () => {
      Object.assign(userForm, {
        username: '',
        password: '',
        email: '',
        phone: '',
        status: 1
      })
      if (userFormRef.value) {
        userFormRef.value.resetFields()
      }
    }

    onMounted(() => {
      fetchUsers()
    })

    return {
      users,
      dialogVisible,
      dialogTitle,
      isEdit,
      userForm,
      rules,
      userFormRef,
      loading,
      showAddDialog,
      editUser,
      submitForm,
      deleteUser
    }
  }
}
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
