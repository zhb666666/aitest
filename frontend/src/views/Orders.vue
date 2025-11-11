<template>
  <div class="orders-container">
    <el-card>
      <h2>我的订单</h2>
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200"></el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiver" label="收货人" width="120"></el-table-column>
        <el-table-column prop="address" label="收货地址"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              type="primary"
              size="small"
              @click="handlePayment(scope.row)"
            >
              支付
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="danger"
              size="small"
              @click="cancelOrder(scope.row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="paymentDialogVisible" title="支付" width="500px">
      <el-form :model="paymentForm" label-width="100px">
        <el-form-item label="支付方式">
          <el-select v-model="paymentForm.paymentMethod" placeholder="请选择支付方式">
            <el-option label="支付宝" value="alipay"></el-option>
            <el-option label="微信" value="wechat"></el-option>
            <el-option label="银行卡" value="bank"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="processPayment" :loading="paymentLoading">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'Orders',
  setup() {
    const orders = ref([])
    const paymentDialogVisible = ref(false)
    const paymentLoading = ref(false)
    const currentOrder = ref(null)

    const paymentForm = ref({
      paymentMethod: 'alipay'
    })

    const fetchOrders = async () => {
      try {
        const response = await api.get('/orders')
        if (response.data.code === 200) {
          orders.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载订单失败')
      }
    }

    const getStatusText = (status) => {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '已发货',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    }

    const getStatusType = (status) => {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'info',
        3: 'success',
        4: 'danger'
      }
      return typeMap[status] || ''
    }

    const handlePayment = (order) => {
      currentOrder.value = order
      paymentDialogVisible.value = true
    }

    const processPayment = async () => {
      if (!paymentForm.value.paymentMethod) {
        ElMessage.warning('请选择支付方式')
        return
      }
      paymentLoading.value = true
      try {
        // 创建支付记录
        const paymentResponse = await api.post('/payments', {
          orderId: currentOrder.value.id,
          paymentMethod: paymentForm.value.paymentMethod
        })
        if (paymentResponse.data.code === 200) {
          const payment = paymentResponse.data.data
          // 模拟支付处理
          const processResponse = await api.post('/payments/process', {
            paymentNo: payment.paymentNo,
            transactionId: 'TXN' + Date.now()
          })
          if (processResponse.data.code === 200) {
            ElMessage.success('支付成功')
            paymentDialogVisible.value = false
            await fetchOrders()
          }
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '支付失败')
      } finally {
        paymentLoading.value = false
      }
    }

    const cancelOrder = async (orderId) => {
      try {
        const response = await api.put(`/orders/${orderId}/cancel`)
        if (response.data.code === 200) {
          ElMessage.success('订单已取消')
          await fetchOrders()
        }
      } catch (error) {
        ElMessage.error('取消订单失败')
      }
    }

    onMounted(() => {
      fetchOrders()
    })

    return {
      orders,
      paymentDialogVisible,
      paymentForm,
      paymentLoading,
      getStatusText,
      getStatusType,
      handlePayment,
      processPayment,
      cancelOrder
    }
  }
}
</script>

<style scoped>
.orders-container {
  padding: 20px;
}
</style>
