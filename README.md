# 数字孪生管道系统

## 项目介绍
数字孪生管道系统是一个前后端分离的应用，用于模拟和监测管道的运行状态。该系统可以实时显示管道的各项参数，运行模拟，并记录历史数据。

## 技术栈

### 后端
- Java Spring Boot
- Spring Data JPA
- MySQL
- JWT

### 前端
- Vue 3
- Element Plus
- Three.js
- Axios

## 项目结构

```
.
├── backend/          # 后端代码
├── frontend/         # 前端代码
├── database/         # 数据库脚本
│   └── create_tables.sql  # 建表语句
├── README.md         # 项目说明
└── .gitignore        # Git忽略配置
```

## 环境要求
- Java 8+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+

## 安装和运行

### 1. 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE pipe_digital_twin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出MySQL
exit
```

### 2. 后端配置
1. 复制配置文件示例
   ```bash
   cd backend/src/main/resources
   cp application.properties.example application.properties
   ```
2. 修改配置文件
   ```properties
   # 数据库密码
   spring.datasource.password=your_database_password
   
   # JWT密钥
   jwt.secret=your_jwt_secret_key
   ```

### 3. 后端运行
```bash
cd backend
mvn spring-boot:run
```

### 4. 前端运行
```bash
cd frontend
npm install
npm run dev
```

## 配置说明

### 后端配置
主要配置文件：`backend/src/main/resources/application.properties`

### 前端配置
主要配置文件：`frontend/src/api/axios.js`

## 功能说明
- 管道模拟
- 实时监测
- 历史数据查询
- 健康状态评估
- 故障模拟

## 许可证
MIT