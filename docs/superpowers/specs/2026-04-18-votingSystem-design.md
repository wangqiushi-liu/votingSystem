# 投票系统 (VotingSystem) 设计方案

## 1. 项目概述

- **项目名称**：AI Star 投票系统
- **项目类型**：前后端分离的 Web 应用
- **核心功能**：为 4000+ 员工提供对 120+ 候选人投票的服务，从中选出 30+ 名 AI Star
- **技术栈**：Spring Boot (Java) + Vue 3 + MySQL + Docker

## 2. 业务需求

### 2.1 投票规则
- 实名投票（Welink SSO 认证）
- 每人投 5-10 票
- 投票后不可修改、不可删除

### 2.2 用户角色
- **普通员工**：查看候选人、投票、查看自己是否已投票
- **管理员**：投票活动管理、候选人管理、查看结果、投票审计、导出结果

### 2.3 候选人信息
- 姓名
- 部门
- 头像
- 个人简介

### 2.4 投票结果
- 仅管理员可见
- 包含实时票数和排名

## 3. 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                      Vue 3 前端                          │
│                   (Nginx 容器化)                         │
└─────────────────────┬───────────────────────────────────┘
                      │ REST API (JSON)
┌─────────────────────▼───────────────────────────────────┐
│                  Spring Boot 后端                        │
│              (Java 17+, Docker 容器化)                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ 认证模块  │  │ 投票模块  │  │ 管理模块  │              │
│  └──────────┘  └──────────┘  └──────────┘              │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                     MySQL 数据库                         │
└─────────────────────────────────────────────────────────┘
```

## 4. 数据模型

### 4.1 用户表 (User)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| welink_user_id | VARCHAR(64) | Welink 用户 ID |
| name | VARCHAR(64) | 姓名 |
| department | VARCHAR(128) | 部门 |
| avatar | VARCHAR(256) | 头像 URL |
| role | ENUM('ADMIN','VOTER') | 角色 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 4.2 候选人表 (Candidate)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(64) | 姓名 |
| department | VARCHAR(128) | 部门 |
| avatar | VARCHAR(256) | 头像 URL |
| bio | TEXT | 个人简介 |
| vote_count | INT | 票数 |
| rank | INT | 排名 |
| status | ENUM('ACTIVE','INACTIVE') | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 4.3 投票记录表 (VoteRecord)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 投票用户 ID |
| candidate_id | BIGINT | 候选人 ID |
| voted_at | DATETIME | 投票时间 |

### 4.4 投票活动表 (VoteSession)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(128) | 投票活动标题 |
| description | TEXT | 活动描述 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| min_votes | INT | 最少投票数（5） |
| max_votes | INT | 最多投票数（10） |
| status | ENUM('DRAFT','ACTIVE','ENDED') | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 5. API 设计

### 5.1 认证模块

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/auth/welink/login | Welink SSO 登录跳转 | 公开 |
| GET | /api/auth/welink/callback | Welink SSO 回调 | 公开 |
| POST | /api/auth/logout | 登出 | 需认证 |
| GET | /api/auth/me | 获取当前用户信息 | 需认证 |

### 5.2 投票模块

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/votes/sessions/active | 获取当前投票活动 | 需认证 |
| GET | /api/votes/candidates | 获取候选人列表 | 需认证 |
| POST | /api/votes/submit | 提交投票（5-10票） | 需认证 |
| GET | /api/votes/status | 查看自己是否已投票 | 需认证 |

### 5.3 管理模块

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| CRUD | /api/admin/sessions | 投票活动管理 | 管理员 |
| CRUD | /api/admin/candidates | 候选人管理 | 管理员 |
| GET | /api/admin/results | 查看投票结果 | 管理员 |
| GET | /api/admin/audit | 投票审计 | 管理员 |
| POST | /api/admin/export | 导出结果 Excel | 管理员 |

## 6. 投票流程

```
用户访问系统
    ↓
跳转 Welink SSO 登录
    ↓
登录成功后回调，获取用户信息
    ↓
查看当前投票活动（候选人列表）
    ↓
选择 5-10 个候选人
    ↓
确认提交
    ↓
验证：是否已投票、票数是否在 5-10 之间
    ↓
保存投票记录（每人一条记录，关联多个候选人）
    ↓
更新各候选人票数
    ↓
返回投票成功（不可修改）
```

## 7. Docker 部署

### 7.1 后端 Dockerfile
- 基于 openjdk:17-slim
- 暴露 8080 端口
- 使用多阶段构建打包 JAR

### 7.2 docker-compose.yml
- MySQL 服务
- Spring Boot 后端服务
- Nginx 前端服务

## 8. 关键约束

| 约束项 | 值 |
|--------|-----|
| 最大用户数 | 4000+ |
| 候选人数量 | 120+ |
| 当选人数 | 30+ |
| 最少投票数 | 5 |
| 最多投票数 | 10 |
| 投票是否匿名 | 否（实名） |
| 投票是否可修改 | 否 |
| 结果可见性 | 仅管理员 |
