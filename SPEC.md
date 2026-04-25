# AI Star 投票系统 - 项目设计文档

## 一、项目概述

| 项目 | 值 |
|------|-----|
| 项目名称 | AI Star Voting System |
| 架构模式 | Spring Boot + Vue 3，前后端分离 |
| 部署方式 | Docker Compose 全栈部署 |
| 代码仓库 | https://github.com/wangqiushi-liu/votingSystem |
| 当前分支 | wangqiushi_dev |

---

## 二、技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | JDK 版本 |
| Spring Boot | 3.2.0 | Web 框架 |
| Spring Security | 6.x | 认证/授权 |
| Spring Data JPA | 3.x | ORM |
| MySQL | 8.0 | 数据库 |
| Lombok | - | 注解生成代码 |
| Maven | 3.9 | 构建工具 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 渐进式前端框架 |
| Vite | 5.x | 构建工具 |
| Vue Router | 4.2 | 路由管理 |
| Pinia | 2.1 | 状态管理 |
| Axios | 1.6 | HTTP 客户端 |

---

## 三、目录结构

```
votingSystem/
├── docker-compose.yml          # Docker 全栈编排
├── DATABASE.md                 # 数据库设计文档
├── SPEC.md                     # 本文档
├── backend/
│   ├── pom.xml                 # Maven 配置
│   ├── Dockerfile              # 后端容器构建
│   └── src/
│       └── main/
│           ├── java/com/voting/
│           │   ├── VotingApplication.java          # 启动类
│           │   ├── entity/                          # 实体类
│           │   │   ├── User.java
│           │   │   ├── Candidate.java
│           │   │   ├── VoteSession.java
│           │   │   ├── VoteRecord.java
│           │   │   └── Role.java
│           │   ├── repository/                      # 数据访问层
│           │   │   ├── UserRepository.java
│           │   │   ├── CandidateRepository.java
│           │   │   ├── VoteSessionRepository.java
│           │   │   └── VoteRecordRepository.java
│           │   ├── service/                         # 业务逻辑层
│           │   │   ├── AuthService.java
│           │   │   ├── VoteService.java
│           │   │   └── AdminService.java
│           │   ├── controller/                     # 接口层
│           │   │   ├── AuthController.java
│           │   │   ├── VoteController.java
│           │   │   └── AdminController.java
│           │   ├── dto/                            # 数据传输对象
│           │   │   ├── WelinkUserInfo.java
│           │   │   ├── VoteSubmitRequest.java
│           │   │   ├── VoteStatusResponse.java
│           │   │   └── AdminResultResponse.java
│           │   ├── config/                        # 配置类
│           │   │   ├── SecurityConfig.java
│           │   │   ├── CorsConfig.java
│           │   │   └── HeaderAuthenticationFilter.java
│           │   └── exception/
│           │       └── GlobalExceptionHandler.java
│           └── resources/
│               └── schema.sql                      # 数据库初始化脚本
└── frontend/
    ├── package.json              # npm 依赖配置
    ├── vite.config.js            # Vite 配置
    ├── nginx.conf                # Nginx 反向代理配置
    ├── Dockerfile               # 前端容器构建
    ├── public/                   # 静态资源
    │   ├── background.png
    │   └── default-avatar.png
    └── src/
        ├── main.js              # Vue 入口
        ├── App.vue              # 根组件
        ├── api/
        │   └── index.js         # API 封装
        ├── router/
        │   └── index.js         # 路由配置
        ├── stores/
        │   └── user.js          # Pinia 用户状态
        ├── components/
        │   ├── CandidateCard.vue
        │   ├── VoteHeader.vue
        │   └── AdminSidebar.vue
        └── views/
            ├── LoginView.vue
            ├── VoteView.vue
            └── AdminView.vue
```

---

## 四、后端功能说明

### 4.1 认证模块 (AuthController)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| Welink 登录 | GET | /api/auth/welink/login | 跳转 Welink OAuth 授权页面 |
| Welink 回调 | GET | /api/auth/welink/callback | 接收授权码，换取用户信息，登录/注册用户 |
| 获取当前用户 | GET | /api/auth/me | 根据 X-User-Id header 返回用户信息 |

**认证流程**：
1. 用户点击"使用 Welink 登录"，跳转 Welink OAuth 页面
2. 用户授权后，Welink 回调到 `/api/auth/welink/callback?code=xxx`
3. 后端用 code 换取 access_token，再获取用户基本信息
4. 调用 `AuthService.loginOrRegister()` 查找或创建用户
5. 返回用户信息和 mock token

**Header 认证**：
- 登录后前端将 `user.id` 存入 localStorage
- 后续请求通过 `X-User-Id` 和 `X-User-Role` header 传递用户身份
- `HeaderAuthenticationFilter` 读取这两个 header 注入 Security Context

### 4.2 投票模块 (VoteController)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取当前活动 | GET | /api/votes/sessions/active | 返回状态为 ACTIVE 的最新投票活动 |
| 获取候选人列表 | GET | /api/votes/candidates | 返回所有状态为 ACTIVE 的候选人，按票数降序 |
| 提交投票 | POST | /api/votes/submit | 用户投票，支持批量投票 |
| 获取投票状态 | GET | /api/votes/status | 返回当前用户是否已投票及投票数量 |

**投票规则**：
- 每个用户只能投一次（不可修改）
- 投票数量必须在 `minVotes` 和 `maxVotes` 之间（默认 5-10）
- 投票必须在活动有效期内（`startTime` <= now <= `endTime`）
- 同一用户对同一候选人不能重复投票（UNIQUE 约束）

### 4.3 管理模块 (AdminController)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取所有投票活动 | GET | /api/admin/sessions | 返回所有投票会话 |
| 获取单个投票活动 | GET | /api/admin/sessions/{id} | 按 ID 获取投票活动 |
| 创建投票活动 | POST | /api/admin/sessions | 创建新的投票会话 |
| 更新投票活动 | PUT | /api/admin/sessions/{id} | 更新投票活动信息 |
| 删除投票活动 | DELETE | /api/admin/sessions/{id} | 删除投票活动 |
| 获取所有候选人 | GET | /api/admin/candidates | 返回所有候选人 |
| 获取单个候选人 | GET | /api/admin/candidates/{id} | 按 ID 获取候选人 |
| 添加候选人 | POST | /api/admin/candidates | 创建新候选人 |
| 更新候选人 | PUT | /api/admin/candidates/{id} | 更新候选人信息 |
| 删除候选人 | DELETE | /api/admin/candidates/{id} | 删除候选人 |
| 获取投票结果 | GET | /api/admin/results | 返回候选人排名和票数 |
| 获取审计数据 | GET | /api/admin/audit | 返回所有投票记录明细 |

---

## 五、前端功能说明

### 5.1 页面结构

```
/               → 重定向到 /login
/login          → 登录页（Welink 扫码登录）
/vote           → 投票页（需登录）
/admin          → 管理后台（需登录，且 role=ADMIN）
```

### 5.2 登录页 (LoginView.vue)

- 背景：渐变色紫色渐变 (`#667eea` → `#764ba2`)
- 显示"AI Star 投票系统"标题
- "使用 Welink 登录"按钮，点击后模拟登录流程
- 登录成功后根据 role 跳转到 `/vote` 或 `/admin`

### 5.3 投票页 (VoteView.vue)

**页面结构**：
1. **VoteHeader** - 顶部导航，显示用户名、头像、退出按钮
2. **Timeline** - 投票进度时间轴（推荐 → 专家审视 → 大众投票 → 专评会评选 → 结果公示）
3. **投票状态** - 已投票显示绿色徽章；未投票显示已选数量和规则（5-10票）
4. **候选人网格** - 4 列布局，`CandidateCard` 组件展示
5. **确认投票按钮** - 选择 5-10 位候选人后启用

**CandidateCard 组件**：
- 显示头像、姓名、部门、个人简介
- 选中状态：蓝色边框 + 浅蓝背景 + 右上角勾选标记
- 点击切换选中状态
- 超过最大票数时禁止继续选择，弹出提示

**交互逻辑**：
- `onMounted` 时调用 3 个 API：获取活动、获取候选人、获取投票状态
- 已投票用户不能再次投票，按钮不显示
- 提交后显示成功提示，页面刷新状态

### 5.4 管理后台 (AdminView.vue)

**侧边栏 (AdminSidebar)**：
- 4 个菜单项：投票活动、候选人管理、投票结果、投票审计
- 点击切换右侧内容区域

**投票活动管理**：
- 表格展示所有投票会话（ID、标题、时间、票数范围、状态）
- 支持创建、编辑、删除投票活动
- 状态标签：DRAFT(黄)、ACTIVE(绿)、ENDED(红)

**候选人管理**：
- 表格展示所有候选人（ID、姓名、部门、票数、状态）
- 支持添加、编辑、删除候选人

**投票结果**：
- 按票数降序显示候选人排名
- 显示姓名、部门、票数

**投票审计**：
- 显示所有投票记录（用户、候选人、投票时间）
- 用于追溯投票行为

### 5.5 路由守卫

- 未登录访问 `/vote` 或 `/admin` → 重定向到 `/login`
- 非管理员访问 `/admin` → 重定向到 `/vote`

---

## 六、API 通信格式

### 请求格式

前端使用 Axios，baseURL 为 `/api`（由 Nginx 转发到后端 8080 端口）。

**认证 Header**：
```javascript
{
  'X-User-Id': user.id,      // 用户 ID
  'X-User-Role': user.role   // ADMIN 或 VOTER
}
```

**请求体**：JSON 格式，camelCase 命名
```json
{
  "candidateIds": [1, 2, 3, 4, 5]
}
```

### 响应格式

成功：
```json
{
  "id": 1,
  "name": "张三",
  "role": "VOTER"
}
```

失败（由 GlobalExceptionHandler 处理）：
```json
{
  "error": "错误描述",
  "message": "详细错误信息"
}
```

---

## 七、配置文件

### 7.1 Docker Compose 环境变量

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| WELINK_APP_ID | Welink 应用 ID | your-app-id |
| WELINK_APP_SECRET | Welink 应用密钥 | your-app-secret |
| WELINK_REDIRECT_URI | OAuth 回调地址 | http://localhost:8080/api/auth/welink/callback |

### 7.2 Spring Boot 配置

| 配置项 | 值 |
|--------|-----|
| datasource.url | jdbc:mysql://mysql:3306/voting_system?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true |
| datasource.username | root |
| datasource.password | root |

### 7.3 Nginx 配置

- 监听 80 端口
- `/api/*` 转发到 `backend:8080`
- 其他请求返回 Vue 构建产物（`/usr/share/nginx/html`）

---

## 八、部署架构

```
                        Docker Network
┌─────────────────────────────────────────────────────┐
│                                                     │
│   ┌──────────┐      ┌────────────┐      ┌───────┐ │
│   │  User    │      │   Nginx    │      │ MySQL │ │
│   │ Browser  │─────▶│  (80)      │      │(3306) │ │
│   └──────────┘      │            │      └───────┘ │
│                      │ /api/* ──▶ │               │
│                      │ /        ──▶│ Vue Dist     │
│                      └────────────┘               │
│                            │                       │
│                            ▼                       │
│                      ┌────────────┐                │
│                      │  Backend   │                │
│                      │  (8080)    │                │
│                      └────────────┘                │
│                                                     │
└─────────────────────────────────────────────────────┘
```

启动命令：
```bash
docker compose up -d
```

---

## 九、安全说明

1. **CORS**：后端配置允许前端 (`localhost:80`, `localhost:8080`) 的跨域请求
2. **CSRF**：当前实现未启用，如需生产环境需添加 CSRF Token
3. **认证**：基于 Header 的简单认证，适用于内网环境
4. **Welink OAuth**：生产环境需配置真实的 Welink 应用凭证

---

## 十、待完善功能

1. **编辑功能**：AdminView 中的编辑 Session 和 Candidate 弹窗未实现
2. **投票会话详情**：VoteView 中 session-info 区域暂无内容
3. **实时结果**：投票结果页面不支持实时刷新
4. **Welink 真实集成**：目前为 mock 实现，需对接真实 Welink OAuth API