<template>
  <div class="cart-container">
    <el-card>
      <h2>购物车</h2>
      <el-table :data="cartItems" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" width="100"></el-table-column>
        <el-table-column label="商品名称" width="200">
          <template #default="scope">
            {{ getProductName(scope.row.productId) }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.quantity"
              :min="1"
              @change="updateQuantity(scope.row)"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeItem(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="cart-footer">
        <el-button type="primary" size="large" @click="checkout" :disabled="cartItems.length === 0">
          结算
        </el-button>
      </div>
    </el-card>

    <el-dialog v-model="checkoutDialogVisible" title="确认订单" width="600px">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="收货地址">
          <el-input v-model="orderForm.address" placeholder="请输入收货地址"></el-input>
        </el-form-item>
        <el-form-item label="收货人">
          <el-input v-model="orderForm.receiver" placeholder="请输入收货人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="orderForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createOrder" :loading="orderLoading">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'Cart',
  setup() {
    const store = useStore()
    const checkoutDialogVisible = ref(false)
    const orderLoading = ref(false)
    const products = ref([])

    const cartItems = computed(() => store.state.cart.cartItems)

    const orderForm = ref({
      address: '',
      receiver: '',
      phone: ''
    })

    const fetchCartItems = async () => {
      await store.dispatch('cart/fetchCartItems')
    }

    const fetchProducts = async () => {
      try {
        const response = await api.get('/products')
        if (response.data.code === 200) {
          products.value = response.data.data
        }
      } catch (error) {
        console.error('加载商品失败', error)
      }
    }

    const getProductName = (productId) => {
      const product = products.value.find(p => p.id === productId)
      return product ? product.productName : '未知商品'
    }

    const updateQuantity = async (item) => {
      try {
        await store.dispatch('cart/updateCartItem', { id: item.id, quantity: item.quantity })
        ElMessage.success('更新成功')
      } catch (error) {
        ElMessage.error(error.message || '更新失败')
      }
    }

    const removeItem = async (id) => {
      try {
        await store.dispatch('cart/removeCartItem', id)
        ElMessage.success('删除成功')
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    }

    const checkout = () => {
      if (cartItems.value.length === 0) {
        ElMessage.warning('购物车为空')
        return
      }
      checkoutDialogVisible.value = true
    }

    const createOrder = async () => {
      if (!orderForm.value.address || !orderForm.value.receiver || !orderForm.value.phone) {
        ElMessage.warning('请填写完整的收货信息')
        return
      }
      orderLoading.value = true
      try {
        const cartItemIds = cartItems.value.map(item => item.id)
        const response = await api.post('/orders', {
          cartItemIds,
          ...orderForm.value
        })
        if (response.data.code === 200) {
          ElMessage.success('订单创建成功')
          checkoutDialogVisible.value = false
          await fetchCartItems()
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '创建订单失败')
      } finally {
        orderLoading.value = false
      }
    }

    onMounted(() => {
      fetchCartItems()
      fetchProducts()
    })

    return {
      cartItems,
      checkoutDialogVisible,
      orderForm,
      orderLoading,
      getProductName,
      updateQuantity,
      removeItem,
      checkout,
      createOrder
    }
  }
}
</script>

<style scoped>
.cart-container {
  padding: 20px;
}

.cart-footer {
  margin-top: 20px;
  text-align: right;
}
</style>

