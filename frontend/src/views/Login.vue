<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
          <el-button @click="showRegister = true">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showRegister" title="注册" width="500px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="registerLoading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const store = useStore()
    const loginFormRef = ref(null)
    const registerFormRef = ref(null)
    const loading = ref(false)
    const registerLoading = ref(false)
    const showRegister = ref(false)

    const loginForm = reactive({
      username: '',
      password: ''
    })

    const registerForm = reactive({
      username: '',
      password: '',
      email: '',
      phone: ''
    })

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }

    const registerRules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
    }

    const handleLogin = async () => {
      await loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            await store.dispatch('auth/login', loginForm)
            ElMessage.success('登录成功')
            router.push('/')
          } catch (error) {
            ElMessage.error(error.message || '登录失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const handleRegister = async () => {
      await registerFormRef.value.validate(async (valid) => {
        if (valid) {
          registerLoading.value = true
          try {
            await store.dispatch('auth/register', registerForm)
            ElMessage.success('注册成功')
            showRegister.value = false
            router.push('/')
          } catch (error) {
            ElMessage.error(error.message || '注册失败')
          } finally {
            registerLoading.value = false
          }
        }
      })
    }

    return {
      loginForm,
      registerForm,
      rules,
      registerRules,
      loginFormRef,
      registerFormRef,
      loading,
      registerLoading,
      showRegister,
      handleLogin,
      handleRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 20px;
}
</style>

