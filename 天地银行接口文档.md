# 天地银行（Tiandi Bank）接口文档

**版本**：v1.0  
**基础路径**：`/`  
**项目类型**：简单银行业务系统  
**认证方式**：当前版本暂未实现 JWT / Session 登录认证，接口通过 `userId` 标识用户

> 本文档按照当前天地银行项目已经实现的接口整理。
> 当前版本以 Spring Boot + MyBatis + MySQL 为核心，不引入 Redis、MQ、Spring Cloud 等复杂技术。

---

## 一、统一说明

### 1.1 请求格式

JSON 请求统一使用：

```http
Content-Type: application/json
```

GET 接口的参数通过 Query 参数传递。

例如：

```http
GET /account/info?userId=1
```

---

### 1.2 当前响应形式

当前项目还没有统一的：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

响应包装。

因此不同接口按照实际 Controller 返回类型返回：

- 查询接口：直接返回对象 JSON
- 开户接口：直接返回账户对象 JSON
- 存款/取款/转账：直接返回成功提示字符串
- 注册：按照当前 Service/Controller 实现返回对应结果

后续如果项目需要，可以再增加统一响应类，例如 `Result<T>`。

---

## 二、用户接口

### 2.1 用户注册

**接口名称**：用户注册

```http
POST /user/register
```

#### 请求体

```json
{
  "username": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "phone": "13800000001"
}
```

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 用户名 |
| password | String | 是 | 登录密码 |
| realName | String | 是 | 真实姓名 |
| phone | String | 是 | 手机号 |

#### 成功

```text
注册成功
```

#### 失败场景

```text
用户名已经存在
```

#### 业务说明

注册时服务端会先根据用户名查询用户：

```java
User existUser = userMapper.selectByUsername(
        registerDTO.getUsername()
);
```

如果已经存在，则拒绝注册。

注册成功后创建用户，并设置：

```text
role = 0
status = 1
```

其中：

- `role = 0`：普通用户
- `status = 1`：正常状态

---

### 2.2 用户登录

**接口名称**：用户登录

```http
POST /user/login
```

#### 请求体

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 用户名 |
| password | String | 是 | 登录密码 |

#### 成功响应

当前登录接口返回 `LoginVO`，包含：

```json
{
  "userId": 1,
  "username": "zhangsan",
  "realName": "张三"
}
```

#### 说明

当前版本登录主要用于完成基础用户认证。

暂未实现：

- JWT
- Redis 登录状态
- Session
- 验证码
- 密码加密

因此当前版本适合作为学习项目。

---

# 三、账户接口

## 3.1 开户

**接口名称**：用户开户

```http
POST /account/open?userId=1
```

#### 请求参数（Query）

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | Long | 是 | 用户ID |

#### 请求示例

```http
POST /account/open?userId=1
```

#### 成功响应

```json
{
  "id": 1,
  "userId": 1,
  "accountNo": "622200xxxxxxxxxx",
  "balance": 0,
  "status": 1,
  "createTime": "2026-09-10T10:00:00"
}
```

#### 失败场景

```text
该用户已经开户
```

#### 业务说明

开户前会检查该用户是否已经存在银行账户。

一个用户当前只能拥有一个银行账户。

开户时：

```text
balance = 0
status = 1
```

银行卡号使用随机方式生成，以：

```text
622200
```

作为前缀。

---

## 3.2 查询账户

**接口名称**：查询账户信息

```http
GET /account/info?userId=1
```

#### 请求参数（Query）

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | Long | 是 | 用户ID |

#### 请求示例

```http
GET /account/info?userId=1
```

#### 成功响应

```json
{
  "id": 1,
  "userId": 1,
  "accountNo": "622200xxxxxxxxxx",
  "balance": 1000.00,
  "status": 1,
  "createTime": "2026-09-10T10:00:00"
}
```

#### 失败场景

```text
您还没有银行账户
```

#### 字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 账户ID |
| userId | Long | 所属用户ID |
| accountNo | String | 银行卡号 |
| balance | BigDecimal | 当前余额 |
| status | Integer | 账户状态 |
| createTime | LocalDateTime | 开户时间 |

账户状态：

| status | 说明 |
|---|---|
| 0 | 冻结 |
| 1 | 正常 |

---

## 3.3 存款

**接口名称**：账户存款

```http
POST /account/deposit
```

#### 请求体

```json
{
  "userId": 1,
  "amount": 1000.00
}
```

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | Long | 是 | 用户ID |
| amount | BigDecimal | 是 | 存款金额，必须大于等于0.01 |

#### 成功响应

```text
存款成功
```

#### 参数校验失败

例如：

```json
{
  "userId": 1,
  "amount": 0
}
```

会触发：

```text
存款金额必须大于0
```

#### 失败场景

```text
您还没有银行账户
```

或：

```text
账户已被冻结
```

#### 核心 SQL

```sql
UPDATE bank_account
SET balance = balance + #{amount}
WHERE user_id = #{userId}
```

#### 业务说明

存款不是直接把余额设置成某个值，而是在原余额基础上增加金额。

例如：

```text
原余额：1000
存款：500
结果：1500
```

因此使用：

```http
POST
```

更符合当前业务语义。

---

## 3.4 取款

**接口名称**：账户取款

```http
POST /account/withdraw
```

#### 请求体

```json
{
  "userId": 1,
  "amount": 300.00
}
```

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | Long | 是 | 用户ID |
| amount | BigDecimal | 是 | 取款金额，必须大于等于0.01 |

#### 成功响应

```text
取款成功
```

#### 失败场景

```text
您还没有银行账户
```

```text
账户已被冻结
```

```text
余额不足
```

#### 核心 SQL

```sql
UPDATE bank_account
SET balance = balance - #{amount}
WHERE user_id = #{userId}
  AND balance >= #{amount}
```

#### 业务说明

取款时并不是：

```text
先查询余额
↓
Java 判断余额够不够
↓
再修改余额
```

而是直接通过 SQL：

```sql
AND balance >= #{amount}
```

保证只有余额充足时才能扣款。

如果 SQL 影响行数为 `0`：

```java
if (rows == 0) {
    throw new RuntimeException("余额不足");
}
```

因此可以判断取款失败。

---

## 3.5 转账

**接口名称**：账户转账

```http
POST /account/transfer
```

#### 请求体

```json
{
  "fromUserId": 1,
  "toUserId": 2,
  "amount": 200.00
}
```

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| fromUserId | Long | 是 | 转出用户ID |
| toUserId | Long | 是 | 转入用户ID |
| amount | BigDecimal | 是 | 转账金额，必须大于等于0.01 |

#### 成功响应

```text
转账成功
```

#### 失败场景

自己给自己转账：

```text
不能给自己转账
```

转出方没有账户：

```text
转出方没有银行账户
```

转入方没有账户：

```text
转入方没有银行账户
```

转出方账户被冻结：

```text
转出方账户已被冻结
```

转入方账户被冻结：

```text
转入方账户已被冻结
```

余额不足：

```text
余额不足
```

转账失败：

```text
转账失败
```

#### 请求示例

假设：

```text
用户1余额：1000
用户2余额：500
```

请求：

```json
{
  "fromUserId": 1,
  "toUserId": 2,
  "amount": 300
}
```

执行后：

```text
用户1：700
用户2：800
```

---

### 3.5.1 转账执行流程

```text
用户1发起转账
       ↓
判断是否给自己转账
       ↓
查询转出方账户
       ↓
查询转入方账户
       ↓
检查账户状态
       ↓
扣除转出方余额
       ↓
增加转入方余额
       ↓
转账成功
```

---

### 3.5.2 事务处理

转账方法使用：

```java
@Transactional
public void transfer(TransferDTO transferDTO) {
    ...
}
```

这是当前项目非常重要的业务设计。

因为转账实际上包含两个数据库操作：

```text
① 转出方扣钱

② 转入方加钱
```

如果没有事务，可能发生：

```text
张三 -300
       ↓
系统异常
       ↓
李四没有 +300
```

此时钱就会凭空消失。

使用 `@Transactional` 后：

```text
扣款成功
   ↓
收款成功
   ↓
全部提交
```

如果中间出现运行时异常：

```text
扣款成功
   ↓
收款失败
   ↓
事务回滚
   ↓
扣款也恢复
```

因此保证：

> 转账操作要么全部成功，要么全部失败。

这就是事务的原子性。

---

# 四、接口汇总

| 模块 | 接口 | 方法 | 说明 |
|---|---|---|---|
| 用户 | `/user/register` | POST | 用户注册 |
| 用户 | `/user/login` | POST | 用户登录 |
| 账户 | `/account/open` | POST | 开户 |
| 账户 | `/account/info` | GET | 查询账户 |
| 账户 | `/account/deposit` | POST | 存款 |
| 账户 | `/account/withdraw` | POST | 取款 |
| 账户 | `/account/transfer` | POST | 转账 |

---

# 五、错误码及常见业务错误

当前项目暂时没有统一的错误码体系，主要通过业务异常信息返回。

后续可以统一设计为：

| HTTP状态码 | 含义 |
|---|---|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 用户/账户不存在 |
| 409 | 业务冲突 |
| 500 | 服务器内部错误 |

常见业务错误：

| 错误信息 | 触发场景 |
|---|---|
| 用户名已经存在 | 注册重复用户名 |
| 您还没有银行账户 | 未开户就进行账户操作 |
| 该用户已经开户 | 重复开户 |
| 账户已被冻结 | 冻结账户进行操作 |
| 存款金额必须大于0 | 存款金额非法 |
| 取款金额必须大于0 | 取款金额非法 |
| 转账金额必须大于0 | 转账金额非法 |
| 不能给自己转账 | 转入转出用户相同 |
| 转出方没有银行账户 | 转出方未开户 |
| 转入方没有银行账户 | 转入方未开户 |
| 余额不足 | 账户余额不足 |

---

# 六、参数校验说明

当前项目使用 Jakarta Validation。

## 6.1 字符串参数

例如：

```java
@NotBlank(message = "用户名不能为空")
private String username;
```

`@NotBlank` 主要用于字符串。

---

## 6.2 Long 参数

例如：

```java
@NotNull(message = "用户Id不能为空")
private Long userId;
```

因为 `Long` 不是字符串，所以不能使用：

```java
@NotBlank
```

---

## 6.3 BigDecimal 金额参数

当前金额使用：

```java
@NotNull(message = "存款金额不能为空")
@DecimalMin(value = "0.01", message = "存款金额必须大于0")
private BigDecimal amount;
```

原因：

- `@NotNull`：保证金额不能为 `null`
- `@DecimalMin("0.01")`：保证金额至少为 0.01

因此：

```text
null       ❌
0          ❌
-100       ❌
0.001      ❌
0.01       ✅
100        ✅
```

---

# 七、HTTP 方法设计说明

## 7.1 为什么存款使用 POST？

存款：

```http
POST /account/deposit
```

存款本质上是：

```text
在当前余额基础上增加一笔新的资金操作
```

例如：

```text
1000 + 500 = 1500
```

如果重复请求：

```text
POST /account/deposit
```

会再次产生存款效果。

因此使用 POST 更符合当前业务语义。

---

## 7.2 为什么取款使用 POST？

取款同样属于一次新的业务操作：

```text
余额 - 300
```

不是把某个资源修改成客户端指定的固定状态。

所以使用：

```http
POST /account/withdraw
```

---

## 7.3 为什么转账使用 POST？

转账也是一次新的业务交易：

```text
账户A - 300
账户B + 300
```

因此使用：

```http
POST /account/transfer
```

比使用 PUT 更符合当前设计。

---

# 八、数据库核心业务说明

## 8.1 账户余额

账户表核心字段：

```text
id
user_id
account_no
balance
status
create_time
```

其中：

```text
balance
```

使用：

```java
BigDecimal
```

而不是：

```java
double
```

因为银行金额需要精确计算。

---

## 8.2 存款

核心逻辑：

```sql
UPDATE bank_account
SET balance = balance + #{amount}
WHERE user_id = #{userId}
```

---

## 8.3 取款

核心逻辑：

```sql
UPDATE bank_account
SET balance = balance - #{amount}
WHERE user_id = #{userId}
  AND balance >= #{amount}
```

这种写法可以让数据库直接参与余额条件判断。

---

## 8.4 转账

转账由两个余额操作组成：

```sql
-- 转出
UPDATE bank_account
SET balance = balance - #{amount}
WHERE user_id = #{userId}
  AND balance >= #{amount};
```

以及：

```sql
-- 转入
UPDATE bank_account
SET balance = balance + #{amount}
WHERE user_id = #{userId};
```

两个操作放在同一个事务中。

---

# 九、当前项目业务流程

```text
                 ┌─────────────┐
                 │    注册     │
                 └──────┬──────┘
                        ↓
                 ┌─────────────┐
                 │    登录     │
                 └──────┬──────┘
                        ↓
                 ┌─────────────┐
                 │    开户     │
                 └──────┬──────┘
                        ↓
                ┌───────┴────────┐
                ↓                ↓
          ┌──────────┐     ┌──────────┐
          │   存款   │     │   取款   │
          └────┬─────┘     └────┬─────┘
               │                │
               └───────┬────────┘
                       ↓
                 ┌─────────────┐
                 │    转账     │
                 └──────┬──────┘
                        ↓
                 ┌─────────────┐
                 │  查询余额   │
                 └─────────────┘
```

---

# 十、当前版本未实现功能

以下功能暂时**不属于 v1.0 已实现接口**：

- JWT Token
- Redis
- Spring Security
- MQ
- 交易流水
- 转账记录查询
- 银行卡挂失
- 账户冻结/解冻接口
- 修改密码
- 注销账户
- 分页查询
- 管理员后台
- 利息计算
- 定时任务

这些功能可以根据项目复杂度逐步增加，不建议一次性全部加入。

---

# 十一、后续版本规划

## v1.1 —— 交易流水

增加：

```http
GET /transaction/list?userId=1
```

记录：

```text
存款
取款
转账支出
转账收入
```

建议新增：

```text
bank_transaction
```

表。

建议字段：

```text
id
user_id
type
amount
balance
related_user_id
remark
create_time
```

---

## v1.2 —— 统一响应结果

增加：

```java
Result<T>
```

统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

这样所有接口的返回格式就能够统一。

---

## v1.3 —— 登录认证

如果后续需要进一步完善，可以增加：

```text
Spring Security
+
JWT
```

届时可以不再让前端直接传：

```text
userId
```

而是：

```text
登录
 ↓
获取 Token
 ↓
请求接口携带 Token
 ↓
服务器解析当前用户
```

但这些都属于后续扩展，不属于当前 v1.0。

---

# 十二、项目当前接口一览

最终当前版本核心业务可以概括为：

```text
用户
 ├── 注册
 └── 登录

账户
 ├── 开户
 ├── 查询账户
 ├── 存款
 ├── 取款
 └── 转账
```

这是当前天地银行项目的第一版核心业务闭环：

```text
注册
 ↓
登录
 ↓
开户
 ↓
存钱
 ↓
取钱
 ↓
转账
 ↓
查询余额
```

后续开发应优先在这个基础上增加**交易流水**，而不是立即堆叠 Redis、MQ、Spring Cloud 等技术栈。
