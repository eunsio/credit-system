# 创新学分申领管理平台 - 概念模型ER图

## 一、完整概念模型ER图

使用经典Chen记号法（矩形=实体，椭圆=属性，菱形=关系）绘制的概念模型：

```mermaid
graph LR
    User["<b>用户</b>"]
    Competition["<b>比赛信息</b>"]
    CreditApply["<b>学分申领</b>"]
    Post["<b>社区帖子</b>"]
    Reply["<b>帖子回复</b>"]

    R1{"提交申请"}
    R2{"审批申请"}
    R3{"发布帖子"}
    R4{"发布比赛"}
    R5{"发表回复"}
    R6{"包含回复"}

    User ---|"1"| R1
    R1 ---|"N"| CreditApply
    User ---|"1"| R2
    R2 ---|"N"| CreditApply
    User ---|"1"| R3
    R3 ---|"N"| Post
    User ---|"1"| R4
    R4 ---|"N"| Competition
    User ---|"1"| R5
    R5 ---|"N"| Reply
    Post ---|"1"| R6
    R6 ---|"N"| Reply

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef relationship fill:#FFB74D,stroke:#EF6C00,stroke-width:2px,color:#fff,font-size:12px

    class User,Competition,CreditApply,Post,Reply entity
    class R1,R2,R3,R4,R5,R6 relationship
```

## 二、实体属性图

### 2.1 用户实体

```mermaid
graph LR
    User["<b>用户</b>"]
    U1(("编号<br/>主键"))
    U2(("用户名<br/>唯一键"))
    U3(("密码"))
    U4(("昵称"))
    U5(("头像"))
    U6(("手机号"))
    U7(("角色"))
    U8(("个人简介"))
    U9(("联系方式"))
    U10(("状态"))

    User --- U1
    User --- U2
    User --- U3
    User --- U4
    User --- U5
    User --- U6
    User --- U7
    User --- U8
    User --- U9
    User --- U10

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef uk fill:#64B5F6,stroke:#1565C0,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px

    class User entity
    class U1 pk
    class U2 uk
    class U3,U4,U5,U6,U7,U8,U9,U10 attr
```

### 2.2 比赛信息实体

```mermaid
graph LR
    Comp["<b>比赛信息</b>"]
    C1(("编号<br/>主键"))
    C2(("比赛名称"))
    C3(("比赛内容"))
    C4(("开始时间"))
    C5(("结束时间"))
    C6(("报名链接"))

    Comp --- C1
    Comp --- C2
    Comp --- C3
    Comp --- C4
    Comp --- C5
    Comp --- C6

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px

    class Comp entity
    class C1 pk
    class C2,C3,C4,C5,C6 attr
```

### 2.3 学分申领实体

```mermaid
graph LR
    Apply["<b>学分申领</b>"]
    A1(("编号<br/>主键"))
    A2(("获奖名称"))
    A3(("证明图片"))
    A4(("学分分值"))
    A5(("申领状态"))
    A6(("审批备注"))
    A7(("审批时间"))
    A8(("所属学年"))

    Apply --- A1
    Apply --- A2
    Apply --- A3
    Apply --- A4
    Apply --- A5
    Apply --- A6
    Apply --- A7
    Apply --- A8

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px

    class Apply entity
    class A1 pk
    class A2,A3,A4,A5,A6,A7,A8 attr
```

### 2.4 社区帖子实体

```mermaid
graph LR
    Post["<b>社区帖子</b>"]
    P1(("编号<br/>主键"))
    P2(("标题"))
    P3(("内容"))
    P4(("帖子类型"))

    Post --- P1
    Post --- P2
    Post --- P3
    Post --- P4

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px

    class Post entity
    class P1 pk
    class P2,P3,P4 attr
```

### 2.5 帖子回复实体

```mermaid
graph LR
    Reply["<b>帖子回复</b>"]
    R1(("编号<br/>主键"))
    R2(("昵称"))
    R3(("回复内容"))

    Reply --- R1
    Reply --- R2
    Reply --- R3

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px

    class Reply entity
    class R1 pk
    class R2,R3 attr
```

## 三、完整概念模型（含属性）

```mermaid
graph TB
    User["<b>用户</b>"]
    U1(("编号<br/>主键"))
    U2(("用户名<br/>唯一键"))
    U3(("昵称"))
    U4(("角色"))
    U5(("手机号"))
    U6(("状态"))

    User --- U1
    User --- U2
    User --- U3
    User --- U4
    User --- U5
    User --- U6

    Comp["<b>比赛信息</b>"]
    C1(("编号<br/>主键"))
    C2(("比赛名称"))
    C3(("比赛内容"))
    C4(("开始时间"))
    C5(("结束时间"))

    Comp --- C1
    Comp --- C2
    Comp --- C3
    Comp --- C4
    Comp --- C5

    Apply["<b>学分申领</b>"]
    A1(("编号<br/>主键"))
    A2(("获奖名称"))
    A3(("证明图片"))
    A4(("学分分值"))
    A5(("申领状态"))
    A6(("所属学年"))

    Apply --- A1
    Apply --- A2
    Apply --- A3
    Apply --- A4
    Apply --- A5
    Apply --- A6

    Post["<b>社区帖子</b>"]
    P1(("编号<br/>主键"))
    P2(("标题"))
    P3(("内容"))
    P4(("帖子类型"))

    Post --- P1
    Post --- P2
    Post --- P3
    Post --- P4

    Reply["<b>帖子回复</b>"]
    R1(("编号<br/>主键"))
    R2(("昵称"))
    R3(("回复内容"))

    Reply --- R1
    Reply --- R2
    Reply --- R3

    Rel1{"提交申请"}
    Rel2{"审批申请"}
    Rel3{"发布帖子"}
    Rel4{"发布比赛"}
    Rel5{"发表回复"}
    Rel6{"包含回复"}

    User ---|"1"| Rel1
    Rel1 ---|"N"| Apply
    User ---|"1"| Rel2
    Rel2 ---|"N"| Apply
    User ---|"1"| Rel3
    Rel3 ---|"N"| Post
    User ---|"1"| Rel4
    Rel4 ---|"N"| Comp
    User ---|"1"| Rel5
    Rel5 ---|"N"| Reply
    Post ---|"1"| Rel6
    Rel6 ---|"N"| Reply

    classDef entity fill:#1E88E5,stroke:#1565C0,stroke-width:3px,color:#fff,font-size:14px
    classDef pk fill:#E57373,stroke:#C62828,stroke-width:2px,color:#fff,font-size:11px
    classDef uk fill:#64B5F6,stroke:#1565C0,stroke-width:2px,color:#fff,font-size:11px
    classDef attr fill:#4DB6AC,stroke:#00897B,stroke-width:1px,color:#fff,font-size:11px
    classDef relationship fill:#FFB74D,stroke:#EF6C00,stroke-width:2px,color:#fff,font-size:12px

    class User,Comp,Apply,Post,Reply entity
    class U1,C1,A1,P1,R1 pk
    class U2 uk
    class U3,U4,U5,U6,C2,C3,C4,C5,A2,A3,A4,A5,A6,P2,P3,P4,R2,R3 attr
    class Rel1,Rel2,Rel3,Rel4,Rel5,Rel6 relationship
```

## 四、关系说明

| 关系 | 连接实体 | 基数 | 说明 |
|------|----------|------|------|
| 提交申请 | 用户 ↔ 学分申领 | 1 : N | 一个学生可提交多条学分申领 |
| 审批申请 | 用户 ↔ 学分申领 | 1 : N | 一个管理员可审批多条申领记录 |
| 发布帖子 | 用户 ↔ 社区帖子 | 1 : N | 一个用户可发布多条社区帖子 |
| 发布比赛 | 用户 ↔ 比赛信息 | 1 : N | 一个管理员可发布多条比赛信息 |
| 发表回复 | 用户 ↔ 帖子回复 | 1 : N | 一个用户可发表多条帖子回复 |
| 包含回复 | 社区帖子 ↔ 帖子回复 | 1 : N | 一个帖子可包含多条回复内容 |

## 五、符号说明

| 符号 | 图形 | 含义 | 颜色 |
|------|------|------|------|
| 实体 | 矩形 | 表示现实世界中的对象或概念 | 蓝色填充 |
| 属性 | 椭圆 | 表示实体的特征或性质 | 青色填充 |
| 主键 | 红色椭圆 | 唯一标识实体的属性 | 红色填充 |
| 唯一键 | 蓝色椭圆 | 取值唯一的属性 | 浅蓝填充 |
| 关系 | 菱形 | 表示实体间的联系 | 橙色填充 |
| 连线 | 直线 | 连接实体、属性和关系 | 灰色 |
| 基数 | 1/N | 标注在线上，表示一对一或一对多 | - |

---

**文档说明**：本概念模型ER图采用经典Chen记号法，聚焦于业务概念层面的实体、属性与关系，省略了外键字段和审计字段（创建时间/更新时间），突出展示系统的核心业务语义。完整物理数据模型请参考 [ER图.md](file:///d:/Code/credit-system/图表设计/ER图.md)。
