<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: ''
})

const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

const handleRegister = async () => {
  errorMsg.value = ''
  successMsg.value = ''

  if (!form.username || !form.password || !form.realName || !form.phone) {
    errorMsg.value = '请填写完整信息'
    return
  }

  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true

  try {
    await request.post('/user/register', {
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone
    })

    successMsg.value = '开户成功，正在前往登录…'
    setTimeout(() => router.push('/login'), 900)
  } catch (error) {
    errorMsg.value = error.message || '注册失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

const goLogin = () => router.push('/login')
</script>

<template>
  <div class="auth-page">

    <div class="auth-brand">
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

        <p class="brand-verse">立本开源<br />福泽绵长</p>
        <p class="brand-verse-sub">开卷立户　福自此始</p>
      </div>

      <div class="brand-ledger">
        <div class="brand-ledger-item">
          <div class="seal">简</div>
          <div>
            <h3>三步开户</h3>
            <p>填写姓名、手机与密码，即可立开新户。</p>
          </div>
        </div>
        <div class="brand-ledger-item">
          <div class="seal">护</div>
          <div>
            <h3>密码加密</h3>
            <p>密码经加密留存，绝不以明文示人。</p>
          </div>
        </div>
        <div class="brand-ledger-item">
          <div class="seal">续</div>
          <div>
            <h3>随时可续</h3>
            <p>开户之后即可存取转账，账目一目了然。</p>
          </div>
        </div>
      </div>

      <div class="brand-foot">© 2026 天地银行　TIANDI BANK ALL RIGHTS RESERVED</div>
    </div>

    <div class="auth-card-wrap">
      <div class="auth-card">
        <div class="eyebrow-seal">
          <div class="seal">立</div>
          <span>开立天地银行新户</span>
        </div>

        <h2>创建账户</h2>
        <p class="sub-title">开启您的天地银行之旅</p>

        <form @submit.prevent="handleRegister">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="3-20 位，登录时使用" autocomplete="username" />

          <label>真实姓名</label>
          <input v-model="form.realName" type="text" placeholder="请输入真实姓名" autocomplete="name" />

          <label>手机号</label>
          <input v-model="form.phone" type="tel" placeholder="请输入手机号" autocomplete="tel" />

          <div class="field-row">
            <div>
              <label>密码</label>
              <input v-model="form.password" type="password" placeholder="6-20 位密码" autocomplete="new-password" />
            </div>
            <div>
              <label>确认密码</label>
              <input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" autocomplete="new-password" />
            </div>
          </div>

          <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
          <p v-if="successMsg" class="success">{{ successMsg }}</p>

          <button type="submit" class="btn btn-primary btn-block" style="margin-top:26px" :disabled="loading">
            {{ loading ? '正在开户…' : '立即注册' }}
          </button>
        </form>

        <p class="switch-text">
          已经有账户？
          <button class="text-btn" @click="goLogin">返回登录</button>
        </p>
      </div>
    </div>

  </div>
</template>
