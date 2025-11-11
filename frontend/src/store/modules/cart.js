import api from '@/api'

const state = {
  cartItems: []
}

const mutations = {
  SET_CART_ITEMS(state, items) {
    state.cartItems = items
  }
}

const actions = {
  async fetchCartItems({ commit }) {
    const response = await api.get('/cart')
    if (response.data.code === 200) {
      commit('SET_CART_ITEMS', response.data.data)
    }
  },
  async addToCart({ dispatch }, { productId, quantity }) {
    const response = await api.post('/cart/add', { productId, quantity })
    if (response.data.code === 200) {
      await dispatch('fetchCartItems')
      return response.data
    }
    throw new Error(response.data.message)
  },
  async updateCartItem({ dispatch }, { id, quantity }) {
    const response = await api.put(`/cart/${id}`, { quantity })
    if (response.data.code === 200) {
      await dispatch('fetchCartItems')
      return response.data
    }
    throw new Error(response.data.message)
  },
  async removeCartItem({ dispatch }, id) {
    const response = await api.delete(`/cart/${id}`)
    if (response.data.code === 200) {
      await dispatch('fetchCartItems')
      return response.data
    }
    throw new Error(response.data.message)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
