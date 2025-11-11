<template>
  <div class="publish-container">
    <el-card>
      <h2>发布商品</h2>
      <el-form :model="productForm" :rules="rules" ref="productFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <ImageUpload v-model="productForm.image" :limit="1" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option label="电子产品" value="electronics"></el-option>
            <el-option label="服装" value="clothing"></el-option>
            <el-option label="食品" value="food"></el-option>
            <el-option label="图书" value="books"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">发布</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import ImageUpload from '@/components/ImageUpload.vue'

export default {
  name: 'ProductPublish',
  components: {
    ImageUpload
  },
  setup() {
    const productFormRef = ref(null)
    const loading = ref(false)

    const productForm = reactive({
      productName: '',
      description: '',
      image: '',
      price: 0,
      stock: 0,
      category: ''
    })

    const rules = {
      productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
      description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
      price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
      stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
      category: [{ required: true, message: '请选择分类', trigger: 'change' }]
    }

    const handleSubmit = async () => {
      await productFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const response = await api.post('/products', productForm)
            if (response.data.code === 200) {
              ElMessage.success('发布成功')
              resetForm()
            } else {
              ElMessage.error(response.data.message || '发布失败')
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || error.message || '发布失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const resetForm = () => {
      productFormRef.value.resetFields()
    }

    return {
      productForm,
      rules,
      productFormRef,
      loading,
      handleSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>
.publish-container {
  padding: 20px;
}

.publish-container h2 {
  margin-bottom: 20px;
}
</style>
