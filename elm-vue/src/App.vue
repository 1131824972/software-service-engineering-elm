<script setup>
import { computed, onMounted, ref } from 'vue';

const apiRoot = 'http://localhost:9000';
const demoUserId = '10001';

function asset(name) {
  return new URL(`./assets/img/${name}`, import.meta.url).href;
}

const bannerImg = asset('index_banner.png');
const memberImg = asset('super_member.png');
const alipayImg = asset('alipay.png');
const wechatImg = asset('wechat.png');
const categoryImages = ['dcfl01.png', 'dcfl02.png', 'dcfl03.png', 'dcfl04.png', 'dcfl05.png'];
const shopImages = ['sj01.png', 'sj02.png', 'sj03.png', 'sj04.png', 'sj05.png', 'sj06.png'];
const foodImages = ['sp01.png', 'sp02.png', 'sp03.png', 'sp04.png', 'sp05.png', 'sp06.png', 'sp07.png'];

const fallbackCategories = [
  { id: 1, name: '美食', icon: 'food' },
  { id: 2, name: '早餐', icon: 'breakfast' },
  { id: 3, name: '跑腿代购', icon: 'delivery' },
  { id: 4, name: '汉堡披萨', icon: 'burger' },
  { id: 5, name: '甜品饮品', icon: 'drink' }
];

const fallbackBusinesses = [
  { id: 1, categoryId: 1, name: '万家饺子（软件园E18店）', description: '各种饺子、炒菜、家常菜', rating: 4.9, monthlySales: 345, startPrice: 15, deliveryFee: 3, deliveryTime: '30分钟', cartQuantity: 2 },
  { id: 2, categoryId: 1, name: '小锅饭豆腐馆（全运店）', description: '米饭套餐、豆腐锅、东北菜', rating: 4.7, monthlySales: 108, startPrice: 15, deliveryFee: 3, deliveryTime: '35分钟', cartQuantity: 0 },
  { id: 3, categoryId: 4, name: '麦当劳麦乐送（全运路店）', description: '汉堡、小食、饮品、套餐', rating: 4.8, monthlySales: 260, startPrice: 15, deliveryFee: 3, deliveryTime: '28分钟', cartQuantity: 1 },
  { id: 4, categoryId: 2, name: '米村拌饭（浑南店）', description: '拌饭、汤饭、韩式小食', rating: 4.6, monthlySales: 189, startPrice: 15, deliveryFee: 3, deliveryTime: '32分钟', cartQuantity: 0 }
];

const fallbackFoods = [
  { id: 1, businessId: 1, name: '纯肉鲜肉（水饺）', description: '新鲜猪肉手工水饺', price: 15, quantity: 0 },
  { id: 2, businessId: 1, name: '玉米鲜肉（水饺）', description: '玉米鲜肉水饺', price: 16, quantity: 0 },
  { id: 3, businessId: 1, name: '虾仁三鲜（蒸饺）', description: '虾仁三鲜蒸饺', price: 22, quantity: 0 },
  { id: 4, businessId: 1, name: '素三鲜（蒸饺）', description: '韭菜鸡蛋素三鲜', price: 15, quantity: 0 },
  { id: 5, businessId: 2, name: '招牌小锅饭', description: '热气小锅饭套餐', price: 18, quantity: 0 },
  { id: 6, businessId: 3, name: '巨无霸套餐', description: '汉堡薯条饮品组合', price: 39, quantity: 0 },
  { id: 7, businessId: 4, name: '石锅拌饭', description: '经典韩式拌饭', price: 24, quantity: 0 }
];

const fallbackAddresses = [
  { id: 1, userId: demoUserId, contactName: '张三', contactSex: '男', contactPhone: '13800000001', address: '沈阳市浑南区软件园E18' },
  { id: 2, userId: demoUserId, contactName: '李四', contactSex: '女', contactPhone: '13800000002', address: '沈阳市浑南区创新路1号' }
];

const view = ref('home');
const loading = ref(false);
const notice = ref('');
const keyword = ref('');
const selectedCategoryId = ref(1);
const selectedBusiness = ref(null);
const selectedAddressId = ref(1);
const currentUser = ref(null);
const currentOrder = ref(null);
const selectedPayMethod = ref('ALIPAY');
const expandedOrders = ref(new Set());

const categories = ref([]);
const businesses = ref([]);
const foods = ref([]);
const cartItems = ref([]);
const addresses = ref([]);
const orders = ref([]);

const loginForm = ref({ userId: demoUserId, password: '123456' });
const registerForm = ref({ userId: '', password: '', userName: '', phone: '' });
const addressForm = ref({ contactName: '', contactSex: '男', contactPhone: '', address: '' });
const editingAddress = ref(null);

const userId = computed(() => currentUser.value?.userId || demoUserId);
const selectedCategoryName = computed(() => categories.value.find((item) => item.id === selectedCategoryId.value)?.name || '推荐商家');
const filteredBusinesses = computed(() => {
  const key = keyword.value.trim();
  return businesses.value.filter((item) => {
    const categoryMatched = !selectedCategoryId.value || item.categoryId === selectedCategoryId.value;
    const keywordMatched = !key || item.name.includes(key) || item.description.includes(key);
    return categoryMatched && keywordMatched;
  });
});
const cartCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0));
const cartTotal = computed(() => cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0));
const deliveryFee = computed(() => selectedBusiness.value?.deliveryFee || 0);
const payableTotal = computed(() => cartTotal.value + (cartItems.value.length ? deliveryFee.value : 0));
const selectedAddress = computed(() => addresses.value.find((item) => item.id === selectedAddressId.value) || addresses.value[0]);

const pageTitle = computed(() => ({
  home: '饿了么外卖平台',
  list: selectedCategoryName.value,
  detail: selectedBusiness.value?.name || '商家详情',
  checkout: '确认订单',
  payment: '在线支付',
  orders: '我的订单',
  addresses: '收货地址',
  addressForm: editingAddress.value ? '编辑地址' : '新增地址',
  login: '用户登录',
  register: '用户注册'
}[view.value] || '饿了么外卖平台'));

function imageFrom(list, id) {
  return asset(list[(Number(id) - 1) % list.length]);
}

async function requestData(path, options = {}) {
  const response = await fetch(`${apiRoot}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok || (body.code && body.code !== 200)) {
    const message = body.message || `HTTP ${response.status}`;
    const error = new Error(message);
    error.status = body.code || response.status;
    throw error;
  }
  return body.data ?? body;
}

async function safeLoad(loader, fallback, message) {
  try {
    return await loader();
  } catch (error) {
    notice.value = message;
    return fallback;
  }
}

function formatMoney(value) {
  return `¥${Number(value || 0).toFixed(0)}`;
}

async function initHome() {
  loading.value = true;
  notice.value = '';
  categories.value = await safeLoad(
    () => requestData('/api/business/categories'),
    fallbackCategories,
    'business-service 暂不可用，当前展示本地演示分类。'
  );
  await loadBusinesses(selectedCategoryId.value || categories.value[0]?.id || 1);
  loading.value = false;
}

async function loadBusinesses(categoryId) {
  selectedCategoryId.value = categoryId;
  businesses.value = await safeLoad(
    () => requestData(`/api/business/list?categoryId=${categoryId}`),
    fallbackBusinesses.filter((item) => item.categoryId === categoryId),
    '商家列表接口暂不可用，当前展示本地演示商家。'
  );
}

async function openBusiness(business) {
  selectedBusiness.value = business;
  view.value = 'detail';
  notice.value = '';
  selectedBusiness.value = await safeLoad(
    () => requestData(`/api/business/${business.id}`),
    business,
    '商家详情接口暂不可用，当前展示本地演示数据。'
  );
  foods.value = await safeLoad(
    () => requestData(`/api/business/${business.id}/foods`),
    fallbackFoods.filter((item) => item.businessId === business.id),
    '食品列表接口暂不可用，当前展示本地演示菜单。'
  );
  await loadCart();
}

async function login() {
  try {
    currentUser.value = await requestData('/api/user/login', {
      method: 'POST',
      body: JSON.stringify(loginForm.value)
    });
    notice.value = '登录成功，已进入真实接口演示流程。';
    view.value = 'home';
    await initHome();
  } catch (error) {
    notice.value = `登录失败：${error.message}`;
  }
}

async function register() {
  try {
    currentUser.value = await requestData('/api/user/register', {
      method: 'POST',
      body: JSON.stringify(registerForm.value)
    });
    notice.value = '注册成功。';
    view.value = 'home';
  } catch (error) {
    notice.value = `注册失败：${error.message}`;
  }
}

function requireLogin() {
  if (currentUser.value) return true;
  notice.value = '请先登录后继续操作。演示账号：10001，密码：123456。';
  view.value = 'login';
  return false;
}

async function loadCart() {
  if (!selectedBusiness.value) return;
  cartItems.value = await safeLoad(
    () => requestData(`/api/cart?userId=${userId.value}&businessId=${selectedBusiness.value.id}`),
    cartItems.value,
    '购物车接口暂不可用，当前保留页面内临时购物车。'
  );
}

async function addFood(food) {
  if (!requireLogin()) return;
  try {
    cartItems.value = await requestData('/api/cart', {
      method: 'POST',
      body: JSON.stringify({
        userId: userId.value,
        businessId: selectedBusiness.value.id,
        foodId: food.id,
        foodName: food.name,
        price: food.price,
        quantity: 1
      })
    });
  } catch (error) {
    const existed = cartItems.value.find((item) => item.foodId === food.id);
    if (existed) {
      existed.quantity += 1;
    } else {
      cartItems.value.push({ id: Date.now(), userId: userId.value, businessId: selectedBusiness.value.id, foodId: food.id, foodName: food.name, price: food.price, quantity: 1 });
    }
    notice.value = '购物车接口暂不可用，当前使用页面临时购物车。';
  }
}

async function decreaseCartItem(item) {
  try {
    cartItems.value = await requestData(`/api/cart/${item.id}/decrease`, { method: 'POST' });
  } catch (error) {
    if (item.quantity > 1) item.quantity -= 1;
    else cartItems.value = cartItems.value.filter((cart) => cart.id !== item.id);
  }
}

async function goCheckout() {
  if (!requireLogin()) return;
  if (!cartItems.value.length) {
    notice.value = '请先选择商品。';
    return;
  }
  await loadAddresses();
  view.value = 'checkout';
}

async function loadAddresses() {
  addresses.value = await safeLoad(
    () => requestData(`/api/address?userId=${userId.value}`),
    fallbackAddresses,
    '地址接口暂不可用，当前展示本地演示地址。'
  );
  if (!selectedAddressId.value && addresses.value.length) selectedAddressId.value = addresses.value[0].id;
}

function startAddAddress() {
  editingAddress.value = null;
  addressForm.value = { contactName: '', contactSex: '男', contactPhone: '', address: '' };
  view.value = 'addressForm';
}

function startEditAddress(address) {
  editingAddress.value = address;
  addressForm.value = { ...address };
  view.value = 'addressForm';
}

async function saveAddress() {
  const payload = { userId: userId.value, ...addressForm.value };
  try {
    const path = editingAddress.value ? `/api/address/${editingAddress.value.id}` : '/api/address';
    const method = editingAddress.value ? 'PUT' : 'POST';
    await requestData(path, { method, body: JSON.stringify(payload) });
    await loadAddresses();
  } catch (error) {
    const saved = { id: editingAddress.value?.id || Date.now(), ...payload };
    addresses.value = editingAddress.value ? addresses.value.map((item) => (item.id === saved.id ? saved : item)) : [...addresses.value, saved];
    notice.value = '地址接口暂不可用，当前仅在页面内保存。';
  }
  view.value = 'addresses';
}

async function deleteAddress(address) {
  try {
    await requestData(`/api/address/${address.id}`, { method: 'DELETE' });
    await loadAddresses();
  } catch (error) {
    addresses.value = addresses.value.filter((item) => item.id !== address.id);
    notice.value = '地址删除接口暂不可用，当前仅在页面内删除。';
  }
}

async function createOrder() {
  if (!selectedAddress.value) {
    notice.value = '请先选择配送地址。';
    return;
  }
  try {
    currentOrder.value = await requestData('/api/order', {
      method: 'POST',
      body: JSON.stringify({ userId: userId.value, businessId: selectedBusiness.value.id, addressId: selectedAddress.value.id })
    });
  } catch (error) {
    currentOrder.value = {
      id: Date.now(),
      userId: userId.value,
      businessId: selectedBusiness.value.id,
      businessName: selectedBusiness.value.name,
      addressId: selectedAddress.value.id,
      addressText: `${selectedAddress.value.contactName} ${selectedAddress.value.contactPhone} ${selectedAddress.value.address}`,
      totalPrice: payableTotal.value,
      status: 'UNPAID',
      payMethod: null,
      items: cartItems.value.map((item) => ({ foodId: item.foodId, foodName: item.foodName, price: item.price, quantity: item.quantity }))
    };
    notice.value = '订单接口暂不可用，当前创建页面演示订单。';
  }
  view.value = 'payment';
}

async function pay(method) {
  if (!currentOrder.value) return;
  selectedPayMethod.value = method;
  try {
    currentOrder.value = await requestData(`/api/order/${currentOrder.value.id}/pay`, {
      method: 'POST',
      body: JSON.stringify({ payMethod: method })
    });
  } catch (error) {
    currentOrder.value = { ...currentOrder.value, status: 'PAID', payMethod: method };
    notice.value = '支付接口暂不可用，当前展示页面演示支付结果。';
  }
  await loadOrders();
  view.value = 'orders';
}

async function loadOrders() {
  orders.value = await safeLoad(
    () => requestData(`/api/order?userId=${userId.value}`),
    currentOrder.value ? [currentOrder.value] : [],
    '历史订单接口暂不可用，当前展示页面订单。'
  );
}

function toggleOrder(orderId) {
  const next = new Set(expandedOrders.value);
  if (next.has(orderId)) next.delete(orderId);
  else next.add(orderId);
  expandedOrders.value = next;
}

function goHome() {
  view.value = 'home';
  initHome();
}

function goOrders() {
  if (!requireLogin()) return;
  view.value = 'orders';
  loadOrders();
}

function goAddresses() {
  if (!requireLogin()) return;
  view.value = 'addresses';
  loadAddresses();
}

onMounted(initHome);
</script>

<template>
  <main class="app-shell">
    <header class="top-bar">
      <button v-if="view !== 'home'" class="back-button" type="button" @click="goHome">‹</button>
      <div class="location-block">
        <span>沈阳市浑南区</span>
        <h1>{{ pageTitle }}</h1>
      </div>
      <button v-if="!currentUser" class="login-pill" type="button" @click="view = 'login'">登录</button>
      <button v-else class="login-pill" type="button" @click="goAddresses">{{ currentUser.userName || currentUser.userId }}</button>
    </header>

    <p v-if="notice" class="notice">{{ notice }}</p>

    <section v-if="view === 'home'" class="screen home-view">
      <div class="address-card">
        <strong>配送至 软件园 E18</strong>
        <span>{{ currentUser?.phone || '13800000001' }}</span>
      </div>

      <label class="search-box">
        <span>搜索</span>
        <input v-model="keyword" type="search" placeholder="搜索商家或食品" />
      </label>

      <section class="category-grid">
        <button v-for="(category, index) in categories" :key="category.id" type="button" :class="['category-card', { active: selectedCategoryId === category.id }]" @click="loadBusinesses(category.id)">
          <img :src="imageFrom(categoryImages, index + 1)" :alt="category.name" />
          <span>{{ category.name }}</span>
        </button>
      </section>

      <section class="promo-banner">
        <img :src="bannerImg" alt="品质套餐" />
        <div>
          <h2>品质套餐</h2>
          <p>搭配齐全，准时送达</p>
          <strong>立即下单</strong>
        </div>
      </section>

      <section class="member-strip">
        <img :src="memberImg" alt="超级会员" />
        <span><strong>超级会员</strong> 每月享超值权益</span>
      </section>

      <div class="section-title">
        <h2>推荐商家</h2>
        <button type="button" @click="view = 'list'">查看全部</button>
      </div>

      <article v-for="business in filteredBusinesses" :key="business.id" class="business-card" @click="openBusiness(business)">
        <img :src="imageFrom(shopImages, business.id)" :alt="business.name" />
        <div>
          <div class="card-title">
            <h3>{{ business.name }}</h3>
            <em v-if="business.cartQuantity">{{ business.cartQuantity }}</em>
          </div>
          <p>{{ business.description }}</p>
          <div class="meta-line"><span>评分 {{ business.rating }}</span><span>月售 {{ business.monthlySales }}</span><span>{{ business.deliveryTime }}</span></div>
          <div class="price-line"><span>{{ formatMoney(business.startPrice) }} 起送</span><span>配送费 {{ formatMoney(business.deliveryFee) }}</span></div>
        </div>
      </article>
    </section>

    <section v-if="view === 'list'" class="screen">
      <div class="filter-tabs">
        <button v-for="category in categories" :key="category.id" type="button" :class="{ active: selectedCategoryId === category.id }" @click="loadBusinesses(category.id)">{{ category.name }}</button>
      </div>
      <article v-for="business in filteredBusinesses" :key="business.id" class="business-card" @click="openBusiness(business)">
        <img :src="imageFrom(shopImages, business.id)" :alt="business.name" />
        <div>
          <h3>{{ business.name }}</h3>
          <p>{{ business.description }}</p>
          <div class="meta-line"><span>评分 {{ business.rating }}</span><span>月售 {{ business.monthlySales }}</span></div>
          <div class="price-line"><span>{{ formatMoney(business.startPrice) }} 起送</span><span>配送费 {{ formatMoney(business.deliveryFee) }}</span></div>
        </div>
      </article>
    </section>

    <section v-if="view === 'detail' && selectedBusiness" class="screen detail-view">
      <section class="shop-hero">
        <img :src="imageFrom(shopImages, selectedBusiness.id)" :alt="selectedBusiness.name" />
        <div>
          <h2>{{ selectedBusiness.name }}</h2>
          <p>{{ selectedBusiness.description }}</p>
          <div class="meta-line"><span>评分 {{ selectedBusiness.rating }}</span><span>{{ selectedBusiness.deliveryTime }}</span><span>配送费 {{ formatMoney(selectedBusiness.deliveryFee) }}</span></div>
        </div>
      </section>

      <div class="section-title"><h2>菜单</h2><span>{{ foods.length }} 款商品</span></div>
      <article v-for="food in foods" :key="food.id" class="food-card">
        <img :src="imageFrom(foodImages, food.id)" :alt="food.name" />
        <div>
          <h3>{{ food.name }}</h3>
          <p>{{ food.description }}</p>
          <strong>{{ formatMoney(food.price) }}</strong>
        </div>
        <button type="button" @click="addFood(food)">+</button>
      </article>

      <section v-if="cartItems.length" class="cart-detail">
        <h2>购物车</h2>
        <div v-for="item in cartItems" :key="item.id" class="row-line">
          <span>{{ item.foodName }}</span>
          <div class="stepper">
            <button type="button" @click="decreaseCartItem(item)">-</button>
            <strong>{{ item.quantity }}</strong>
            <button type="button" @click="addFood({ id: item.foodId, name: item.foodName, price: item.price })">+</button>
          </div>
        </div>
      </section>

      <footer class="cart-bar">
        <div><strong>{{ formatMoney(payableTotal) }}</strong><span>{{ cartCount }} 件商品，含配送费 {{ formatMoney(deliveryFee) }}</span></div>
        <button type="button" @click="goCheckout">去结算</button>
      </footer>
    </section>

    <section v-if="view === 'checkout'" class="screen">
      <div class="section-title"><h2>确认订单</h2><button type="button" @click="startAddAddress">新增地址</button></div>
      <label v-for="address in addresses" :key="address.id" :class="['address-option', { active: selectedAddressId === address.id }]">
        <input v-model="selectedAddressId" type="radio" :value="address.id" />
        <span><strong>{{ address.address }}</strong><br />{{ address.contactName }} {{ address.contactPhone }}</span>
      </label>
      <section class="summary-card">
        <h2>{{ selectedBusiness?.name }}</h2>
        <div v-for="item in cartItems" :key="item.id" class="row-line"><span>{{ item.foodName }} × {{ item.quantity }}</span><strong>{{ formatMoney(item.price * item.quantity) }}</strong></div>
        <div class="row-line"><span>配送费</span><strong>{{ formatMoney(deliveryFee) }}</strong></div>
        <div class="total-line"><span>合计</span><strong>{{ formatMoney(payableTotal) }}</strong></div>
      </section>
      <button class="primary-action" type="button" @click="createOrder">提交订单</button>
    </section>

    <section v-if="view === 'payment' && currentOrder" class="screen payment-view">
      <section class="payment-card">
        <span>订单编号 {{ currentOrder.id }}</span>
        <h2>{{ formatMoney(currentOrder.totalPrice) }}</h2>
        <p>{{ currentOrder.businessName }}</p>
        <small>{{ currentOrder.addressText }}</small>
      </section>
      <button :class="['pay-method', { active: selectedPayMethod === 'ALIPAY' }]" type="button" @click="selectedPayMethod = 'ALIPAY'"><img :src="alipayImg" alt="支付宝" />支付宝支付</button>
      <button :class="['pay-method', { active: selectedPayMethod === 'WECHAT' }]" type="button" @click="selectedPayMethod = 'WECHAT'"><img :src="wechatImg" alt="微信" />微信支付</button>
      <button class="primary-action" type="button" @click="pay(selectedPayMethod)">确认支付</button>
    </section>

    <section v-if="view === 'orders'" class="screen">
      <div class="section-title"><h2>我的订单</h2><button type="button" @click="loadOrders">刷新</button></div>
      <article v-for="order in orders" :key="order.id" class="order-card">
        <header @click="toggleOrder(order.id)">
          <div><h3>{{ order.businessName }}</h3><span>{{ order.status === 'PAID' ? '已支付' : '未支付' }}</span></div>
          <strong>{{ formatMoney(order.totalPrice) }}</strong>
        </header>
        <div v-if="expandedOrders.has(order.id)" class="order-body">
          <p>{{ order.addressText }}</p>
          <div v-for="item in order.items" :key="item.foodId" class="row-line"><span>{{ item.foodName }} × {{ item.quantity }}</span><strong>{{ formatMoney(item.price * item.quantity) }}</strong></div>
        </div>
      </article>
      <div v-if="!orders.length" class="empty-state">暂无订单</div>
    </section>

    <section v-if="view === 'addresses'" class="screen">
      <div class="section-title"><h2>收货地址</h2><button type="button" @click="startAddAddress">新增</button></div>
      <article v-for="address in addresses" :key="address.id" class="address-card-row">
        <div><h3>{{ address.contactName }} {{ address.contactPhone }}</h3><p>{{ address.address }}</p></div>
        <div><button type="button" @click="startEditAddress(address)">编辑</button><button type="button" @click="deleteAddress(address)">删除</button></div>
      </article>
    </section>

    <section v-if="view === 'addressForm'" class="screen form-view">
      <label>联系人<input v-model="addressForm.contactName" type="text" /></label>
      <label>性别<select v-model="addressForm.contactSex"><option>男</option><option>女</option></select></label>
      <label>电话<input v-model="addressForm.contactPhone" type="tel" /></label>
      <label>地址<input v-model="addressForm.address" type="text" /></label>
      <button class="primary-action" type="button" @click="saveAddress">保存地址</button>
    </section>

    <section v-if="view === 'login'" class="screen form-view">
      <p class="form-tip">演示账号：10001 / 123456</p>
      <label>账号<input v-model="loginForm.userId" type="text" /></label>
      <label>密码<input v-model="loginForm.password" type="password" /></label>
      <button class="primary-action" type="button" @click="login">登录</button>
      <button class="link-action" type="button" @click="view = 'register'">没有账号，去注册</button>
    </section>

    <section v-if="view === 'register'" class="screen form-view">
      <label>账号<input v-model="registerForm.userId" type="text" /></label>
      <label>密码<input v-model="registerForm.password" type="password" /></label>
      <label>姓名<input v-model="registerForm.userName" type="text" /></label>
      <label>电话<input v-model="registerForm.phone" type="tel" /></label>
      <button class="primary-action" type="button" @click="register">注册</button>
    </section>

    <nav class="bottom-nav">
      <button :class="{ active: ['home', 'list', 'detail'].includes(view) }" type="button" @click="goHome">首页</button>
      <button :class="{ active: view === 'orders' }" type="button" @click="goOrders">订单</button>
      <button :class="{ active: ['addresses', 'login', 'register'].includes(view) }" type="button" @click="goAddresses">我的</button>
    </nav>
  </main>
</template>
