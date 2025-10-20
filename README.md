# 学生成绩查询系统 (Student Academic Performance Management System)

## 项目概述

本项目是一个基于Web的学生成绩查询系统，支持教师录入成绩、学生查询成绩，并具备时间段控制功能。系统采用前后端分离架构，集成腾讯云服务，提供高可用、可扩展的解决方案。

## 核心功能

### 学生功能
- ✅ 成绩查询（支持按学期筛选）
- ✅ 个人信息查询
- ✅ 时间段控制（只能在指定时间段内查询）

### 教师功能
- ✅ 单个成绩录入
- ✅ 批量成绩导入（Excel/CSV文件）
- ✅ 成绩管理（修改、删除）
- ✅ 时间段设置（控制成绩查询开放时间）

### 管理员功能
- ✅ 学生信息管理
- ✅ 用户管理
- ✅ 系统配置
- ✅ 数据统计

## 技术架构

### 前端技术栈
- **框架**: React 18 + TypeScript
- **UI组件库**: Ant Design
- **路由管理**: React Router v6
- **HTTP客户端**: Axios
- **状态管理**: React Query + Zustand
- **构建工具**: Vite

### 后端技术栈
- **框架**: Spring Boot 3.x + Java 17
- **安全框架**: Spring Security + JWT
- **数据访问**: Spring Data JPA
- **数据库**: MySQL
- **文件处理**: Apache POI
- **构建工具**: Maven

### 腾讯云服务
- **计算**: 腾讯云CVM + 弹性伸缩
- **数据库**: 腾讯云MySQL
- **缓存**: 腾讯云Redis
- **存储**: 腾讯云COS
- **CDN**: 腾讯云CDN
- **监控**: 腾讯云监控

## 项目结构

```
Student-Academic-Performance-Management-System/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── components/          # 通用组件
│   │   ├── pages/              # 页面组件
│   │   ├── services/           # API服务
│   │   ├── store/             # 状态管理
│   │   └── types/             # 类型定义
│   └── package.json
├── backend/                     # 后端项目
│   ├── src/main/java/
│   │   └── com/studentgrades/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 服务层
│   │       ├── repository/     # 数据访问层
│   │       ├── entity/         # 实体类
│   │       └── config/         # 配置类
│   └── pom.xml
├── database/                    # 数据库相关
│   ├── init/                   # 初始化脚本
│   └── migrations/             # 迁移文件
├── docs/                       # 项目文档
├── deployment/                 # 部署配置
└── README.md
```

## 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Docker (可选)

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/your-username/Student-Academic-Performance-Management-System.git
cd Student-Academic-Performance-Management-System
```

2. **启动数据库**
```bash
# 使用Docker启动MySQL
docker run --name mysql-db -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=student_grades -p 3306:3306 -d mysql:8.0
```

3. **启动后端服务**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

4. **启动前端服务**
```bash
cd frontend
npm install
npm run dev
```

5. **访问应用**
- 前端: http://localhost:3000
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

### 默认账户

- **管理员**: admin / admin123
- **教师**: teacher / teacher123
- **学生**: student / student123

## 部署指南

### Docker部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 腾讯云部署

1. **配置腾讯云CLI**
```bash
tccli configure
```

2. **部署基础设施**
```bash
cd deployment/tencentcloud/terraform
terraform init
terraform plan
terraform apply
```

3. **部署应用**
```bash
./scripts/deploy.sh
```

## API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/refresh` - 刷新Token

### 学生相关
- `GET /api/students/profile` - 获取个人信息
- `GET /api/students/grades` - 查询成绩
- `GET /api/students/grades/{semester}` - 按学期查询成绩

### 教师相关
- `POST /api/teachers/grades` - 录入成绩
- `PUT /api/teachers/grades/{id}` - 修改成绩
- `DELETE /api/teachers/grades/{id}` - 删除成绩
- `POST /api/teachers/grades/upload` - 批量导入成绩
- `POST /api/teachers/time-periods` - 设置查询时间段

### 管理员相关
- `GET /api/admin/students` - 获取学生列表
- `POST /api/admin/students` - 添加学生
- `PUT /api/admin/students/{id}` - 修改学生信息
- `DELETE /api/admin/students/{id}` - 删除学生

## 数据库设计

### 逻辑外键策略
- 数据库不使用任何物理外键；由服务层进行跨表存在性与删除保护校验。
- 通过唯一索引与普通索引保障查询性能与可引用性。
- 提供一致性审计任务与修复脚本，定期扫描并处理孤儿记录。

### 核心表结构
- **users**: 用户表
- **students**: 学生表
- **teachers**: 教师表
- **courses**: 课程表
- **grades**: 成绩表
- **time_periods**: 时间段控制表
- **file_uploads**: 文件上传记录表

详细设计请参考 [数据库设计文档](database/README.md)

## 安全特性

- JWT Token认证
- 基于角色的访问控制 (RBAC)
- 密码加密存储
- HTTPS传输加密
- SQL注入防护
- XSS攻击防护

## 性能优化

- 数据库索引优化
- Redis缓存
- CDN加速
- 连接池配置
- 查询优化

## 监控和日志

- 腾讯云监控
- 应用性能监控
- 错误追踪和告警
- 结构化日志记录

## 成本优化

- 使用按量计费实例
- COS智能分层
- MySQL基础版
- CDN缓存优化

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
aws云计算课程设计
