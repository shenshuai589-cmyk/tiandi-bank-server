<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const errorMsg = ref('')
const loading = ref(false)

const handleLogin = async () => {
  errorMsg.value = ''

  if (!form.username || !form.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true

  try {
    const response = await request.post('/user/login', form)
    const data = response.data.data

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName || '')

    router.push('/dashboard')
  } catch (error) {
    errorMsg.value = error.message || '登录失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

const goRegister = () => router.push('/register')
</script>

<template>
  <div class="auth-page">

    <!-- 天 · 品牌一侧 -->
    <div class="brand-brand auth-brand">
      <div class="cloud-field" aria-hidden="true">
        <svg class="c1" viewBox="0 0 220 110" fill="none" xmlns="http://www.w3.org/2000/svg" style="color:#e3c988">
          <path d="M40 70C25 70 15 58 20 46C24 35 38 32 46 38C50 22 72 18 82 32C92 20 116 24 118 42C132 40 142 52 136 64C150 62 158 76 148 86C158 92 154 106 138 106L46 106C24 106 14 92 24 80C18 78 16 72 22 68Z" stroke="currentColor" stroke-width="2.2"/>
          <path d="M150 86C168 86 182 74 188 58" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <circle cx="192" cy="52" r="3.4" fill="currentColor"/>
        </svg>
        <svg class="c2" viewBox="0 0 220 110" fill="none" xmlns="http://www.w3.org/2000/svg" style="color:#e3c988">
          <path d="M40 70C25 70 15 58 20 46C24 35 38 32 46 38C50 22 72 18 82 32C92 20 116 24 118 42C132 40 142 52 136 64C150 62 158 76 148 86C158 92 154 106 138 106L46 106C24 106 14 92 24 80C18 78 16 72 22 68Z" stroke="currentColor" stroke-width="2.2"/>
        </svg>
        <svg class="c3" viewBox="0 0 220 110" fill="none" xmlns="http://www.w3.org/2000/svg" style="color:#e3c988">
          <path d="M40 70C25 70 15 58 20 46C24 35 38 32 46 38C50 22 72 18 82 32C92 20 116 24 118 42C132 40 142 52 136 64C150 62 158 76 148 86C158 92 154 106 138 106L46 106C24 106 14 92 24 80C18 78 16 72 22 68Z" stroke="currentColor" stroke-width="2.2"/>
          <path d="M150 86C168 86 182 74 188 58" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <circle cx="192" cy="52" r="3.4" fill="currentColor"/>
        </svg>
      </div>

      <div class="brand-top">
        <div class="brand-mark">
          <div class="seal gold">天</div>
          <div>
            <div class="word">天地银行</div>
            <small>TIANDI BANK · EST. 2026</small>
          </div>
        </div>

        <p class="brand-verse">
          一卷天书<br />存万家财
        </p>
        <p class="brand-verse-sub">经天纬地　护财有道</p>
      </div>

      <div class="brand-ledger">
        <div class="brand-ledger-item">
          <div class="seal">诚</div>
          <div>
            <h3>信守如约</h3>
            <p>每一笔存取，皆有据可查，如卷宗留痕。</p>
          </div>
        </div>
        <div class="brand-ledger-item">
          <div class="seal">安</div>
          <div>
            <h3>周天护佑</h3>
            <p>多重校验守护账户，安稳如金身不坏。</p>
          </div>
        </div>
        <div class="brand-ledger-item">
          <div class="seal">通</div>
          <div>
            <h3>畅行无阻</h3>
            <p>存款、取款、转账，一念之间，随行随办。</p>
          </div>
        </div>
      </div>

      <div class="brand-foot">© 2026 天地银行　TIANDI BANK ALL RIGHTS RESERVED</div>
    </div>

    <!-- 地 · 表单一侧 -->
    <div class="auth-card-wrap">
      <div class="auth-card">
        <div class="eyebrow-seal">
          <div class="seal">开</div>
          <span>登入天地银行账户</span>
        </div>

        <h2>欢迎回来</h2>
        <p class="sub-title">请输入账户信息，继续您的天地之旅</p>

        <form @submit.prevent="handleLogin">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" autocomplete="username" />

          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" autocomplete="current-password" />

          <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

          <button type="submit" class="btn btn-primary btn-block" style="margin-top:26px" :disabled="loading">
            {{ loading ? '正在核验…' : '登　录' }}
          </button>
        </form>

        <p class="switch-text">
          尚未开通天地账户？
          <button class="text-btn" @click="goRegister">立即注册</button>
        </p>
      </div>
    </div>

  </div>
</template>
