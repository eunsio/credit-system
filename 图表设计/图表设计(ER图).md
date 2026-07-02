# 创新学分申领管理平台 - 数据库ER图设计

## 一、数据库ER图

使用Mermaid语法绘制的数据库实体关系图：

```mermaid
erDiagram
    sys_user {
        bigint id PK "主键ID"
        varchar username UK "用户名/学号/工号"
        varchar password "密码"
        varchar nickname "昵称"
        varchar avatar "头像URL"
        varchar phone "绑定手机号"
        varchar role "角色: STUDENT, ADMIN, INSTRUCTOR"
        text bio "简介(主要用于教师)"
        varchar contact_info "联系方式(主要用于教师)"
        tinyint status "状态: 1-正常, 0-禁用"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }
    
    sys_competition {
        bigint id PK "主键ID"
        varchar title "比赛名称"
        text content "比赛详情介绍"
        datetime start_time "开始报名时间"
        datetime end_time "截止日期"
        varchar registration_url "报名系统跳转链接"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }
    
    sys_credit_apply {
        bigint id PK "主键ID"
        bigint user_id FK "申请学生ID"
        varchar award_title "获奖名称/项目名称"
        text image_url "获奖证明照片链接(多图逗号分隔)"
        decimal credit_score "申请学分分值"
        tinyint status "审核状态: 0-待审核, 1-已批准, 2-已驳回"
        varchar audit_remark "审批备注/驳回原因"
        datetime audit_time "审批时间"
        bigint auditor_id FK "审批人ID"
        varchar academic_year "所属学年"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }
    
    sys_community_post {
        bigint id PK "主键ID"
        bigint user_id FK "发布人ID"
        varchar title "标题"
        text content "详情内容"
        tinyint post_type "类型: 0-技术求助, 1-官方公告"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }
    
    sys_post_reply {
        bigint id PK "主键ID"
        bigint post_id FK "帖子ID"
        bigint user_id FK "回复人ID"
        varchar nickname "回复人昵称"
        text content "回复内容"
        datetime create_time "创建时间"
    }
    
    %% 关系定义
    sys_user ||--o{ sys_credit_apply : "提交申请"
    sys_user ||--o{ sys_community_post : "发布帖子"
    sys_user ||--o{ sys_post_reply : "回复帖子"
    sys_user ||--o{ sys_competition : "发布比赛"
    sys_community_post ||--o{ sys_post_reply : "帖子回复"
```

## 二、表关系说明

### 2.1 核心关系

| 关系 | 类型 | 说明 |
|------|------|------|
| sys_user → sys_credit_apply | 一对多 | 一个学生可以提交多条学分申请记录 |
| sys_user → sys_community_post | 一对多 | 一个用户可以发布多条社区帖子 |
| sys_user → sys_post_reply | 一对多 | 一个用户可以发布多条帖子回复 |
| sys_user → sys_competition | 一对多 | 一个管理员可以发布多条比赛信息 |
| sys_community_post → sys_post_reply | 一对多 | 一条帖子可以有多条回复 |

### 2.2 外键关系图

```mermaid
graph LR
    A[sys_user<br/>用户表] -->|user_id| B[sys_credit_apply<br/>学分申领表]
    A -->|user_id| C[sys_community_post<br/>社区帖子表]
    A -->|auditor_id| B
    A -->|user_id| D[sys_post_reply<br/>回复表]
    C -->|post_id| D
    
    style A fill:#e1f5ff
    style B fill:#fff3e0
    style C fill:#f3e5f5
    style D fill:#e8f5e9
```

## 三、索引设计

### 3.1 主键索引

所有表均使用 `bigint id` 作为自增主键，建立主键索引。

### 3.2 外键索引

为了提升查询性能，在外键字段上建立索引：

| 表名 | 索引名 | 字段 | 说明 |
|------|--------|------|------|
| sys_credit_apply | idx_user_id | user_id | 加速按学生查询申领记录 |
| sys_community_post | idx_user_id | user_id | 加速按用户查询帖子 |
| sys_post_reply | idx_post_id | post_id | 加速按帖子查询回复 |
| sys_post_reply | idx_user_id | user_id | 加速按用户查询回复 |

### 3.3 索引可视化

```mermaid
graph TB
    subgraph sys_credit_apply
        PK1[id PK]
        FK1[user_id FK + INDEX]
        FK2[auditor_id FK]
    end
    
    subgraph sys_community_post
        PK2[id PK]
        FK3[user_id FK + INDEX]
    end
    
    subgraph sys_post_reply
        PK3[id PK]
        FK4[post_id FK + INDEX]
        FK5[user_id FK + INDEX]
    end
    
    style PK1 fill:#ff6b6b
    style PK2 fill:#ff6b6b
    style PK3 fill:#ff6b6b
    style FK1 fill:#4ecdc4
    style FK3 fill:#4ecdc4
    style FK4 fill:#4ecdc4
    style FK5 fill:#4ecdc4
    style FK2 fill:#95a5a6
```

## 四、数据表详细设计

### 4.1 sys_user（用户表）

```mermaid
classDiagram
    class sys_user {
        +bigint id
        +varchar username
        +varchar password
        +varchar nickname
        +varchar avatar
        +varchar phone
        +varchar role
        +text bio
        +varchar contact_info
        +tinyint status
        +datetime create_time
        +datetime update_time
    }
    
    class STUDENT {
        学生角色
        可申领学分
        可发布求助
    }
    
    class ADMIN {
        管理员角色
        可审批申请
        可发布公告
    }
    
    class INSTRUCTOR {
        指导教师角色
        信息由管理员维护
    }
    
    sys_user --> STUDENT : role字段
    sys_user --> ADMIN : role字段
    sys_user --> INSTRUCTOR : role字段
```

**字段说明**：

- `username`：学号或工号，全局唯一
- `role`：角色类型，支持三种角色
  - STUDENT：学生
  - ADMIN：管理员
  - INSTRUCTOR：指导教师
- `bio` 和 `contact_info`：主要用于教师角色，存储教师简介和联系方式

### 4.2 sys_credit_apply（学分申领表）

```mermaid
stateDiagram-v2
    [*] --> 待审核: 学生提交申请
    待审核 --> 已批准: 管理员审批通过
    待审核 --> 已驳回: 管理员审批驳回
    已批准 --> [*]
    已驳回 --> [*]
    
    note right of 待审核
        status = 0
        等待管理员审核
    end note
    
    note right of 已批准
        status = 1
        学分已认定
    end note
    
    note right of 已驳回
        status = 2
        显示驳回原因
    end note
```

**状态流转说明**：

- `status` 字段控制申领状态
  - 0：待审核，学生提交后的初始状态
  - 1：已批准，管理员审核通过
  - 2：已驳回，管理员审核驳回，需填写驳回原因

### 4.3 sys_community_post（社区帖子表）

```mermaid
graph TB
    subgraph 帖子类型
        A[post_type = 0<br/>技术求助]
        B[post_type = 1<br/>官方公告]
    end
    
    subgraph 发布权限
        C[学生]
        D[管理员]
    end
    
    C -->|可发布| A
    D -->|可发布| A
    D -->|可发布| B
    C -.->|不可发布| B
    
    style A fill:#95e1d3
    style B fill:#f38181
    style C fill:#aa96da
    style D fill:#fcbad3
```

**帖子类型说明**：

- `post_type` 字段区分帖子类型
  - 0：技术求助帖，学生和管理员均可发布
  - 1：官方公告帖，仅管理员可发布

### 4.4 sys_post_reply（帖子回复表）

```mermaid
flowchart LR
    A[帖子详情页] --> B[查看帖子内容]
    B --> C{是否有回复?}
    C -->|是| D[显示回复列表]
    C -->|否| E[显示暂无回复]
    D --> F[回复输入框]
    E --> F
    F --> G[提交回复]
    G --> H[保存到sys_post_reply]
    H --> D
    
    style A fill:#e8f5e9
    style D fill:#fff3e0
    style F fill:#e1f5ff
    style H fill:#f3e5f5
```

**回复功能说明**：

- 一条帖子可以有多条回复
- 回复按创建时间升序排列
- 回复内容包含回复人昵称和内容

## 五、数据字典

### 5.1 角色类型字典

| 值 | 名称 | 说明 |
|------|------|------|
| STUDENT | 学生 | 系统主要使用者，可申领学分、发布求助 |
| ADMIN | 管理员 | 系统管理者，可审批、发布公告、管理数据 |
| INSTRUCTOR | 指导教师 | 信息由管理员维护，供学生查看 |

### 5.2 申领状态字典

| 值 | 名称 | 说明 |
|------|------|------|
| 0 | 待审核 | 学生提交后的初始状态，等待管理员审核 |
| 1 | 已批准 | 管理员审核通过，学分已认定 |
| 2 | 已驳回 | 管理员审核驳回，需查看驳回原因 |

### 5.3 帖子类型字典

| 值 | 名称 | 发布权限 |
|------|------|------|
| 0 | 技术求助 | 学生、管理员均可发布 |
| 1 | 官方公告 | 仅管理员可发布 |

### 5.4 用户状态字典

| 值 | 名称 | 说明 |
|------|------|------|
| 1 | 正常 | 用户账号正常可用 |
| 0 | 禁用 | 用户账号被禁用，无法登录 |

## 六、数据完整性约束

### 6.1 实体完整性

- 所有表的主键 `id` 设置为自增，保证唯一性
- 用户表的 `username` 字段设置唯一约束，避免重复注册

### 6.2 参照完整性

```mermaid
graph TB
    A[sys_user.id] -->|外键约束| B[sys_credit_apply.user_id]
    A -->|外键约束| C[sys_community_post.user_id]
    A -->|外键约束| D[sys_post_reply.user_id]
    E[sys_community_post.id] -->|外键约束| D
    
    style A fill:#ff6b6b
    style B fill:#4ecdc4
    style C fill:#4ecdc4
    style D fill:#4ecdc4
    style E fill:#ff6b6b
```

**外键约束说明**：

- `sys_credit_apply.user_id` 必须关联有效的 `sys_user.id`
- `sys_community_post.user_id` 必须关联有效的 `sys_user.id`
- `sys_post_reply.post_id` 必须关联有效的 `sys_community_post.id`
- `sys_post_reply.user_id` 必须关联有效的 `sys_user.id`

### 6.3 域完整性

| 字段 | 约束 | 说明 |
|------|------|------|
| username | NOT NULL, UNIQUE | 用户名不能为空且必须唯一 |
| password | NOT NULL | 密码不能为空 |
| role | NOT NULL | 角色不能为空，必须为指定值之一 |
| award_title | NOT NULL | 获奖名称不能为空 |
| image_url | NOT NULL | 获奖证明图片不能为空 |
| title | NOT NULL | 帖子标题不能为空 |
| content | NOT NULL | 帖子内容不能为空 |

## 七、数据量估算

### 7.1 预估数据规模

```mermaid
pie title 数据表预估规模
    "sys_user (用户表)" : 1000
    "sys_credit_apply (申领表)" : 5000
    "sys_community_post (帖子表)" : 500
    "sys_post_reply (回复表)" : 2000
    "sys_competition (比赛表)" : 100
```

### 7.2 容量规划

| 表名 | 预估记录数 | 单条记录大小 | 预估容量 |
|------|-----------|-------------|---------|
| sys_user | 1,000 | 500B | 500KB |
| sys_credit_apply | 5,000 | 1KB | 5MB |
| sys_community_post | 500 | 2KB | 1MB |
| sys_post_reply | 2,000 | 500B | 1MB |
| sys_competition | 100 | 2KB | 200KB |

**总计预估容量**：约 7.7MB，MySQL 容量规划充足。

---

**文档版本**：v1.0  
**创建日期**：2026年6月25日  
**创建人**：项目开发团队