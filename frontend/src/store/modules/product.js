import api from '@/api'

const state = {
  products: [],
  currentProduct: null
}

const mutations = {
  SET_PRODUCTS(state, products) {
    state.products = products
  },
  SET_CURRENT_PRODUCT(state, product) {
    state.currentProduct = product
  }
}

const actions = {
  async fetchProducts({ commit }, params = {}) {
    const response = await api.get('/products', { params })
    if (response.data.code === 200) {
      commit('SET_PRODUCTS', response.data.data)
    }
  },
  async searchProducts({ commit }, keyword) {
    const response = await api.get('/products/search', { params: { keyword } })
    if (response.data.code === 200) {
      commit('SET_PRODUCTS', response.data.data)
    }
  },
  async createProduct({ dispatch }, productData) {
    const response = await api.post('/products', productData)
    if (response.data.code === 200) {
      await dispatch('fetchProducts')
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

