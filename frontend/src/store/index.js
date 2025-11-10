import { createStore } from 'vuex'
import auth from './modules/auth'
import user from './modules/user'
import product from './modules/product'
import cart from './modules/cart'

export default createStore({
  modules: {
    auth,
    user,
    product,
    cart
  }
})

