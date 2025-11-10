import api from '@/api'

const state = {
  token: localStorage.getItem('token') || '',
  username: localStorage.getItem('username') || ''
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    localStorage.setItem('token', token)
  },
  SET_USERNAME(state, username) {
    state.username = username
    localStorage.setItem('username', username)
  },
  CLEAR_AUTH(state) {
    state.token = ''
    state.username = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }
}

const actions = {
  async login({ commit }, { username, password }) {
    const response = await api.post('/auth/login', { username, password })
    if (response.data.code === 200) {
      commit('SET_TOKEN', response.data.data.token)
      commit('SET_USERNAME', response.data.data.username)
      return response.data
    }
    throw new Error(response.data.message)
  },
  async register({ commit }, userData) {
    const response = await api.post('/auth/register', userData)
    if (response.data.code === 200) {
      commit('SET_TOKEN', response.data.data.token)
      commit('SET_USERNAME', response.data.data.username)
      return response.data
    }
    throw new Error(response.data.message)
  },
  logout({ commit }) {
    commit('CLEAR_AUTH')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

