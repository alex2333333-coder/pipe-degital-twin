-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  nickname VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建管道表
CREATE TABLE IF NOT EXISTS pipes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  length DOUBLE NOT NULL,
  start_point VARCHAR(100) NOT NULL,
  end_point VARCHAR(100) NOT NULL,
  diameter DOUBLE,
  material VARCHAR(50),
  construction_date DATE,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建监测点表
CREATE TABLE IF NOT EXISTS monitoring_points (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  pipe_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  location VARCHAR(100),
  latitude DOUBLE,
  longitude DOUBLE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (pipe_id) REFERENCES pipes(id) ON DELETE CASCADE
);

-- 创建监测数据表
CREATE TABLE IF NOT EXISTS monitoring_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  monitoring_point_id BIGINT NOT NULL,
  pressure DOUBLE,
  flow DOUBLE,
  temperature DOUBLE,
  vibration DOUBLE,
  alarm_status VARCHAR(50),
  data_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (monitoring_point_id) REFERENCES monitoring_points(id) ON DELETE CASCADE
);

-- 创建管道参数表
CREATE TABLE IF NOT EXISTS pipe_parameters (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  length DOUBLE NOT NULL,
  diameter DOUBLE NOT NULL,
  thickness DOUBLE NOT NULL,
  material_density DOUBLE NOT NULL,
  elasticity_modulus DOUBLE NOT NULL,
  material VARCHAR(50) NOT NULL,
  initial_pressure DOUBLE NOT NULL,
  initial_temperature DOUBLE NOT NULL,
  flow_rate DOUBLE NOT NULL,
  fluid_viscosity DOUBLE NOT NULL,
  corrosion_rate DOUBLE NOT NULL,
  service_life INT NOT NULL,
  current_age INT NOT NULL,
  time_step INT NOT NULL,
  simulation_speed INT NOT NULL,
  real_time BOOLEAN NOT NULL,
  current_pressure DOUBLE NOT NULL,
  current_temperature DOUBLE NOT NULL,
  health_status DOUBLE NOT NULL,
  fault_status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建模拟历史表
CREATE TABLE IF NOT EXISTS simulation_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  timestamp DATETIME NOT NULL,
  pressure DOUBLE NOT NULL,
  temperature DOUBLE NOT NULL,
  flow_rate DOUBLE NOT NULL,
  health DOUBLE NOT NULL,
  fault_status VARCHAR(50) NOT NULL,
  current_age INT NOT NULL,
  corrosion_rate DOUBLE NOT NULL,
  service_life INT NOT NULL,
  length DOUBLE NOT NULL,
  diameter DOUBLE NOT NULL,
  thickness DOUBLE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);