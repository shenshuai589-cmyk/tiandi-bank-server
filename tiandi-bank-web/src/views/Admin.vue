<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const username = localStorage.getItem('username') || ''
const realName = localStorage.getItem('realName') || ''

const activeTab = ref('users')
const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'accounts', label: '账户管理' },
  { key: 'transactions', label: '交易记录' }
]

const accessDenied = ref(false)
const accessMessage = ref('')

const users = ref([])
const accounts = ref([])
const transactions = ref([])

const usersLoading = ref(true)
const accountsLoading = ref(true)
const txLoading = ref(true)

const rowActionId = ref(null)

const formatMoney = (val) =>
  Number(val ?? 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const formatTime = (val) => (val ? String(val).replace('T', ' ').slice(0, 16) : '')

const typeLabel = {
  1: '存款',
  2: '取款',
  3: '转账转出',
  4: '转账转入'
}

const loadUsers = async () => {
  usersLoading.value = true
  try {
    const res = await request.get('/admin/user/list')
    users.value = res.data.data || []
  } catch (error) {
    accessDenied.value = true
    accessMessage.value = error.message || '暂无权限访问后台管理'
  } finally {
    usersLoading.value = false
  }
}

const loadAccounts = async () => {
  accountsLoading.value = true
  try {
    const res = await request.get('/admin/account/list')
    accounts.value = res.data.data || []
  } catch (error) {
    /* 已在用户列表处统一处理权限错误 */
  } finally {
    accountsLoading.value = false
  }
}

const loadTransactions = async () => {
  txLoading.value = true
  try {
    const res = await request.get('/admin/transaction/list')
    transactions.value = (res.data.data || []).sort(
      (a, b) => new Date(b.createTime) - new Date(a.createTime)
    )
  } catch (error) {
    /* 已在用户列表处统一处理权限错误 */
  } finally {
    txLoading.value = false
  }
}

const toggleFreeze = async (user) => {
  rowActionId.value = user.id
  try {
    const action = user.status === 1 ? 'freeze' : 'unfreeze'
    await request.put(`/admin/user/${user.id}/${action}`)
    user.status = user.status === 1 ? 0 : 1
  } catch (error) {
    window.alert(error.message || '操作失败')
  } finally {
    rowActionId.value = null
  }
}

const backToDashboard = () => router.push('/dashboard')
const backToLogin = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(async () => {
  await loadUsers()
  if (!accessDenied.value) {
    loadAccounts()
    loadTransactions()
  }
})
</script>

<template>
  <div class="admin-page">

    <div class="admin-top">
      <div>
        <div class="eyebrow-seal">
          <div class="seal gold">监</div>
          <span>天地银行 · 后台管理</span>
        </div>
        <h1>掌中执印，纵览全局账目</h1>
      </div>
      <div style="text-align:right">
        <div class="user-chip" style="margin-bottom:10px">
          <div class="avatar">{{ (realName || username || '管').slice(0, 1) }}</div>
          <span>{{ realName || username }}</span>
        </div>
        <button class="btn btn-sm outline-btn" @click="backToDashboard">返回工作台</button>
      </div>
    </div>

    <div class="admin-body">

      <div v-if="accessDenied" class="panel" style="max-width:520px;margin:40px auto;text-align:center">
        <div class="seal" style="margin:0 auto 18px">禁</div>
        <h2 style="margin:0 0 8px">未持印信，不得入内</h2>
        <p class="muted">{{ accessMessage }}</p>
        <p class="muted">此页面仅限管理员账户（role = 1）访问。</p>
        <div style="display:flex;gap:12px;justify-content:center;margin-top:18px">
          <button class="btn btn-outline" @click="backToDashboard">返回工作台</button>
          <button class="btn btn-danger" @click="backToLogin">切换账户</button>
        </div>
      </div>

      <template v-else>
        <div class="admin-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="table-panel">
          <div class="table-wrap">
            <div v-if="usersLoading" class="empty">正在调阅用户名册…</div>
            <table v-else>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>真实姓名</th>
                  <th>手机号</th>
                  <th>角色</th>
                  <th>状态</th>
                  <th>开户时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="u in users" :key="u.id">
                  <td>{{ u.id }}</td>
                  <td>{{ u.username }}</td>
                  <td>{{ u.realName || '—' }}</td>
                  <td>{{ u.phone || '—' }}</td>
                  <td>
                    <span class="status-pill" :class="u.role === 1 ? 'admin' : ''">
                      {{ u.role === 1 ? '管理员' : '普通用户' }}
                    </span>
                  </td>
                  <td>
                    <span class="status-pill" :class="u.status === 1 ? 'active' : 'frozen'">
                      {{ u.status === 1 ? '正常' : '已冻结' }}
                    </span>
                  </td>
                  <td>{{ formatTime(u.createTime) }}</td>
                  <td>
                    <button
                      class="text-btn"
                      :disabled="rowActionId === u.id"
                      @click="toggleFreeze(u)"
                    >
                      {{ u.status === 1 ? '冻结' : '解冻' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 账户管理 -->
        <div v-else-if="activeTab === 'accounts'" class="table-panel">
          <div class="table-wrap">
            <div v-if="accountsLoading" class="empty">正在调阅账户名册…</div>
            <table v-else>
              <thead>
                <tr>
                  <th>账户ID</th>
                  <th>所属用户ID</th>
                  <th>银行卡号</th>
                  <th>余额</th>
                  <th>状态</th>
                  <th>开户时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="a in accounts" :key="a.id">
                  <td>{{ a.id }}</td>
                  <td>{{ a.userId }}</td>
                  <td>{{ a.accountNo }}</td>
                  <td>¥{{ formatMoney(a.balance) }}</td>
                  <td>
                    <span class="status-pill" :class="a.status === 1 ? 'active' : 'frozen'">
                      {{ a.status === 1 ? '正常' : '已冻结' }}
                    </span>
                  </td>
                  <td>{{ formatTime(a.createTime) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 交易记录 -->
        <div v-else class="table-panel">
          <div class="table-wrap">
            <div v-if="txLoading" class="empty">正在调阅全局账簿…</div>
            <table v-else>
              <thead>
                <tr>
                  <th>时间</th>
                  <th>用户ID</th>
                  <th>类型</th>
                  <th>金额</th>
                  <th>交易前余额</th>
                  <th>交易后余额</th>
                  <th>对方账户</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tx in transactions" :key="tx.id">
                  <td>{{ formatTime(tx.createTime) }}</td>
                  <td>{{ tx.userId }}</td>
                  <td>{{ typeLabel[tx.type] || tx.type }}</td>
                  <td>¥{{ formatMoney(tx.amount) }}</td>
                  <td>¥{{ formatMoney(tx.beforeBalance) }}</td>
                  <td>¥{{ formatMoney(tx.afterBalance) }}</td>
                  <td>{{ tx.targetAccountNo || '—' }}</td>
                  <td>{{ tx.remark || '—' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>

    </div>
  </div>
</template>
