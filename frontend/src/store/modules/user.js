import api from '@/api'

const state = {
  users: [],
  currentUser: null
}

const mutations = {
  SET_USERS(state, users) {
    state.users = users
  },
  SET_CURRENT_USER(state, user) {
    state.currentUser = user
  }
}

const actions = {
  async fetchUsers({ commit }) {
    const response = await api.get('/users')
    if (response.data.code === 200) {
      commit('SET_USERS', response.data.data)
    }
  },
  async createUser({ dispatch }, userData) {
    const response = await api.post('/users', userData)
    if (response.data.code === 200) {
      await dispatch('fetchUsers')
      return response.data
    }
    throw new Error(response.data.message)
  },
  async updateUser({ dispatch }, { id, userData }) {
    const response = await api.put(`/users/${id}`, userData)
    if (response.data.code === 200) {
      await dispatch('fetchUsers')
      return response.data
    }
    throw new Error(response.data.message)
  },
  async deleteUser({ dispatch }, id) {
    const response = await api.delete(`/users/${id}`)
    if (response.data.code === 200) {
      await dispatch('fetchUsers')
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
