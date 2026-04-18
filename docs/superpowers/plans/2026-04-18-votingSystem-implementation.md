# AI Star 投票系统实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个前后端分离的投票系统，支持 4000+ 用户对 120+ 候选人投票，选出 30+ 名 AI Star

**Architecture:** 单体 Spring Boot 后端 + Vue 3 前端，MySQL 数据库，Welink SSO 认证，Docker 容器化部署

**Tech Stack:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA, MySQL 8, Vue 3, Vite, Pinia, Axios, Nginx, Docker

---

## 文件结构

```
votingSystem/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # Maven 配置
│   ├── src/main/java/com/voting/
│   │   ├── VotingApplication.java    # 启动类
│   │   ├── config/
│   │   │   ├── SecurityConfig.java   # Spring Security 配置
│   │   │   └── CorsConfig.java       # CORS 配置
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Candidate.java
│   │   │   ├── VoteRecord.java
│   │   │   └── VoteSession.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── CandidateRepository.java
│   │   │   ├── VoteRecordRepository.java
│   │   │   └── VoteSessionRepository.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── VoteService.java
│   │   │   └── AdminService.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── VoteController.java
│   │   │   └── AdminController.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   ├── VoteSubmitRequest.java
│   │   │   └── VoteResultResponse.java
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/
│   │   ├── application.yml           # 应用配置
│   │   └── schema.sql                # 数据库 DDL
│   └── Dockerfile                    # 后端 Docker 配置
├── frontend/                         # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   ├── src/
│   │   ├── main.js
│   │   ├── App.vue
│   │   ├── api/
│   │   │   └── index.js              # API 客户端
│   │   ├── components/
│   │   │   ├── CandidateCard.vue
│   │   │   ├── VoteHeader.vue
│   │   │   └── AdminSidebar.vue
│   │   ├── views/
│   │   │   ├── LoginView.vue
│   │   │   ├── VoteView.vue
│   │   │   ├── ResultView.vue
│   │   │   └── AdminView.vue
│   │   ├── stores/
│   │   │   └── user.js               # Pinia store
│   │   └── router/
│   │       └── index.js
│   └── Dockerfile                   # 前端 Nginx 配置
├── docker-compose.yml                # 容器编排
└── docs/superpowers/
    ├── specs/2026-04-18-votingSystem-design.md
    └── plans/2026-04-18-votingSystem-implementation.md
```

---

## Phase 1: 项目初始化

### Task 1: 创建后端 Spring Boot 项目结构

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/voting/VotingApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/schema.sql`

- [ ] **Step 1: 创建 Maven pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.voting</groupId>
    <artifactId>voting-system</artifactId>
    <version>1.0.0</version>
    <name>voting-system</name>
    <description>AI Star Voting System</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

```java
package com.voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VotingApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotingApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/voting_system?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

welink:
  app-id: ${WELINK_APP_ID:your-app-id}
  app-secret: ${WELINK_APP_SECRET:your-app-secret}
  redirect-uri: ${WELINK_REDIRECT_URI:http://localhost:8080/api/auth/welink/callback}
```

- [ ] **Step 4: 创建数据库 schema.sql**

```sql
CREATE DATABASE IF NOT EXISTS voting_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE voting_system;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    welink_user_id VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    department VARCHAR(128),
    avatar VARCHAR(256),
    role ENUM('ADMIN', 'VOTER') NOT NULL DEFAULT 'VOTER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_welink_user_id (welink_user_id)
);

CREATE TABLE IF NOT EXISTS candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    department VARCHAR(128),
    avatar VARCHAR(256),
    bio TEXT,
    vote_count INT DEFAULT 0,
    rank INT DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS vote_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    min_votes INT NOT NULL DEFAULT 5,
    max_votes INT NOT NULL DEFAULT 10,
    status ENUM('DRAFT', 'ACTIVE', 'ENDED') NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS vote_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    voted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id),
    UNIQUE KEY uk_user_candidate (user_id, candidate_id),
    INDEX idx_user_id (user_id),
    INDEX idx_candidate_id (candidate_id)
);
```

- [ ] **Step 5: Commit**

```bash
cd /Users/innerpeace/codeRepo/votingSystem
git add backend/pom.xml backend/src/main/java/com/voting/VotingApplication.java backend/src/main/resources/application.yml backend/src/main/resources/schema.sql
git commit -m "feat: initialize Spring Boot project structure"
```

---

### Task 2: 创建实体类

**Files:**
- Create: `backend/src/main/java/com/voting/entity/User.java`
- Create: `backend/src/main/java/com/voting/entity/Candidate.java`
- Create: `backend/src/main/java/com/voting/entity/VoteRecord.java`
- Create: `backend/src/main/java/com/voting/entity/VoteSession.java`
- Create: `backend/src/main/java/com/voting/entity/Role.java`

- [ ] **Step 1: 创建 Role 枚举**

```java
package com.voting.entity;

public enum Role {
    ADMIN,
    VOTER
}
```

- [ ] **Step 2: 创建 User 实体**

```java
package com.voting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "welink_user_id", nullable = false, unique = true, length = 64)
    private String welinkUserId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 128)
    private String department;

    @Column(length = 256)
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.VOTER;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: 创建 Candidate 实体**

```java
package com.voting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 128)
    private String department;

    @Column(length = 256)
    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "vote_count")
    private Integer voteCount = 0;

    private Integer rank = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status = CandidateStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CandidateStatus {
        ACTIVE,
        INACTIVE
    }
}
```

- [ ] **Step 4: 创建 VoteSession 实体**

```java
package com.voting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_sessions")
@Data
public class VoteSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "min_votes", nullable = false)
    private Integer minVotes = 5;

    @Column(name = "max_votes", nullable = false)
    private Integer maxVotes = 10;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.DRAFT;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SessionStatus {
        DRAFT,
        ACTIVE,
        ENDED
    }
}
```

- [ ] **Step 5: 创建 VoteRecord 实体**

```java
package com.voting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_records")
@Data
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/voting/entity/
git commit -m "feat: create entity classes (User, Candidate, VoteSession, VoteRecord)"
```

---

### Task 3: 创建 Repository 接口

**Files:**
- Create: `backend/src/main/java/com/voting/repository/UserRepository.java`
- Create: `backend/src/main/java/com/voting/repository/CandidateRepository.java`
- Create: `backend/src/main/java/com/voting/repository/VoteRecordRepository.java`
- Create: `backend/src/main/java/com/voting/repository/VoteSessionRepository.java`

- [ ] **Step 1: 创建 UserRepository**

```java
package com.voting.repository;

import com.voting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByWelinkUserId(String welinkUserId);
    boolean existsByWelinkUserId(String welinkUserId);
}
```

- [ ] **Step 2: 创建 CandidateRepository**

```java
package com.voting.repository;

import com.voting.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByStatusOrderByVoteCountDesc(Candidate.CandidateStatus status);

    @Modifying
    @Query("UPDATE Candidate c SET c.voteCount = c.voteCount + 1 WHERE c.id = :id")
    void incrementVoteCount(@Param("id") Long id);
}
```

- [ ] **Step 3: 创建 VoteRecordRepository**

```java
package com.voting.repository;

import com.voting.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    boolean existsByUserId(Long userId);
    List<VoteRecord> findByUserId(Long userId);
    long countByCandidateId(Long candidateId);
}
```

- [ ] **Step 4: 创建 VoteSessionRepository**

```java
package com.voting.repository;

import com.voting.entity.VoteSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteSessionRepository extends JpaRepository<VoteSession, Long> {
    Optional<VoteSession> findFirstByStatusOrderByCreatedAtDesc(VoteSession.SessionStatus status);
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/voting/repository/
git commit -m "feat: create repository interfaces"
```

---

### Task 4: 创建 DTO 类

**Files:**
- Create: `backend/src/main/java/com/voting/dto/WelinkUserInfo.java`
- Create: `backend/src/main/java/com/voting/dto/VoteSubmitRequest.java`
- Create: `backend/src/main/java/com/voting/dto/VoteStatusResponse.java`
- Create: `backend/src/main/java/com/voting/dto/AdminResultResponse.java`

- [ ] **Step 1: 创建 WelinkUserInfo DTO**

```java
package com.voting.dto;

import lombok.Data;

@Data
public class WelinkUserInfo {
    private String userId;
    private String name;
    private String department;
    private String avatar;
}
```

- [ ] **Step 2: 创建 VoteSubmitRequest DTO**

```java
package com.voting.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class VoteSubmitRequest {
    @NotEmpty(message = "候选人ID列表不能为空")
    @Size(min = 5, max = 10, message = "投票数量必须在5-10之间")
    private List<Long> candidateIds;
}
```

- [ ] **Step 3: 创建 VoteStatusResponse DTO**

```java
package com.voting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteStatusResponse {
    private boolean voted;
    private Integer voteCount;
}
```

- [ ] **Step 4: 创建 AdminResultResponse DTO**

```java
package com.voting.dto;

import lombok.Data;

@Data
public class AdminResultResponse {
    private Long candidateId;
    private String name;
    private String department;
    private String avatar;
    private Integer voteCount;
    private Integer rank;
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/voting/dto/
git commit -m "feat: create DTO classes"
```

---

### Task 5: 创建 Service 层

**Files:**
- Create: `backend/src/main/java/com/voting/service/AuthService.java`
- Create: `backend/src/main/java/com/voting/service/VoteService.java`
- Create: `backend/src/main/java/com/voting/service/AdminService.java`

- [ ] **Step 1: 创建 AuthService**

```java
package com.voting.service;

import com.voting.dto.WelinkUserInfo;
import com.voting.entity.User;
import com.voting.entity.Role;
import com.voting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public User loginOrRegister(WelinkUserInfo welinkUserInfo) {
        return userRepository.findByWelinkUserId(welinkUserInfo.getUserId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setWelinkUserId(welinkUserInfo.getUserId());
                    newUser.setName(welinkUserInfo.getName());
                    newUser.setDepartment(welinkUserInfo.getDepartment());
                    newUser.setAvatar(welinkUserInfo.getAvatar());
                    newUser.setRole(Role.VOTER);
                    return userRepository.save(newUser);
                });
    }

    public User getUserByWelinkUserId(String welinkUserId) {
        return userRepository.findByWelinkUserId(welinkUserId).orElse(null);
    }
}
```

- [ ] **Step 2: 创建 VoteService**

```java
package com.voting.service;

import com.voting.dto.VoteStatusResponse;
import com.voting.dto.VoteSubmitRequest;
import com.voting.entity.*;
import com.voting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteSessionRepository voteSessionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public VoteSession getActiveSession() {
        return voteSessionRepository.findFirstByStatusOrderByCreatedAtDesc(SessionStatus.ACTIVE)
                .orElse(null);
    }

    public List<Candidate> getCandidates() {
        return candidateRepository.findByStatusOrderByVoteCountDesc(Candidate.CandidateStatus.ACTIVE);
    }

    public VoteStatusResponse getVoteStatus(Long userId) {
        boolean voted = voteRecordRepository.existsByUserId(userId);
        int voteCount = voted ? voteRecordRepository.findByUserId(userId).size() : 0;
        return new VoteStatusResponse(voted, voteCount);
    }

    @Transactional
    public void submitVote(Long userId, VoteSubmitRequest request) {
        // 检查是否已投票
        if (voteRecordRepository.existsByUserId(userId)) {
            throw new RuntimeException("您已经投过票了，不能修改");
        }

        // 检查投票活动是否有效
        VoteSession session = getActiveSession();
        if (session == null) {
            throw new RuntimeException("当前没有活跃的投票活动");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            throw new RuntimeException("投票不在有效时间内");
        }

        // 检查票数范围
        int voteCount = request.getCandidateIds().size();
        if (voteCount < session.getMinVotes() || voteCount > session.getMaxVotes()) {
            throw new RuntimeException("投票数量必须在" + session.getMinVotes() + "-" + session.getMaxVotes() + "之间");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        // 保存投票记录
        for (Long candidateId : request.getCandidateIds()) {
            VoteRecord record = new VoteRecord();
            record.setUser(user);
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("候选人不存在: " + candidateId));
            record.setCandidate(candidate);
            voteRecordRepository.save(record);

            // 增加候选人票数
            candidateRepository.incrementVoteCount(candidateId);
        }
    }
}
```

- [ ] **Step 3: 创建 AdminService**

```java
package com.voting.service;

import com.voting.dto.AdminResultResponse;
import com.voting.entity.*;
import com.voting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final VoteSessionRepository voteSessionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;

    // ==================== VoteSession CRUD ====================

    public List<VoteSession> getAllSessions() {
        return voteSessionRepository.findAll();
    }

    public VoteSession getSessionById(Long id) {
        return voteSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("投票活动不存在"));
    }

    @Transactional
    public VoteSession createSession(VoteSession session) {
        return voteSessionRepository.save(session);
    }

    @Transactional
    public VoteSession updateSession(Long id, VoteSession session) {
        VoteSession existing = getSessionById(id);
        existing.setTitle(session.getTitle());
        existing.setDescription(session.getDescription());
        existing.setStartTime(session.getStartTime());
        existing.setEndTime(session.getEndTime());
        existing.setMinVotes(session.getMinVotes());
        existing.setMaxVotes(session.getMaxVotes());
        existing.setStatus(session.getStatus());
        return voteSessionRepository.save(existing);
    }

    @Transactional
    public void deleteSession(Long id) {
        voteSessionRepository.deleteById(id);
    }

    // ==================== Candidate CRUD ====================

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
    }

    @Transactional
    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate updateCandidate(Long id, Candidate candidate) {
        Candidate existing = getCandidateById(id);
        existing.setName(candidate.getName());
        existing.setDepartment(candidate.getDepartment());
        existing.setAvatar(candidate.getAvatar());
        existing.setBio(candidate.getBio());
        existing.setStatus(candidate.getStatus());
        return candidateRepository.save(existing);
    }

    @Transactional
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    // ==================== Results & Audit ====================

    public List<AdminResultResponse> getResults() {
        List<Candidate> candidates = candidateRepository.findByStatusOrderByVoteCountDesc(Candidate.CandidateStatus.ACTIVE);
        return candidates.stream()
                .map(c -> {
                    AdminResultResponse r = new AdminResultResponse();
                    r.setCandidateId(c.getId());
                    r.setName(c.getName());
                    r.setDepartment(c.getDepartment());
                    r.setAvatar(c.getAvatar());
                    r.setVoteCount(c.getVoteCount());
                    r.setRank(c.getRank());
                    return r;
                })
                .collect(Collectors.toList());
    }

    public List<VoteRecord> getAuditData() {
        return voteRecordRepository.findAll();
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/voting/service/
git commit -m "feat: create service layer (AuthService, VoteService, AdminService)"
```

---

### Task 6: 创建 Controller 层

**Files:**
- Create: `backend/src/main/java/com/voting/controller/AuthController.java`
- Create: `backend/src/main/java/com/voting/controller/VoteController.java`
- Create: `backend/src/main/java/com/voting/controller/AdminController.java`

- [ ] **Step 1: 创建 AuthController**

```java
package com.voting.controller;

import com.voting.entity.User;
import com.voting.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/welink/login")
    public ResponseEntity<Void> welinkLogin() {
        // 实际实现中会重定向到 Welink OAuth
        String welinkAuthUrl = "https://your-welink-server/oauth2/authorize?" +
                "app_id=your-app-id&redirect_uri=http://localhost:8080/api/auth/welink/callback&response_type=code";
        return ResponseEntity.status(302).header("Location", welinkAuthUrl).build();
    }

    @GetMapping("/welink/callback")
    public ResponseEntity<Map<String, Object>> welinkCallback(@RequestParam String code) {
        // 实际实现中会根据 code 获取 access_token 和用户信息
        // 这里简化处理，假设 code 包含用户信息
        User user = authService.loginOrRegister(parseWelinkCode(code));
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", "mock-jwt-token-" + user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        User user = authService.getUserByWelinkUserId(String.valueOf(userId));
        return ResponseEntity.ok(user);
    }

    private com.voting.dto.WelinkUserInfo parseWelinkCode(String code) {
        // 实际实现中需要调用 Welink API 获取用户信息
        // 这里返回模拟数据
        com.voting.dto.WelinkUserInfo info = new com.voting.dto.WelinkUserInfo();
        info.setUserId(code);
        info.setName("User " + code);
        info.setDepartment("Department");
        info.setAvatar("https://via.placeholder.com/100");
        return info;
    }
}
```

- [ ] **Step 2: 创建 VoteController**

```java
package com.voting.controller;

import com.voting.dto.VoteStatusResponse;
import com.voting.dto.VoteSubmitRequest;
import com.voting.entity.Candidate;
import com.voting.entity.VoteSession;
import com.voting.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/sessions/active")
    public ResponseEntity<VoteSession> getActiveSession() {
        VoteSession session = voteService.getActiveSession();
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getCandidates() {
        return ResponseEntity.ok(voteService.getCandidates());
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitVote(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody VoteSubmitRequest request) {
        voteService.submitVote(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<VoteStatusResponse> getVoteStatus(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(voteService.getVoteStatus(userId));
    }
}
```

- [ ] **Step 3: 创建 AdminController**

```java
package com.voting.controller;

import com.voting.dto.AdminResultResponse;
import com.voting.entity.Candidate;
import com.voting.entity.VoteRecord;
import com.voting.entity.VoteSession;
import com.voting.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== VoteSession CRUD ====================

    @GetMapping("/sessions")
    public ResponseEntity<List<VoteSession>> getAllSessions() {
        return ResponseEntity.ok(adminService.getAllSessions());
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<VoteSession> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getSessionById(id));
    }

    @PostMapping("/sessions")
    public ResponseEntity<VoteSession> createSession(@RequestBody VoteSession session) {
        return ResponseEntity.ok(adminService.createSession(session));
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<VoteSession> updateSession(@PathVariable Long id, @RequestBody VoteSession session) {
        return ResponseEntity.ok(adminService.updateSession(id, session));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        adminService.deleteSession(id);
        return ResponseEntity.ok().build();
    }

    // ==================== Candidate CRUD ====================

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(adminService.getAllCandidates());
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCandidateById(id));
    }

    @PostMapping("/candidates")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidate) {
        return ResponseEntity.ok(adminService.createCandidate(candidate));
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidate) {
        return ResponseEntity.ok(adminService.updateCandidate(id, candidate));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        adminService.deleteCandidate(id);
        return ResponseEntity.ok().build();
    }

    // ==================== Results & Audit ====================

    @GetMapping("/results")
    public ResponseEntity<List<AdminResultResponse>> getResults() {
        return ResponseEntity.ok(adminService.getResults());
    }

    @GetMapping("/audit")
    public ResponseEntity<List<VoteRecord>> getAudit() {
        return ResponseEntity.ok(adminService.getAuditData());
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/voting/controller/
git commit -m "feat: create controller layer (AuthController, VoteController, AdminController)"
```

---

### Task 7: 创建配置类

**Files:**
- Create: `backend/src/main/java/com/voting/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/voting/config/CorsConfig.java`
- Create: `backend/src/main/java/com/voting/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: 创建 SecurityConfig**

```java
package com.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

- [ ] **Step 2: 创建 CorsConfig**

```java
package com.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 3: 创建 GlobalExceptionHandler**

```java
package com.voting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/voting/config/ backend/src/main/java/com/voting/exception/
git commit -m "feat: create config and exception handler classes"
```

---

### Task 8: 创建后端 Dockerfile

**Files:**
- Create: `backend/Dockerfile`

- [ ] **Step 1: 创建后端 Dockerfile**

```dockerfile
# Build stage
FROM maven:3.9-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 2: Commit**

```bash
git add backend/Dockerfile
git commit -m "feat: add backend Dockerfile"
```

---

### Task 9: 创建前端 Vue 3 项目

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "voting-system-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "axios": "^1.6.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AI Star 投票系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

- [ ] **Step 5: 创建 App.vue**

```vue
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
}
</style>
```

- [ ] **Step 6: Commit**

```bash
git add frontend/package.json frontend/vite.config.js frontend/index.html frontend/src/main.js frontend/src/App.vue
git commit -m "feat: initialize Vue 3 frontend project"
```

---

### Task 10: 创建前端路由和 API

**Files:**
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/api/index.js`

- [ ] **Step 1: 创建 router/index.js**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import VoteView from '../views/VoteView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/vote',
    name: 'Vote',
    component: VoteView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminView,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.meta.requiresAuth && !userStore.id) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
    next('/vote')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 2: 创建 api/index.js**

```javascript
import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user.id) {
    config.headers['X-User-Id'] = user.id
    config.headers['X-User-Role'] = user.role
  }
  return config
})

// 响应拦截器
apiClient.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authApi = {
  welinkLogin: () => apiClient.get('/auth/welink/login'),
  welinkCallback: (code) => apiClient.get('/auth/welink/callback', { params: { code } }),
  getMe: () => apiClient.get('/auth/me')
}

// Vote API
export const voteApi = {
  getActiveSession: () => apiClient.get('/votes/sessions/active'),
  getCandidates: () => apiClient.get('/votes/candidates'),
  submitVote: (candidateIds) => apiClient.post('/votes/submit', { candidateIds }),
  getVoteStatus: () => apiClient.get('/votes/status')
}

// Admin API
export const adminApi = {
  // Sessions
  getSessions: () => apiClient.get('/admin/sessions'),
  getSession: (id) => apiClient.get(`/admin/sessions/${id}`),
  createSession: (data) => apiClient.post('/admin/sessions', data),
  updateSession: (id, data) => apiClient.put(`/admin/sessions/${id}`, data),
  deleteSession: (id) => apiClient.delete(`/admin/sessions/${id}`),
  // Candidates
  getCandidates: () => apiClient.get('/admin/candidates'),
  getCandidate: (id) => apiClient.get(`/admin/candidates/${id}`),
  createCandidate: (data) => apiClient.post('/admin/candidates', data),
  updateCandidate: (id, data) => apiClient.put(`/admin/candidates/${id}`, data),
  deleteCandidate: (id) => apiClient.delete(`/admin/candidates/${id}`),
  // Results & Audit
  getResults: () => apiClient.get('/admin/results'),
  getAudit: () => apiClient.get('/admin/audit')
}

export default apiClient
```

- [ ] **Step 3: Commit**

```bash
git add frontend/src/router/index.js frontend/src/api/index.js
git commit -m "feat: create frontend router and API client"
```

---

### Task 11: 创建 Pinia Store

**Files:**
- Create: `frontend/src/stores/user.js`

- [ ] **Step 1: 创建 stores/user.js**

```javascript
import { defineStore } from 'pinia'
import { authApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    isLoggedIn: !!JSON.parse(localStorage.getItem('user') || '{}').id,
    isAdmin: JSON.parse(localStorage.getItem('user') || '{}').role === 'ADMIN'
  }),

  actions: {
    async login(code) {
      try {
        const response = await authApi.welinkCallback(code)
        this.user = response.user
        this.isLoggedIn = true
        this.isAdmin = response.user.role === 'ADMIN'
        localStorage.setItem('user', JSON.stringify(response.user))
        localStorage.setItem('token', response.token)
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },

    logout() {
      this.user = {}
      this.isLoggedIn = false
      this.isAdmin = false
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  }
})
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/stores/user.js
git commit -m "feat: create Pinia user store"
```

---

### Task 12: 创建前端组件

**Files:**
- Create: `frontend/src/components/CandidateCard.vue`
- Create: `frontend/src/components/VoteHeader.vue`
- Create: `frontend/src/components/AdminSidebar.vue`

- [ ] **Step 1: 创建 CandidateCard.vue**

```vue
<template>
  <div class="candidate-card" :class="{ selected: isSelected }" @click="toggleSelect">
    <img :src="candidate.avatar || '/default-avatar.png'" :alt="candidate.name" class="avatar" />
    <div class="info">
      <h3>{{ candidate.name }}</h3>
      <p class="department">{{ candidate.department }}</p>
      <p class="bio">{{ candidate.bio }}</p>
    </div>
    <div class="check-mark" v-if="isSelected">✓</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  candidate: {
    type: Object,
    required: true
  },
  isSelected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const toggleSelect = () => {
  emit('toggle', props.candidate.id)
}
</script>

<style scoped>
.candidate-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.candidate-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.candidate-card.selected {
  border: 2px solid #1890ff;
  background: #e6f7ff;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 16px;
  object-fit: cover;
}

.info {
  flex: 1;
}

.info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
}

.department {
  color: #666;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.bio {
  font-size: 13px;
  color: #888;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.check-mark {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background: #1890ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
</style>
```

- [ ] **Step 2: 创建 VoteHeader.vue**

```vue
<template>
  <header class="vote-header">
    <div class="header-content">
      <h1>AI Star 投票</h1>
      <div class="user-info">
        <img :src="user.avatar || '/default-avatar.png'" :alt="user.name" class="user-avatar" />
        <span>{{ user.name }}</span>
        <button @click="handleLogout" class="logout-btn">退出</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.vote-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 16px 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.logout-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
```

- [ ] **Step 3: 创建 AdminSidebar.vue**

```vue
<template>
  <aside class="admin-sidebar">
    <h2>管理后台</h2>
    <nav>
      <a
        v-for="item in menuItems"
        :key="item.path"
        :href="'#' + item.path"
        :class="{ active: currentPath === item.path }"
        @click.prevent="navigate(item.path)"
      >
        {{ item.name }}
      </a>
    </nav>
  </aside>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  currentPath: {
    type: String,
    default: 'sessions'
  }
})

const emit = defineEmits(['navigate'])

const menuItems = [
  { name: '投票活动', path: 'sessions' },
  { name: '候选人管理', path: 'candidates' },
  { name: '投票结果', path: 'results' },
  { name: '投票审计', path: 'audit' }
]

const navigate = (path) => {
  emit('navigate', path)
}
</script>

<style scoped>
.admin-sidebar {
  width: 200px;
  background: white;
  min-height: 100vh;
  padding: 24px 0;
}

h2 {
  padding: 0 24px;
  margin: 0 0 24px 0;
  font-size: 18px;
}

nav a {
  display: block;
  padding: 12px 24px;
  color: #333;
  text-decoration: none;
  transition: all 0.2s;
}

nav a:hover {
  background: #f5f5f5;
}

nav a.active {
  background: #e6f7ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}
</style>
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/components/CandidateCard.vue frontend/src/components/VoteHeader.vue frontend/src/components/AdminSidebar.vue
git commit -m "feat: create frontend components (CandidateCard, VoteHeader, AdminSidebar)"
```

---

### Task 13: 创建前端页面视图

**Files:**
- Create: `frontend/src/views/LoginView.vue`
- Create: `frontend/src/views/VoteView.vue`
- Create: `frontend/src/views/AdminView.vue`

- [ ] **Step 1: 创建 LoginView.vue**

```vue
<template>
  <div class="login-page">
    <div class="login-card">
      <h1>AI Star 投票系统</h1>
      <p>请使用 Welink 扫码登录</p>
      <button @click="handleLogin" class="login-btn">使用 Welink 登录</button>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleLogin = async () => {
  // 模拟登录 - 实际应该跳转 Welink OAuth
  const mockCode = 'mock-user-' + Date.now()
  const success = await userStore.login(mockCode)
  if (success) {
    const user = userStore.user
    if (user.role === 'ADMIN') {
      router.push('/admin')
    } else {
      router.push('/vote')
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  padding: 48px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

h1 {
  margin: 0 0 16px 0;
  font-size: 28px;
  color: #333;
}

p {
  margin: 0 0 32px 0;
  color: #666;
}

.login-btn {
  padding: 14px 32px;
  font-size: 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #40a9ff;
}
</style>
```

- [ ] **Step 2: 创建 VoteView.vue**

```vue
<template>
  <div class="vote-page">
    <VoteHeader :user="user" />

    <div class="vote-content" v-if="session">
      <div class="session-info">
        <h2>{{ session.title }}</h2>
        <p>{{ session.description }}</p>
        <p class="vote-rule">请选择 {{ session.minVotes }}-{{ session.maxVotes }} 位候选人</p>
      </div>

      <div class="vote-status" v-if="hasVoted">
        <span class="voted-badge">您已完成投票</span>
      </div>

      <div class="selected-info" v-else>
        <span>已选择: {{ selectedIds.length }} / {{ session.minVotes }}-{{ session.maxVotes }}</span>
      </div>

      <div class="candidates-grid">
        <CandidateCard
          v-for="candidate in candidates"
          :key="candidate.id"
          :candidate="candidate"
          :is-selected="selectedIds.includes(candidate.id)"
          @toggle="toggleCandidate"
        />
      </div>

      <div class="vote-action" v-if="!hasVoted">
        <button
          @click="submitVote"
          :disabled="selectedIds.length < session.minVotes || selectedIds.length > session.maxVotes"
          class="submit-btn"
        >
          确认投票
        </button>
      </div>
    </div>

    <div class="no-session" v-else>
      <p>当前没有活跃的投票活动</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { voteApi } from '../api'
import VoteHeader from '../components/VoteHeader.vue'
import CandidateCard from '../components/CandidateCard.vue'

const userStore = useUserStore()
const user = userStore.user

const session = ref(null)
const candidates = ref([])
const selectedIds = ref([])
const hasVoted = ref(false)

onMounted(async () => {
  // 获取当前投票活动
  session.value = await voteApi.getActiveSession().catch(() => null)
  // 获取候选人列表
  candidates.value = await voteApi.getCandidates().catch(() => [])
  // 检查投票状态
  const status = await voteApi.getVoteStatus().catch(() => ({ voted: false }))
  hasVoted.value = status.voted
})

const toggleCandidate = (id) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const submitVote = async () => {
  try {
    await voteApi.submitVote(selectedIds.value)
    hasVoted.value = true
    alert('投票成功！')
  } catch (error) {
    alert(error.message || '投票失败')
  }
}
</script>

<style scoped>
.vote-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.vote-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.session-info {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.session-info h2 {
  margin: 0 0 8px 0;
}

.session-info p {
  margin: 0;
  color: #666;
}

.vote-rule {
  margin-top: 8px !important;
  color: #1890ff !important;
  font-weight: 500;
}

.selected-info {
  text-align: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  margin-bottom: 24px;
  font-weight: 500;
}

.voted-badge {
  display: inline-block;
  padding: 8px 24px;
  background: #52c41a;
  color: white;
  border-radius: 20px;
  font-weight: 500;
}

.candidates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.vote-action {
  text-align: center;
}

.submit-btn {
  padding: 14px 48px;
  font-size: 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.no-session {
  text-align: center;
  padding: 48px;
  background: white;
  border-radius: 8px;
  margin: 24px auto;
  max-width: 600px;
}
</style>
```

- [ ] **Step 3: 创建 AdminView.vue**

```vue
<template>
  <div class="admin-page">
    <div class="admin-layout">
      <AdminSidebar :current-path="currentSection" @navigate="handleNavigate" />

      <main class="admin-content">
        <!-- 投票活动管理 -->
        <div v-if="currentSection === 'sessions'" class="section">
          <h2>投票活动管理</h2>
          <button @click="showSessionModal = true" class="add-btn">创建活动</button>

          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>票数范围</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in sessions" :key="s.id">
                <td>{{ s.id }}</td>
                <td>{{ s.title }}</td>
                <td>{{ formatDate(s.startTime) }}</td>
                <td>{{ formatDate(s.endTime) }}</td>
                <td>{{ s.minVotes }}-{{ s.maxVotes }}</td>
                <td><span :class="'status-' + s.status">{{ s.status }}</span></td>
                <td>
                  <button @click="editSession(s)">编辑</button>
                  <button @click="deleteSession(s.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 候选人管理 -->
        <div v-if="currentSection === 'candidates'" class="section">
          <h2>候选人管理</h2>
          <button @click="showCandidateModal = true" class="add-btn">添加候选人</button>

          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>姓名</th>
                <th>部门</th>
                <th>票数</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in candidates" :key="c.id">
                <td>{{ c.id }}</td>
                <td>{{ c.name }}</td>
                <td>{{ c.department }}</td>
                <td>{{ c.voteCount }}</td>
                <td><span :class="'status-' + c.status">{{ c.status }}</span></td>
                <td>
                  <button @click="editCandidate(c)">编辑</button>
                  <button @click="deleteCandidate(c.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 投票结果 -->
        <div v-if="currentSection === 'results'" class="section">
          <h2>投票结果</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>姓名</th>
                <th>部门</th>
                <th>票数</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in results" :key="r.candidateId">
                <td>{{ r.rank }}</td>
                <td>{{ r.name }}</td>
                <td>{{ r.department }}</td>
                <td>{{ r.voteCount }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 投票审计 -->
        <div v-if="currentSection === 'audit'" class="section">
          <h2>投票审计</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>投票用户</th>
                <th>候选人</th>
                <th>投票时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in auditData" :key="a.id">
                <td>{{ a.userName }}</td>
                <td>{{ a.candidateName }}</td>
                <td>{{ formatDate(a.votedAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../api'
import AdminSidebar from '../components/AdminSidebar.vue'

const currentSection = ref('sessions')
const sessions = ref([])
const candidates = ref([])
const results = ref([])
const auditData = ref([])

const showSessionModal = ref(false)
const showCandidateModal = ref(false)

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  sessions.value = await adminApi.getSessions().catch(() => [])
  candidates.value = await adminApi.getCandidates().catch(() => [])
  results.value = await adminApi.getResults().catch(() => [])
  auditData.value = await adminApi.getAudit().catch(() => [])
}

const handleNavigate = (section) => {
  currentSection.value = section
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const editSession = (s) => {
  // 实现编辑逻辑
  console.log('edit session', s)
}

const deleteSession = async (id) => {
  if (confirm('确认删除？')) {
    await adminApi.deleteSession(id)
    await loadData()
  }
}

const editCandidate = (c) => {
  console.log('edit candidate', c)
}

const deleteCandidate = async (id) => {
  if (confirm('确认删除？')) {
    await adminApi.deleteCandidate(id)
    await loadData()
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-layout {
  display: flex;
}

.admin-content {
  flex: 1;
  padding: 24px;
}

.section h2 {
  margin: 0 0 24px 0;
}

.add-btn {
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 16px;
}

.data-table {
  width: 100%;
  background: white;
  border-collapse: collapse;
  border-radius: 8px;
  overflow: hidden;
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-weight: 500;
}

.data-table button {
  padding: 4px 8px;
  margin-right: 8px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.status-ACTIVE { color: #52c41a; }
.status-INACTIVE { color: #999; }
.status-DRAFT { color: #faad14; }
.status-ENDED { color: #f5222d; }
</style>
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/views/LoginView.vue frontend/src/views/VoteView.vue frontend/src/views/AdminView.vue
git commit -m "feat: create frontend views (Login, Vote, Admin)"
```

---

### Task 14: 创建前端 Dockerfile

**Files:**
- Create: `frontend/Dockerfile`
- Create: `frontend/nginx.conf`

- [ ] **Step 1: 创建 nginx.conf**

```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

- [ ] **Step 2: 创建 frontend/Dockerfile**

```dockerfile
# Build stage
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

- [ ] **Step 3: Commit**

```bash
git add frontend/Dockerfile frontend/nginx.conf
git commit -m "feat: create frontend Dockerfile and nginx config"
```

---

### Task 15: 创建 docker-compose.yml

**Files:**
- Create: `docker-compose.yml`

- [ ] **Step 1: 创建 docker-compose.yml**

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: voting-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: voting_system
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./backend/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: voting-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/voting_system?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      WELINK_APP_ID: ${WELINK_APP_ID:-your-app-id}
      WELINK_APP_SECRET: ${WELINK_APP_SECRET:-your-app-secret}
      WELINK_REDIRECT_URI: ${WELINK_REDIRECT_URI:-http://localhost:8080/api/auth/welink/callback}
    depends_on:
      mysql:
        condition: service_healthy

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: voting-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

- [ ] **Step 2: Commit**

```bash
git add docker-compose.yml
git commit -m "feat: create docker-compose.yml for full stack deployment"
```

---

## 计划完成

### 任务总结

| Phase | Task | Description |
|-------|------|-------------|
| 1 | Task 1 | 创建后端 Spring Boot 项目结构 |
| 1 | Task 2 | 创建实体类 (User, Candidate, VoteSession, VoteRecord) |
| 1 | Task 3 | 创建 Repository 接口 |
| 1 | Task 4 | 创建 DTO 类 |
| 2 | Task 5 | 创建 Service 层 |
| 2 | Task 6 | 创建 Controller 层 |
| 2 | Task 7 | 创建配置类 (Security, CORS, Exception) |
| 2 | Task 8 | 创建后端 Dockerfile |
| 3 | Task 9 | 创建前端 Vue 3 项目结构 |
| 3 | Task 10 | 创建前端路由和 API 客户端 |
| 3 | Task 11 | 创建 Pinia Store |
| 3 | Task 12 | 创建前端组件 |
| 3 | Task 13 | 创建前端视图页面 |
| 3 | Task 14 | 创建前端 Dockerfile |
| 4 | Task 15 | 创建 docker-compose.yml |

---

### 后续步骤

1. 运行 `docker-compose up -d` 启动所有服务
2. 配置 Welink OAuth 应用并设置环境变量
3. 导入候选人数据
4. 创建管理员账号（直接操作数据库设置 role = 'ADMIN'）

**Plan saved to:** `docs/superpowers/plans/2026-04-18-votingSystem-implementation.md`
