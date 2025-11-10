<template>
  <div class="market-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <h3>搜索商品</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="请输入商品名称"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
          <el-divider />
          <h3>商品分类</h3>
          <el-menu @select="handleCategorySelect">
            <el-menu-item index="all">全部</el-menu-item>
            <el-menu-item index="electronics">电子产品</el-menu-item>
            <el-menu-item index="clothing">服装</el-menu-item>
            <el-menu-item index="food">食品</el-menu-item>
            <el-menu-item index="books">图书</el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-row :gutter="20">
          <el-col :span="8" v-for="product in products" :key="product.id" style="margin-bottom: 20px">
            <el-card :body-style="{ padding: '0px' }">
              <img :src="product.image || '/placeholder.png'" class="product-image" />
              <div style="padding: 14px">
                <h3>{{ product.productName }}</h3>
                <p class="product-description">{{ product.description }}</p>
                <div class="product-footer">
                  <span class="product-price">¥{{ product.price }}</span>
                  <el-button type="primary" size="small" @click="addToCart(product)">加入购物车</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'Market',
  setup() {
    const store = useStore()
    const products = ref([])
    const searchKeyword = ref('')
    const currentCategory = ref('all')

    const fetchProducts = async () => {
      try {
        const response = await api.get('/products', { params: { status: 1 } })
        if (response.data.code === 200) {
          products.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载商品失败')
      }
    }

    const handleSearch = async () => {
      if (!searchKeyword.value.trim()) {
        fetchProducts()
        return
      }
      try {
        await store.dispatch('product/searchProducts', searchKeyword.value)
        products.value = store.state.product.products
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const handleCategorySelect = async (category) => {
      currentCategory.value = category
      if (category === 'all') {
        fetchProducts()
      } else {
        try {
          const response = await api.get(`/products/category/${category}`)
          if (response.data.code === 200) {
            products.value = response.data.data
          }
        } catch (error) {
          ElMessage.error('加载分类商品失败')
        }
      }
    }

    const addToCart = async (product) => {
      try {
        await store.dispatch('cart/addToCart', { productId: product.id, quantity: 1 })
        ElMessage.success('已加入购物车')
      } catch (error) {
        ElMessage.error(error.message || '加入购物车失败')
      }
    }

    onMounted(() => {
      fetchProducts()
    })

    return {
      products,
      searchKeyword,
      handleSearch,
      handleCategorySelect,
      addToCart
    }
  }
}
</script>

<style scoped>
.market-container {
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-description {
  color: #909399;
  font-size: 14px;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>

