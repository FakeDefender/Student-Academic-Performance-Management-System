# 学生成绩查询系统 (Student Academic Performance Management System)

## 项目概述

本项目是一个基于Web的学生成绩查询系统，支持教师录入成绩、学生查询成绩，并具备时间段控制功能。系统采用前后端分离架构，使用Spring Boot + React技术栈，集成Redis缓存和腾讯云服务，提供高可用、可扩展的解决方案。

## 核心功能

### 学生功能
- ✅ 成绩查询（支持按学期、学年筛选）
- ✅ 个人信息管理（查看和修改个人基本信息）
- ✅ 时间段控制（只能在指定时间段内查询成绩）

### 教师功能
- ✅ 单个成绩录入
- ✅ 批量成绩导入（Excel/CSV文件）
- ✅ 成绩管理（修改、删除）
- ✅ 我的课程（查看教授的课程列表）
- ✅ 课程学生查询（查看课程的所有学生及其成绩）
- ✅ 时间段设置（控制成绩查询开放时间）
- ✅ 文件上传（支持Excel/CSV文件批量导入）

### 管理员功能
- ✅ 学生信息管理
- ✅ 教师信息管理
- ✅ 课程信息管理
- ✅ 时间段管理
- ✅ 系统监控（缓存管理和限流测试）

## 技术架构

### 前端技术栈
- **框架**: React 18.3.1 + TypeScript
- **UI组件库**: Ant Design 5.19.4
- **路由管理**: React Router v6.26.2
- **HTTP客户端**: Axios 1.7.7
- **状态管理**: React Hooks + localStorage
- **构建工具**: Vite
- **开发语言**: TypeScript

### 后端技术栈
- **框架**: Spring Boot 3.5.6 + Java 21
- **安全框架**: 自定义JWT实现
- **数据访问**: MyBatis 3.0.3
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **文件处理**: Apache POI
- **构建工具**: Maven
- **文档**: Springdoc OpenAPI 3

### 腾讯云服务
- **数据库**: 腾讯云MySQL (43.139.178.197:13306)
- **缓存**: 腾讯云Redis (43.139.178.197:26739)
- **监控**: 腾讯云监控

## 快速开始

### 环境要求

- Node.js 18+
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/your-username/Student-Academic-Performance-Management-System.git
cd Student-Academic-Performance-Management-System
```

2. **配置数据库**
```bash
# 连接腾讯云MySQL数据库
# 主机: xxxxxx
# 端口: xxxx
# 用户名: xxxxx
# 密码: xxxxx
# 数据库: student_grades
```

3. **配置Redis**
```bash
# 连接腾讯云Redis
# 主机: xxxxxx
# 端口: xxxx
# 用户名: xxxxx
# 密码: xxxxx
# 数据库: 0
```

4. **启动后端服务**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

5. **启动前端服务**
```bash
cd frontend
npm install
npm run dev
```

6. **访问应用**
- 前端: http://localhost:5174
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

### 默认账户

- **管理员**: admin / admin123
- **教师**: teacher / teacher123
- **学生**: student / student123

## 部署指南

### 开发环境部署

1. **后端部署**
```bash
cd backend
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

2. **前端部署**
```bash
cd frontend
npm run build
# 将dist目录部署到Web服务器
```

### 生产环境部署

1. **后端部署**
- 使用Spring Boot内嵌Tomcat
- 配置JVM参数
- 设置环境变量

2. **前端部署**
- 使用Nginx作为Web服务器
- 配置反向代理
- 启用Gzip压缩

## API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 学生相关
- `GET /api/students/profile` - 获取个人信息
- `PUT /api/students/profile` - 更新个人信息
- `GET /api/grades/student/{studentId}` - 按学生ID查询成绩
- `GET /api/grades/student/by-number/{studentNumber}` - 按学号查询成绩

### 教师相关
- `GET /api/courses/my-courses` - 获取我的课程
- `GET /api/courses/{courseId}/students` - 获取课程学生
- `POST /api/grades` - 录入成绩
- `PUT /api/grades/{id}` - 修改成绩
- `GET /api/grades/teacher/student/{studentNumber}` - 教师查询学生成绩
- `POST /api/files/grades/import` - 批量导入成绩
- `GET /api/files/grades/template` - 下载导入模板

### 管理员相关
- `GET /api/students` - 获取学生列表
- `POST /api/students` - 添加学生
- `PUT /api/students/{id}` - 修改学生信息
- `DELETE /api/students/{id}` - 删除学生
- `GET /api/teachers` - 获取教师列表
- `POST /api/teachers` - 添加教师
- `PUT /api/teachers/{id}` - 修改教师信息
- `DELETE /api/teachers/{id}` - 删除教师
- `GET /api/courses` - 获取课程列表
- `POST /api/courses` - 添加课程
- `PUT /api/courses/{id}` - 修改课程信息
- `DELETE /api/courses/{id}` - 删除课程
- `GET /api/time-periods` - 获取时间段列表
- `POST /api/time-periods` - 添加时间段
- `PUT /api/time-periods/{id}` - 修改时间段
- `DELETE /api/time-periods/{id}` - 删除时间段

### 系统管理
- `GET /api/test/cache` - 测试缓存功能
- `GET /api/test/rate-limit` - 测试限流功能
- `POST /api/test/clear-all-cache` - 清除所有缓存
- `GET /api/test/cache-status` - 检查缓存状态

## 数据库设计

### 逻辑外键策略
- 数据库不使用任何物理外键；由服务层进行跨表存在性与删除保护校验
- 通过唯一索引与普通索引保障查询性能与可引用性
- 提供一致性审计任务与修复脚本，定期扫描并处理孤儿记录

### 核心表结构
- **users**: 用户表
- **students**: 学生表
- **teachers**: 教师表
- **courses**: 课程表
- **grades**: 成绩表
- **time_periods**: 时间段控制表
- **teacher_courses**: 教师课程关联表
- **course_time_periods**: 课程时间段表

详细设计请参考 [数据库设计文档](数据库设计.md)

## 安全特性

- JWT Token认证（自定义实现）
- 基于角色的访问控制 (RBAC)
- 密码加密存储
- HTTPS传输加密
- SQL注入防护（MyBatis参数化查询）
- XSS攻击防护
- 接口限流控制
- 缓存安全配置

## 性能优化

- Redis缓存策略（@Cacheable注解）
- 接口限流控制（@RateLimit注解）
- 数据库索引优化
- MyBatis查询优化
- 连接池配置
- Vite构建优化
- Ant Design组件优化

## 监控和日志

- Spring Boot Actuator健康检查
- 腾讯云监控
- 缓存状态监控
- 限流状态监控
- 应用性能监控
- 错误追踪和告警
- 结构化日志记录

## 特色功能

### 1. 时间段控制
- 全局时间段控制
- 课程级别时间段控制
- 时间段开放状态检查

### 2. 批量导入
- Excel文件解析（Apache POI）
- CSV文件解析
- 数据验证和错误处理
- 导入模板下载

### 3. 缓存和限流
- Redis缓存集成
- 注解式缓存控制
- 注解式限流控制
- 缓存清理机制

### 4. 自定义JWT实现
- 完全自主实现的JWT认证系统
- 角色权限控制
- Token刷新机制

### 5. 逻辑外键设计
- 不使用数据库物理外键
- 服务层数据一致性校验
- 灵活的数据库设计

## 开发指南

### 代码规范
- 前端: ESLint + Prettier
- 后端: Checkstyle + SpotBugs
- 提交信息: Conventional Commits

### 测试
```bash
# 前端测试
cd frontend
npm run test

# 后端测试
cd backend
mvn test
```

### 构建
```bash
# 前端构建
cd frontend
npm run build

# 后端构建
cd backend
mvn clean package
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目链接: [https://github.com/your-username/Student-Academic-Performance-Management-System](https://github.com/your-username/Student-Academic-Performance-Management-System)
- 问题反馈: [Issues](https://github.com/your-username/Student-Academic-Performance-Management-System/issues)

## 更新日志

### v1.0.0 (2025-01-XX)
- 初始版本发布
- 基础功能实现
- 腾讯云服务集成
- 完整的文档和部署指南
- 自定义JWT认证系统
- Redis缓存和限流功能
- 批量导入功能
- 时间段控制功能
- 课程学生查询功能
- 个人信息管理功能

## 技术亮点

### 1. 自定义JWT实现
- 完全自主实现的JWT认证系统
- 角色权限控制
- Token刷新机制

### 2. 逻辑外键设计
- 不使用数据库物理外键
- 服务层数据一致性校验
- 灵活的数据库设计

### 3. 缓存和限流
- 注解式缓存控制
- 注解式限流控制
- Redis集成优化

### 4. 文件处理
- 多格式文件支持
- 文件类型检测
- 批量数据处理

### 5. 时间段控制
- 全局时间段控制
- 课程级别时间段控制
- 灵活的时间管理

## 项目结构

详细的项目结构请参考 [项目目录结构文档](项目目录结构.md)

## 系统架构

详细的系统架构请参考 [系统架构设计文档](系统架构设计.md)