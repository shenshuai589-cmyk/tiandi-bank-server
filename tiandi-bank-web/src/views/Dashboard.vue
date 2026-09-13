<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()

const userId = Number(localStorage.getItem('userId'))
const username = localStorage.getItem('username') || ''
const realName = localStorage.getItem('realName') || ''

const activeTab = ref('overview')
const tabs = [
  { key: 'overview', label: '总览', icon: '殿' },
  { key: 'deposit', label: '存款', icon: '入' },
  { key: 'withdraw', label: '取款', icon: '出' },
  { key: 'transfer', label: '转账', icon: '通' },
  { key: 'history', label: '交易记录', icon: '卷' },
  { key: 'security', label: '账户安全', icon: '锁' }
]

/* ========== 账户信息 ========== */
const account = ref(null)
const accountLoading = ref(true)
const accountError = ref('')
const openingAccount = ref(false)

const loadAccount = async () => {
  accountLoading.value = true
  accountError.value = ''
  try {
    const res = await request.get('/account/info', { params: { userId } })
    account.value = res.data.data
  } catch (error) {
    account.value = null
    accountError.value = error.message || '账户信息加载失败'
  } finally {
    accountLoading.value = false
  }
}

const openAccount = async () => {
  openingAccount.value = true
  try {
    await request.post('/account/open', null, { params: { userId } })
    await loadAccount()
  } catch (error) {
    accountError.value = error.message || '开户失败，请稍后再试'
  } finally {
    openingAccount.value = false
  }
}

/* ========== 交易记录 ========== */
const transactions = ref([])
const txLoading = ref(true)

const typeMeta = {
  1: { label: '存款', badge: 'deposit', icon: '＋', sign: 'in' },
  2: { label: '取款', badge: 'withdraw', icon: '－', sign: 'out' },
  3: { label: '转账转出', badge: 'transfer-out', icon: '↗', sign: 'out' },
  4: { label: '转账转入', badge: 'transfer-in', icon: '↘', sign: 'in' }
}

const loadTransactions = async () => {
  txLoading.value = true
  try {
    const res = await request.get('/transaction/list', { params: { userId } })
    transactions.value = (res.data.data || []).sort(
      (a, b) => new Date(b.createTime) - new Date(a.createTime)
    )
  } catch (error) {
    transactions.value = []
  } finally {
    txLoading.value = false
  }
}

const recentTransactions = computed(() => transactions.value.slice(0, 5))

const formatMoney = (val) =>
  Number(val ?? 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

const formatTime = (val) => (val ? String(val).replace('T', ' ').slice(0, 16) : '')

/* ========== 存款 ========== */
const depositForm = reactive({ amount: '' })
const depositState = reactive({ loading: false, error: '', success: '' })

const submitDeposit = async () => {
  depositState.error = ''
  depositState.success = ''
  if (!depositForm.amount || Number(depositForm.amount) <= 0) {
    depositState.error = '请输入大于 0 的存款金额'
    return
  }
  depositState.loading = true
  try {
    await request.post('/account/deposit', { userId, amount: depositForm.amount })
    depositState.success = `存款成功，已存入 ¥${formatMoney(depositForm.amount)}`
    depositForm.amount = ''
    await Promise.all([loadAccount(), loadTransactions()])
  } catch (error) {
    depositState.error = error.message || '存款失败，请稍后再试'
  } finally {
    depositState.loading = false
  }
}

/* ========== 取款 ========== */
const withdrawForm = reactive({ amount: '' })
const withdrawState = reactive({ loading: false, error: '', success: '' })

const submitWithdraw = async () => {
  withdrawState.error = ''
  withdrawState.success = ''
  if (!withdrawForm.amount || Number(withdrawForm.amount) <= 0) {
    withdrawState.error = '请输入大于 0 的取款金额'
    return
  }
  withdrawState.loading = true
  try {
    await request.post('/account/withdraw', { userId, amount: withdrawForm.amount })
    withdrawState.success = `取款成功，已取出 ¥${formatMoney(withdrawForm.amount)}`
    withdrawForm.amount = ''
    await Promise.all([loadAccount(), loadTransactions()])
  } catch (error) {
    withdrawState.error = error.message || '取款失败，请稍后再试'
  } finally {
    withdrawState.loading = false
  }
}

/* ========== 转账 ========== */
const transferForm = reactive({ toUserId: '', amount: '' })
const transferState = reactive({ loading: false, error: '', success: '' })

const submitTransfer = async () => {
  transferState.error = ''
  transferState.success = ''
  if (!transferForm.toUserId) {
    transferState.error = '请输入对方用户ID'
    return
  }
  if (!transferForm.amount || Number(transferForm.amount) <= 0) {
    transferState.error = '请输入大于 0 的转账金额'
    return
  }
  transferState.loading = true
  try {
    await request.post('/account/transfer', {
      fromUserId: userId,
      toUserId: Number(transferForm.toUserId),
      amount: transferForm.amount
    })
    transferState.success = `转账成功，已汇出 ¥${formatMoney(transferForm.amount)}`
    transferForm.toUserId = ''
    transferForm.amount = ''
    await Promise.all([loadAccount(), loadTransactions()])
  } catch (error) {
    transferState.error = error.message || '转账失败，请稍后再试'
  } finally {
    transferState.loading = false
  }
}

/* ========== 修改密码 ========== */
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmNewPassword: '' })
const pwdState = reactive({ loading: false, error: '', success: '' })

const submitPassword = async () => {
  pwdState.error = ''
  pwdState.success = ''
  if (!pwdForm.oldPassword || !pwdForm.newPassword || !pwdForm.confirmNewPassword) {
    pwdState.error = '请完整填写密码信息'
    return
  }
  pwdState.loading = true
  try {
    await request.put('/user/password', { ...pwdForm })
    pwdState.success = '密码修改成功'
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmNewPassword = ''
  } catch (error) {
    pwdState.error = error.message || '密码修改失败'
  } finally {
    pwdState.loading = false
  }
}

/* ========== 退出登录 ========== */
const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  loadAccount()
  loadTransactions()
})
</script>

<template>
  <div class="bank-layout">

    <!-- 天 · 侧栏 -->
    <aside class="sidebar">
      <div>
        <div class="side-logo">
          <div class="seal gold" style="width:38px;height:38px;font-size:16px">天</div>
          <div>
            <div class="word">天地银行</div>
            <small>TIANDI BANK</small>
          </div>
        </div>

        <nav class="side-nav">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            <span class="ico">{{ tab.icon }}</span>
            {{ tab.label }}
          </button>
        </nav>
      </div>

      <div class="sidebar-bottom">
        <button class="logout-btn" @click="logout">
          <span class="ico">退</span>
          退出登录
        </button>
      </div>
    </aside>

    <!-- 地 · 主内容 -->
    <main class="main-content">

      <div class="topbar">
        <span class="topbar-title">天地银行 · 个人工作台</span>
        <div class="user-chip">
          <div class="avatar">{{ (realName || username || '客').slice(0, 1) }}</div>
          <span>{{ realName || username }}</span>
        </div>
      </div>

      <!-- ===================== 总览 ===================== -->
      <section v-if="activeTab === 'overview'">
        <div class="welcome">
          <div>
            <div class="welcome-eyebrow">吉时已到 · 欢迎回来</div>
            <h1>{{ realName || username }}，愿你财源如天河汇聚</h1>
            <p>今日亦是打理天地账户的好日子</p>
          </div>
          <button class="btn outline-btn" @click="activeTab = 'history'">查看全部流水</button>
        </div>

        <div v-if="accountLoading" class="stat-card">
          <span class="muted">正在核验账户信息…</span>
        </div>

        <div v-else-if="account" class="stat-card">
          <div>
            <div class="label"><span class="seal" style="width:30px;height:30px;font-size:13px">银</span>可用余额</div>
            <div class="amount">¥ {{ formatMoney(account.balance) }}</div>
            <div class="account-no">卡号 {{ account.accountNo }}</div>
          </div>
          <span class="status-pill" :class="account.status === 1 ? 'active' : 'frozen'">
            {{ account.status === 1 ? '账户正常' : '已冻结' }}
          </span>
        </div>

        <div v-else class="stat-card">
          <div>
            <div class="label">尚未开立天地账户</div>
            <p class="muted" style="margin:8px 0 0">{{ accountError }}</p>
          </div>
          <button class="btn btn-primary" :disabled="openingAccount" @click="openAccount">
            {{ openingAccount ? '开户中…' : '立即开户' }}
          </button>
        </div>

        <div class="card-grid">
          <div class="panel">
            <h2>近期流水</h2>
            <p class="muted">最近 5 笔往来记录</p>

            <div v-if="txLoading" class="empty">正在调阅账簿…</div>
            <div v-else-if="!recentTransactions.length" class="empty">暂无交易记录，去存一笔试试吧</div>
            <div v-else>
              <div class="tx-row" v-for="tx in recentTransactions" :key="tx.id">
                <div class="tx-left">
                  <div class="tx-badge" :class="typeMeta[tx.type]?.badge">{{ typeMeta[tx.type]?.icon }}</div>
                  <div>
                    <strong>{{ typeMeta[tx.type]?.label || '交易' }}</strong>
                    <small>{{ formatTime(tx.createTime) }}<span v-if="tx.targetAccountNo"> · 对方 {{ tx.targetAccountNo }}</span></small>
                  </div>
                </div>
                <span class="tx-amount" :class="typeMeta[tx.type]?.sign">
                  {{ typeMeta[tx.type]?.sign === 'in' ? '+' : '-' }}¥{{ formatMoney(tx.amount) }}
                </span>
              </div>
            </div>
          </div>

          <div class="panel">
            <h2>账户信息</h2>
            <p class="muted">开户人基本资料</p>
            <div class="info-row"><span>用户名</span><span>{{ username }}</span></div>
            <div class="info-row"><span>真实姓名</span><span>{{ realName || '未填写' }}</span></div>
            <div class="info-row"><span>用户ID</span><span>{{ userId }}</span></div>
            <div class="info-row" v-if="account"><span>银行卡号</span><span>{{ account.accountNo }}</span></div>
          </div>
        </div>
      </section>

      <!-- ===================== 存款 ===================== -->
      <section v-else-if="activeTab === 'deposit'" class="panel form-panel">
        <h2>存款入账</h2>
        <p class="muted">如百川入海，存款即时到账</p>

        <label>存款金额（元）</label>
        <input v-model="depositForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入存款金额" />
        <p class="amount-hint" v-if="account">当前余额 ¥{{ formatMoney(account.balance) }}</p>

        <p v-if="depositState.error" class="error">{{ depositState.error }}</p>
        <p v-if="depositState.success" class="success">{{ depositState.success }}</p>

        <button class="btn btn-primary" style="margin-top:22px" :disabled="depositState.loading" @click="submitDeposit">
          {{ depositState.loading ? '存入中…' : '确认存款' }}
        </button>
      </section>

      <!-- ===================== 取款 ===================== -->
      <section v-else-if="activeTab === 'withdraw'" class="panel form-panel">
        <h2>取款出账</h2>
        <p class="muted">取之有度，量入为出</p>

        <label>取款金额（元）</label>
        <input v-model="withdrawForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入取款金额" />
        <p class="amount-hint" v-if="account">当前余额 ¥{{ formatMoney(account.balance) }}</p>

        <p v-if="withdrawState.error" class="error">{{ withdrawState.error }}</p>
        <p v-if="withdrawState.success" class="success">{{ withdrawState.success }}</p>

        <button class="btn btn-primary" style="margin-top:22px" :disabled="withdrawState.loading" @click="submitWithdraw">
          {{ withdrawState.loading ? '取出中…' : '确认取款' }}
        </button>
      </section>

      <!-- ===================== 转账 ===================== -->
      <section v-else-if="activeTab === 'transfer'" class="panel form-panel">
        <h2>转账汇兑</h2>
        <p class="muted">一念天地间，瞬息达对方账户</p>

        <label>对方用户ID</label>
        <input v-model="transferForm.toUserId" type="number" placeholder="请输入对方的用户ID" />

        <label>转账金额（元）</label>
        <input v-model="transferForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入转账金额" />
        <p class="amount-hint" v-if="account">当前余额 ¥{{ formatMoney(account.balance) }}</p>

        <p v-if="transferState.error" class="error">{{ transferState.error }}</p>
        <p v-if="transferState.success" class="success">{{ transferState.success }}</p>

        <button class="btn btn-primary" style="margin-top:22px" :disabled="transferState.loading" @click="submitTransfer">
          {{ transferState.loading ? '转账中…' : '确认转账' }}
        </button>
      </section>

      <!-- ===================== 交易记录 ===================== -->
      <section v-else-if="activeTab === 'history'" class="panel">
        <h2>交易流水总览</h2>
        <p class="muted">按时间倒序排列，共 {{ transactions.length }} 笔</p>

        <div v-if="txLoading" class="empty">正在调阅账簿…</div>
        <div v-else-if="!transactions.length" class="empty">暂无交易记录</div>
        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>时间</th>
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
                <td>{{ typeMeta[tx.type]?.label || tx.type }}</td>
                <td class="tx-amount" :class="typeMeta[tx.type]?.sign">
                  {{ typeMeta[tx.type]?.sign === 'in' ? '+' : '-' }}¥{{ formatMoney(tx.amount) }}
                </td>
                <td>¥{{ formatMoney(tx.beforeBalance) }}</td>
                <td>¥{{ formatMoney(tx.afterBalance) }}</td>
                <td>{{ tx.targetAccountNo || '—' }}</td>
                <td>{{ tx.remark || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- ===================== 账户安全 ===================== -->
      <section v-else-if="activeTab === 'security'" class="panel form-panel">
        <h2>修改密码</h2>
        <p class="muted">定期更新密码，护账户周全</p>

        <label>原密码</label>
        <input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />

        <label>新密码</label>
        <input v-model="pwdForm.newPassword" type="password" placeholder="6-20 位新密码" />

        <label>确认新密码</label>
        <input v-model="pwdForm.confirmNewPassword" type="password" placeholder="请再次输入新密码" />

        <p v-if="pwdState.error" class="error">{{ pwdState.error }}</p>
        <p v-if="pwdState.success" class="success">{{ pwdState.success }}</p>

        <button class="btn btn-primary" style="margin-top:22px" :disabled="pwdState.loading" @click="submitPassword">
          {{ pwdState.loading ? '提交中…' : '确认修改' }}
        </button>
      </section>

    </main>
  </div>
</template>
