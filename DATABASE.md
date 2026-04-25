# 投票系统数据库设计文档

## 一、数据库信息

| 项目 | 值 |
|------|-----|
| 数据库类型 | MySQL 8.0 |
| 数据库容器 | voting-mysql |
| 默认端口 | 3306 |
| 数据库名称 | voting_system |
| 字符集 | utf8mb4 / utf8mb4_unicode_ci |

## 二、账号信息

| 用途 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| root 管理员 | root | root | 拥有所有权限，用于应用连接 |
| 应用连接 | root | root | Spring Boot 通过此账号连接数据库 |

> **安全注意**：生产环境请更换为强密码，并创建专用应用账号。

## 三、连接方式

### 本地连接（宿主机）

```bash
mysql -h 127.0.0.1 -P 3306 -u root -proot voting_system
```

### Docker 容器内连接

```bash
docker exec -it voting-mysql mysql -uroot -proot voting_system
```

### 通过 Docker Compose

```bash
docker compose exec -T mysql mysql -uroot -proot voting_system --default-character-set=utf8mb4
```

## 四、表结构

### 4.1 users 用户表

存储系统用户信息。

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    welink_user_id VARCHAR(64) NOT NULL UNIQUE COMMENT 'Welink用户唯一标识',
    name VARCHAR(64) NOT NULL COMMENT '用户姓名',
    department VARCHAR(128) COMMENT '部门',
    avatar VARCHAR(256) COMMENT '头像URL',
    role ENUM('ADMIN', 'VOTER') NOT NULL DEFAULT 'VOTER' COMMENT '角色：ADMIN=管理员 VOTER=投票者',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_welink_user_id (welink_user_id)
);
```

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| welink_user_id | VARCHAR(64) | NOT NULL, UNIQUE | Welink 用户唯一标识，用于 OAuth 登录 |
| name | VARCHAR(64) | NOT NULL | 用户姓名 |
| department | VARCHAR(128) | 可空 | 部门 |
| avatar | VARCHAR(256) | 可空 | 头像 URL |
| role | ENUM('ADMIN','VOTER') | NOT NULL, DEFAULT 'VOTER' | 角色 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

---

### 4.2 candidates 候选人表

存储投票候选人信息。

```sql
CREATE TABLE IF NOT EXISTS candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL COMMENT '候选人姓名',
    department VARCHAR(128) COMMENT '部门/组织',
    avatar VARCHAR(256) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    vote_count INT DEFAULT 0 COMMENT '得票数',
    `rank` INT DEFAULT 0 COMMENT '排名',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE=活跃 INACTIVE=非活跃',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);
```

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(64) | NOT NULL | 候选人姓名 |
| department | VARCHAR(128) | 可空 | 部门/组织 |
| avatar | VARCHAR(256) | 可空 | 头像 URL |
| bio | TEXT | 可空 | 个人简介/履历 |
| vote_count | INT | DEFAULT 0 | 当前得票数 |
| `rank` | INT | DEFAULT 0 | 当前排名 |
| status | ENUM('ACTIVE','INACTIVE') | NOT NULL, DEFAULT 'ACTIVE' | 状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

> 注意：`rank` 是 MySQL 关键字，使用时需要用反引号包裹。

---

### 4.3 vote_sessions 投票会话表

管理投票活动的生命周期。

```sql
CREATE TABLE IF NOT EXISTS vote_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(128) NOT NULL COMMENT '投票活动标题',
    description TEXT COMMENT '投票活动描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    min_votes INT NOT NULL DEFAULT 5 COMMENT '最少投票数',
    max_votes INT NOT NULL DEFAULT 10 COMMENT '最多投票数',
    status ENUM('DRAFT', 'ACTIVE', 'ENDED') NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT=草稿 ACTIVE=进行中 ENDED=已结束',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);
```

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| title | VARCHAR(128) | NOT NULL | 投票活动标题 |
| description | TEXT | 可空 | 活动描述 |
| start_time | DATETIME | NOT NULL | 投票开始时间 |
| end_time | DATETIME | NOT NULL | 投票结束时间 |
| min_votes | INT | NOT NULL, DEFAULT 5 | 最少投票数 |
| max_votes | INT | NOT NULL, DEFAULT 10 | 最多投票数 |
| status | ENUM('DRAFT','ACTIVE','ENDED') | NOT NULL, DEFAULT 'DRAFT' | 状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

---

### 4.4 vote_records 投票记录表

记录用户投票行为，关联用户和候选人。

```sql
CREATE TABLE IF NOT EXISTS vote_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    candidate_id BIGINT NOT NULL COMMENT '候选人ID',
    voted_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id),
    UNIQUE KEY uk_user_candidate (user_id, candidate_id),
    INDEX idx_user_id (user_id),
    INDEX idx_candidate_id (candidate_id)
);
```

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NOT NULL, FK(users.id) | 投票用户 |
| candidate_id | BIGINT | NOT NULL, FK(candidates.id) | 所投候选人 |
| voted_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 投票时间 |

**约束说明**：
- `UNIQUE KEY uk_user_candidate (user_id, candidate_id)` — 同一用户对同一候选人只能投票一次
- 外键确保数据完整性

---

## 五、ER 关系图

```
users ──────────< vote_records >────────── candidates
  │                                            │
  │                                            │
  └─── 1:N ────────────────────────────────────┘

vote_sessions: 一个投票活动包含多个候选人（业务层面，非外键约束）
```

- **users** 和 **candidates**：通过 `vote_records` 实现多对多关系
- 一个用户可以对多个候选人投票
- 一个候选人可以被多个用户投票

---

## 六、索引设计

| 表名 | 索引名 | 字段 | 类型 | 说明 |
|------|--------|------|------|------|
| users | idx_welink_user_id | welink_user_id | UNIQUE | 快速查找用户 |
| candidates | idx_status | status | INDEX | 按状态筛选候选人 |
| vote_sessions | idx_status | status | INDEX | 按状态筛选投票会话 |
| vote_records | uk_user_candidate | (user_id, candidate_id) | UNIQUE | 防重复投票 |
| vote_records | idx_user_id | user_id | INDEX | 查询用户投票记录 |
| vote_records | idx_candidate_id | candidate_id | INDEX | 统计候选人得票 |

---

## 七、环境变量说明

应用通过以下环境变量连接数据库（定义在 docker-compose.yml）：

```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/voting_system?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: root
```