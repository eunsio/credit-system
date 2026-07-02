/*
 创新学分申领管理平台 - 数据库初始化脚本
 数据库：credit_system
*/

CREATE DATABASE IF NOT EXISTS `credit_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `credit_system`;

-- 1. 用户基础表 (学生、管理员、指导教师)
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/学号/工号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '绑定手机号',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: STUDENT, ADMIN, INSTRUCTOR',
    `bio` TEXT DEFAULT NULL COMMENT '简介 (主要用于教师)',
    `contact_info` VARCHAR(255) DEFAULT NULL COMMENT '联系方式 (主要用于教师)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础表';

-- 2. 比赛信息表
CREATE TABLE IF NOT EXISTS `sys_competition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(100) NOT NULL COMMENT '比赛名称',
    `content` TEXT COMMENT '比赛详情介绍',
    `start_time` DATETIME COMMENT '开始报名时间',
    `end_time` DATETIME COMMENT '截止日期',
    `registration_url` VARCHAR(255) COMMENT '报名系统跳转链接',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛信息表';

-- 3. 创新学分申领记录表
CREATE TABLE IF NOT EXISTS `sys_credit_apply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '申请学生ID',
    `award_title` VARCHAR(100) NOT NULL COMMENT '获奖名称/项目名称',
    `image_url` TEXT NOT NULL COMMENT '获奖证明照片/文件链接（多图逗号分隔）',
    `credit_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '申请学分分值',
    `status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已批准, 2-已驳回',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审批备注/驳回原因',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `academic_year` VARCHAR(20) DEFAULT NULL COMMENT '所属学年 (用于获奖统计)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='创新学分申领记录表';

-- 4. 技术社区求助信息表
CREATE TABLE IF NOT EXISTS `sys_community_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '发布人ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '详情内容',
    `post_type` TINYINT NOT NULL DEFAULT 0 COMMENT '信息类型: 0-技术求助, 1-官方支持公告',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术社区信息表';

-- 5. 帖子回复表
CREATE TABLE IF NOT EXISTS `sys_post_reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '回复人ID',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '回复人昵称',
    `content` TEXT NOT NULL COMMENT '回复内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子回复表';

-- 预置测试数据

-- 1. 用户数据 (密码均为 123456)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `phone`, `bio`, `contact_info`, `status`) VALUES 
('admin', '123456', '系统管理员', 'ADMIN', '13800000001', '负责全校创新学分系统的维护与审核', 'admin@in12.cn', 1),
('student01', '123456', '张三', 'STUDENT', '13800000002', '2023级计算机科学与技术1班', NULL, 1),
('student02', '123456', '李四', 'STUDENT', '13800000003', '2023级软件工程2班', NULL, 1),
('student03', '123456', '王五', 'STUDENT', '13800000005', '2024级数据科学1班', NULL, 1),
('student04', '123456', '赵六', 'STUDENT', '13800000006', '2022级物联网工程1班', NULL, 1),
('student05', '123456', '钱七', 'STUDENT', '13800000007', '2022级人工智能2班', NULL, 1),
('teacher01', '123456', '王老师', 'INSTRUCTOR', '13800000004', '计算机学院教授，国家级竞赛金牌教练', 'wang@in12.cn', 1),
('teacher02', '123456', '陈老师', 'INSTRUCTOR', '13800000008', '创新创业学院导师，擅长项目孵化', 'chen@in12.cn', 1),
('teacher03', '123456', '林老师', 'INSTRUCTOR', '13800000009', '电子工程学院副教授，嵌入式开发专家', 'lin@in12.cn', 1),
('student06', '123456', '孙八', 'STUDENT', '13800000010', '2023级计算机科学与技术2班', NULL, 1),
('student07', '123456', '周九', 'STUDENT', '13800000011', '2023级软件工程1班', NULL, 1),
('student08', '123456', '吴十', 'STUDENT', '13800000012', '2024级人工智能1班', NULL, 1),
('student09', '123456', '郑十一', 'STUDENT', '13800000013', '2024级数据科学2班', NULL, 1),
('student10', '123456', '冯十二', 'STUDENT', '13800000014', '2022级计算机科学与技术3班', NULL, 1),
('student11', '123456', '陈十三', 'STUDENT', '13800000015', '2022级软件工程3班', NULL, 1),
('student12', '123456', '褚十四', 'STUDENT', '13800000016', '2023级物联网工程2班', NULL, 1),
('student13', '123456', '卫十五', 'STUDENT', '13800000017', '2023级电子信息工程1班', NULL, 1),
('student14', '123456', '蒋十六', 'STUDENT', '13800000018', '2024级通信工程1班', NULL, 1),
('student15', '123456', '沈十七', 'STUDENT', '13800000019', '2022级网络工程1班', NULL, 1),
('student16', '123456', '韩十八', 'STUDENT', '13800000020', '2024级信息安全1班', NULL, 1),
('student17', '123456', '杨十九', 'STUDENT', '13800000021', '2023级数字媒体技术1班', NULL, 1),
('student18', '123456', '朱二十', 'STUDENT', '13800000022', '2022级智能科学与技术1班', NULL, 1),
('teacher04', '123456', '张老师', 'INSTRUCTOR', '13800000023', '数学与统计学院教授，数学建模竞赛指导专家', 'zhang@in12.cn', 1),
('teacher05', '123456', '刘老师', 'INSTRUCTOR', '13800000024', '信息工程学院讲师，ACM校队金牌教练', 'liu@in12.cn', 1),
('teacher06', '123456', '黄老师', 'INSTRUCTOR', '13800000025', '艺术学院副教授，设计类竞赛指导', 'huang@in12.cn', 1);

-- 2. 比赛数据
INSERT INTO `sys_competition` (`title`, `content`, `start_time`, `end_time`, `registration_url`) VALUES 
('第十七届蓝桥杯全国软件大赛', '蓝桥杯大赛是由工业和信息化部人才交流中心主办的全国性IT学科赛事。', '2026-05-01 00:00:00', '2026-06-30 23:59:59', 'https://dasai.lanqiao.cn/'),
('2026中国大学生计算机设计大赛', '旨在激发大学生学习计算机知识和技能的兴趣，提高运用信息技术解决实际问题的综合能力。', '2026-04-01 00:00:00', '2026-05-20 23:59:59', 'http://jsjds.com.cn/'),
('ACM-ICPC 2026 区域赛', '世界上公认的规模最大、水平最高的国际大学生程序设计竞赛。', '2026-09-01 00:00:00', '2026-12-31 23:59:59', 'https://icpc.global/'),
('第八届“互联网+”大学生创新创业大赛', '深化高等教育综合改革，激发大学生的创造力。', '2026-03-01 00:00:00', '2026-08-31 23:59:59', 'https://cy.ncss.cn/'),
('“挑战杯”全国大学生课外学术科技作品竞赛', '被誉为中国大学生科技创新创业的“奥林匹克”盛会。', '2026-01-10 00:00:00', '2026-11-20 23:59:59', 'http://www.tiaozhanbei.net/'),
('全国大学生数学建模竞赛', '创办于1992年，每年一届，是全国高校规模最大的基础课程竞赛。', '2026-09-10 00:00:00', '2026-09-13 23:59:59', 'http://www.mcm.edu.cn/'),
('全国大学生电子设计竞赛', '教育部和工信部共同发起的大学生学科竞赛，是电子信息领域最具影响力的赛事之一。', '2026-05-15 00:00:00', '2026-07-31 23:59:59', 'http://www.nuedc.com.cn/'),
('全国大学生机器人大赛 RoboMaster', '由大疆创新发起并承办的全球性射击对抗类机器人比赛。', '2026-03-01 00:00:00', '2026-06-15 23:59:59', 'https://www.robomaster.com/'),
('中国大学生服务外包创新创业大赛', '紧贴服务外包和创新创业主题，以应用为导向推动产学融合。', '2026-04-01 00:00:00', '2026-07-20 23:59:59', 'http://www.fwwb.org.cn/'),
('全国大学生信息安全竞赛', '由教育部高等学校信息安全专业教学指导委员会主办的网络安全领域A类赛事。', '2026-03-15 00:00:00', '2026-05-31 23:59:59', 'http://www.ciscn.cn/'),
('“中国软件杯”大学生软件设计大赛', '由工信部、教育部联合主办的面向全国高校学生的软件设计大赛。', '2026-02-01 00:00:00', '2026-06-30 23:59:59', 'http://www.cnsoftbei.com/'),
('全国大学生英语竞赛', '全国性大学英语学科竞赛，分为A/B/C/D四个类别。', '2026-03-01 00:00:00', '2026-04-15 23:59:59', 'http://www.chinaneccs.cn/');

-- 3. 学分申领数据 (涵盖各种状态、分值和学年)
INSERT INTO `sys_credit_apply` (`user_id`, `award_title`, `image_url`, `credit_score`, `status`, `academic_year`, `audit_remark`, `auditor_id`, `audit_time`) VALUES 
(2, '蓝桥杯Java B组一等奖', 'http://oss.in12.cn/awards/student01_lqb.jpg', 3.0, 1, '2025-2026', '证书核验无误，符合认定标准', 1, '2026-05-20 10:00:00'),
(2, '计算机设计大赛省二等奖', 'http://oss.in12.cn/awards/student01_jsj.jpg', 1.5, 0, '2025-2026', NULL, NULL, NULL),
(3, '中国大学生数学建模竞赛国二', 'http://oss.in12.cn/awards/student02_sxjm.jpg', 4.0, 1, '2025-2026', '国家级二等奖，予以认定', 1, '2026-05-21 09:30:00'),
(3, '校内优秀志愿者', 'http://oss.in12.cn/awards/student02_zyz.jpg', 0.5, 2, '2025-2026', '非学术或技能类竞赛，不属于创新学分范畴', 1, '2026-05-21 11:00:00'),
(4, '英语四级证书 (425分)', 'http://oss.in12.cn/awards/student03_cet4.jpg', 1.0, 2, '2025-2026', '英语等级证书不计入创新学分，属于通用学分', 1, '2026-05-21 14:00:00'),
(5, '“互联网+”省赛金奖', 'http://oss.in12.cn/awards/student04_hlw.jpg', 5.0, 1, '2024-2025', '优秀项目，准予通过', 1, '2025-10-15 16:20:00'),
(5, '校机器人大赛三等奖', 'http://oss.in12.cn/awards/student04_jqr.jpg', 0.5, 1, '2024-2025', '准予通过', 1, '2025-12-01 08:45:00'),
(6, '实用新型专利一项', 'http://oss.in12.cn/awards/student05_zl.jpg', 2.5, 0, '2025-2026', NULL, NULL, NULL),
(10, '蓝桥杯Python组国赛三等奖', 'http://oss.in12.cn/awards/student06_lqbpy.jpg', 2.5, 1, '2025-2026', '证书核验通过，符合认定标准', 1, '2026-05-18 15:30:00'),
(11, '中国软件杯省赛一等奖', 'http://oss.in12.cn/awards/student07_softcup.jpg', 1.5, 0, '2025-2026', NULL, NULL, NULL),
(12, '计算机设计大赛国赛三等奖', 'http://oss.in12.cn/awards/student08_jsjds.jpg', 2.0, 2, '2025-2026', '证明材料模糊不清，请重新上传清晰图片后再次申请', 7, '2026-05-19 10:15:00'),
(10, 'RoboMaster机甲大师区域赛二等奖', 'http://oss.in12.cn/awards/student06_rm.jpg', 3.5, 1, '2024-2025', '奖项属实，予以认定', 24, '2025-09-20 14:00:00'),
(10, '全国大学生信息安全竞赛CTF省一等奖', 'http://oss.in12.cn/awards/student06_ctf.jpg', 2.0, 0, '2025-2026', NULL, NULL, NULL),
(11, '电子设计竞赛省三等奖', 'http://oss.in12.cn/awards/student07_dz.jpg', 1.5, 1, '2025-2026', '证书核验无误，准予通过', 1, '2026-05-21 08:45:00'),
(11, '全国大学生英语竞赛C类二等奖', 'http://oss.in12.cn/awards/student07_nec.jpg', 1.0, 2, '2025-2026', '英语竞赛按学校规定不计入创新学分范畴', 8, '2026-05-20 16:30:00'),
(12, '数学建模竞赛省二等奖', 'http://oss.in12.cn/awards/student08_sxjm.jpg', 2.5, 1, '2025-2026', '省级二等奖，予以认定', 23, '2026-05-19 11:20:00'),
(12, '挑战杯校赛金奖', 'http://oss.in12.cn/awards/student08_tzb.jpg', 1.0, 0, '2025-2026', NULL, NULL, NULL),
(13, '服务外包创新创业大赛国赛三等奖', 'http://oss.in12.cn/awards/student09_fwwb.jpg', 3.0, 1, '2024-2025', '项目成果突出，准予通过', 1, '2025-11-10 09:00:00'),
(13, '互联网+校赛银奖', 'http://oss.in12.cn/awards/student09_hlw.jpg', 0.5, 2, '2025-2026', '校赛级别奖项不符合创新学分认定最低要求', 25, '2026-05-18 14:30:00'),
(14, 'ACM-ICPC区域赛铜牌', 'http://oss.in12.cn/awards/student10_acm.jpg', 4.0, 1, '2024-2025', '国家级铜奖，予以高学分认定', 1, '2025-12-20 10:00:00'),
(14, '中国软件杯国赛二等奖', 'http://oss.in12.cn/awards/student10_softcup.jpg', 3.0, 0, '2025-2026', NULL, NULL, NULL),
(15, '蓝桥杯C++组省赛一等奖', 'http://oss.in12.cn/awards/student11_lqbcpp.jpg', 2.0, 1, '2025-2026', '奖项属实，符合认定标准', 7, '2026-05-20 13:45:00'),
(15, '发明专利一项', 'http://oss.in12.cn/awards/student11_fmzl.jpg', 5.0, 0, '2025-2026', NULL, NULL, NULL),
(16, '数学建模竞赛国赛一等奖', 'http://oss.in12.cn/awards/student12_sxjm.jpg', 5.0, 1, '2024-2025', '国家级一等奖，优秀项目', 23, '2025-10-08 09:30:00'),
(16, '英语六级550+证书', 'http://oss.in12.cn/awards/student12_cet6.jpg', 1.0, 2, '2025-2026', '英语等级证书按学校规定不计入创新学分范畴', 1, '2026-05-17 15:00:00'),
(17, '电子设计竞赛国赛二等奖', 'http://oss.in12.cn/awards/student13_dzds.jpg', 4.0, 1, '2025-2026', '国家级二等奖，予以高学分认定', 8, '2026-05-16 10:30:00'),
(17, '校园黑客马拉松大赛一等奖', 'http://oss.in12.cn/awards/student13_hacker.jpg', 0.5, 1, '2025-2026', '竞赛成果突出，准予通过', 8, '2026-05-21 11:15:00'),
(18, '计算机设计大赛省一等奖', 'http://oss.in12.cn/awards/student14_jsj.jpg', 2.0, 0, '2025-2026', NULL, NULL, NULL),
(18, '软件著作权登记', 'http://oss.in12.cn/awards/student14_rz.jpg', 1.5, 2, '2024-2025', '软件著作权需附带源代码证明及开发文档', 24, '2025-12-10 09:45:00'),
(19, '信息安全竞赛CTF国赛二等奖', 'http://oss.in12.cn/awards/student15_ctf.jpg', 4.0, 1, '2025-2026', '竞赛成绩优异，予以认定', 7, '2026-05-15 16:00:00'),
(19, '挑战杯省赛三等奖', 'http://oss.in12.cn/awards/student15_tzb.jpg', 1.5, 0, '2025-2026', NULL, NULL, NULL),
(20, '机器人竞赛校赛一等奖', 'http://oss.in12.cn/awards/student16_jqr.jpg', 1.0, 1, '2025-2026', '获奖情况属实，准予通过', 24, '2026-05-14 14:20:00'),
(20, '互联网+省赛铜奖', 'http://oss.in12.cn/awards/student16_hlw.jpg', 2.0, 0, '2025-2026', NULL, NULL, NULL),
(21, '蓝桥杯嵌入式组省赛二等奖', 'http://oss.in12.cn/awards/student17_lqbqd.jpg', 2.0, 1, '2025-2026', '证书核验无误，符合认定标准', 1, '2026-05-13 09:10:00'),
(21, '校园创新项目立项', 'http://oss.in12.cn/awards/student17_cxxm.jpg', 0.5, 2, '2025-2026', '创新项目立项不等同于竞赛获奖，不予认定', 25, '2026-05-20 10:30:00'),
(22, 'ACM-ICPC邀请赛银牌', 'http://oss.in12.cn/awards/student18_acm.jpg', 5.0, 1, '2024-2025', '国家级银奖，予以最高学分认定', 1, '2025-11-25 09:00:00'),
(22, '中国软件杯国赛三等奖', 'http://oss.in12.cn/awards/student18_softcup.jpg', 3.0, 0, '2025-2026', NULL, NULL, NULL),
(2, '大学生创新项目国家级立项', 'http://oss.in12.cn/awards/student01_cxxm.jpg', 2.5, 1, '2024-2025', '国家级大创项目，予以认定', 1, '2025-12-15 10:40:00'),
(6, '国家奖学金', 'http://oss.in12.cn/awards/student05_gjjxj.jpg', 0.5, 2, '2025-2026', '奖学金不属于创新学分认定范畴', 8, '2026-05-19 09:00:00'),
(5, '人工智能大赛省赛二等奖', 'http://oss.in12.cn/awards/student04_ai.jpg', 2.5, 1, '2025-2026', '奖项属实，准予认定', 7, '2026-05-18 16:45:00');

-- 4. 技术社区数据
INSERT INTO `sys_community_post` (`user_id`, `title`, `content`, `post_type`) VALUES 
(1, '【官方】2026年度创新学分申领指引', '请各位同学在提交申请时，确保图片清晰，学年选择正确。如有疑问请咨询各院系教务办。', 1),
(1, '【公告】关于严厉打击学分申领造假行为的通知', '发现证书造假者，将取消本年度所有评优评先资格并记入档案。', 1),
(2, 'Spring Cloud Gateway 网关超时问题怎么调优？', '在高并发压测下，网关经常出现 504 Timeout，求大牛给个优化配置方案。', 0),
(3, '寻求ACM校队组队队友', '本人大二，擅长动态规划和图论，希望能找两位队友一起备战下半年的区域赛。', 0),
(4, 'Vue3 + Vite 项目打包体积太大怎么办？', '引入了太多第三方库，生成的 vendor.js 很大，首屏加载慢。', 0),
(5, '求教：如何使用 Docker 部署 Nacos 集群？', '按照官方文档配了但是节点之间无法互相通信，求避坑指南。', 0),
(1, '【专家分享】嵌入式开发的职业规划与学习路径', '作为一名老师，我看过很多学生走弯路，这里分享一些核心技能点...', 1),
(1, '【公告】2026年春季创新学分认定时间安排', '本学期创新学分集中认定时间为5月20日至6月10日，请各位同学合理安排申请时间。', 1),
(1, '【官方】创新学分常见问题答疑汇总', 'Q：哪些比赛可以申请创新学分？A：详见学校发布的《创新学分竞赛目录清单》。Q：同一比赛多个奖项能否重复申领？A：同一赛事按最高等级认定，不可叠加。', 1),
(1, '【专家分享】如何撰写高质量的创业计划书', '一个好的计划书应该包含市场分析、商业模式、财务预测等核心模块，以下是我多年辅导经验的总结...', 1),
(10, '求推荐适合新手的算法刷题路线', '大一刚学完C语言和数据结构基础，想开始刷算法题，不知道从哪入手比较好，求大佬指点。', 0),
(11, 'Spring Boot 3 整合 MyBatis Plus 遇到分页插件不生效', '按照官方文档配了分页拦截器，但是查询的时候还是查全表，没有自动分页，配置如下...', 0),
(12, 'Redis 缓存穿透和缓存击穿有什么区别？', '看了一些博客还是半懂不懂，有没有通俗易懂的解释？生产环境一般怎么解决？', 0),
(13, 'Git 多人协作时遇到冲突怎么优雅地解决？', '团队四个人一起开发，经常遇到 merge conflict 把代码搞乱的尴尬情况，求最佳实践。', 0),
(14, 'Python 爬虫如何绕过网站的反爬机制？', '最近在做数据采集，目标网站有验证码和请求频率限制，尝试了 Selenium 效果不理想。', 0),
(15, '求助：数据库慢查询优化思路', '线上有个接口耗时3秒多，排查发现是SQL查询太慢，EXPLAIN 显示全表扫描，加了索引效果也不明显。', 0),
(16, '大二学生，想参加蓝桥杯应该怎么准备？', '目前只会Java基础，听说蓝桥杯对算法要求比较高，求过来人分享备考经验和推荐资料。', 0),
(17, 'Docker Compose 编排多个微服务时网络不通', '用 docker-compose 部署了Nacos、MySQL和几个Spring Boot服务，服务注册成功了但调用接口报connect refused。', 0),
(18, '毕业设计选题求助：推荐一些适合本科生的项目方向', '大四了要开始做毕设，想做Java Web相关的，最好是能体现一定技术含量的，求推荐。', 0),
(19, 'VSCode 远程开发配置SSH总是超时', '按照教程配了 Remote-SSH 插件，config 文件也没问题，但是连接十几秒后就断开，是网络问题还是配置问题？', 0),
(20, '聊聊微服务架构下的分布式事务方案', '我们团队在用Seata做分布式事务，但是AT模式的性能损耗有点大，想了解其他方案的优劣对比。', 0),
(22, 'Linux服务器被挖矿病毒入侵了怎么排查？', '今天发现CPU飙到100%，top一看有个陌生进程占用大量资源，kill掉又自动重启，怀疑中了挖矿木马。', 0),
(1, '【专家分享】ACM竞赛入门到进阶的系统学习法', '从大一零基础到大三拿铜牌，我指导过的学生走过的路，这里总结了一套高效训练方法...', 1);
