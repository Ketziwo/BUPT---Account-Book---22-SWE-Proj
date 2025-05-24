# 智能账单管理系统

## 项目概述
本系统是一个基于Java Swing开发的智能账单管理器，旨在帮助用户有效地管理个人财务，追踪收支、设置预算、可视化分析财务状况，并提供AI智能分析能力。系统支持多用户管理，数据持久化存储，并能通过图表直观地展示财务状况。

## 主要功能

### 1. 账单管理
- **交易记录**: 支持添加、修改、删除和查询交易记录
- **分类标签**: 使用标签系统对交易进行分类，包含丰富的预设标签（如餐饮、娱乐、交通等）
- **搜索和筛选**: 可根据时间、金额、标签等条件筛选交易记录

### 2. 预算管理
- **预算设置**: 可设置特定时间段和特定分类的预算金额
- **预算跟踪**: 实时监控预算使用情况，显示预算完成度
- **超支提醒**: 当接近或超出预算时提供视觉反馈

### 3. 数据可视化
- **收支趋势图**: 展示一段时间内的收支变化趋势
- **分类饼图**: 显示不同分类的支出比例
- **月度对比**: 比较不同月份的收支情况

### 4. 智能分析
- **微信账单导入**: 支持直接导入微信支付的CSV账单文件
- **AI智能分析**: 利用AI技术自动分类和分析交易记录
- **预算顾问**: 提供基于历史数据的智能预算建议

### 5. 用户管理
- **多用户支持**: 支持多个用户账号创建和切换
- **账户安全**: 提供简单的用户认证功能

## 技术架构

### 设计模式
- **MVC架构**: 项目采用MVC设计模式，清晰分离数据模型、视图和控制器
- **单例模式**: 使用单例模式管理全局资源和数据

### 核心组件
- **数据模型**:
  - `Transaction`: 交易记录模型
  - `Budget`: 预算模型
  - `Tag`: 标签模型
  - `User`: 用户模型
  - `TransactionManager`: 全局数据管理器

- **数据访问**:
  - `CsvTransactionDao`: CSV数据持久化实现
  - `DeepSeekClient`: AI服务接口

- **用户界面**:
  - `MainFrame`: 主窗口框架
  - `ManagerPanel`: 账单管理面板
  - `BudgetPanel`: 预算管理面板
  - `ChartsPanel`: 数据可视化面板
  - `SettingsPanel`: 设置与用户管理面板

### 技术栈
- **编程语言**: Java 17
- **UI框架**: Java Swing
- **构建工具**: Maven
- **图表库**: JFreeChart
- **数据处理**: Apache Commons CSV
- **JSON处理**: Google Gson

## 系统截图

*注：在此处可添加系统的各个功能截图*

## 安装与使用

### 系统要求
- JDK 17或更高版本
- Maven 3.6或更高版本

### 构建与运行
1. 克隆仓库
   ```bash
   git clone https://github.com/yourname/BUPT---Account-Book---22-SWE-Proj.git
   cd BUPT---Account-Book---22-SWE-Proj
   ```

2. 使用Maven构建项目
   ```bash
   mvn clean package
   ```

3. 运行应用
   ```bash
   java -jar target/BUPT---Account-Book---22-SWE-Proj-1.0-SNAPSHOT.jar
   ```

### 基本使用流程
1. 首次启动时会自动登录默认用户
2. 在设置面板可切换或创建新用户
3. 在交易管理面板添加和管理交易记录
4. 在预算面板设置和跟踪预算
5. 在图表面板查看财务状况可视化

## 项目结构
```
src/
  main/
    java/
      cn/
        edu/
          bupt/
            App.java                  # 应用程序入口
            dao/                      # 数据访问层
              CsvTransactionDao.java  # CSV数据操作
              DeepSeekClient.java     # AI服务接口
            model/                    # 数据模型
              Budget.java             # 预算模型
              Tag.java                # 标签模型
              Transaction.java        # 交易记录模型
              TransactionManager.java # 数据管理器
              User.java               # 用户模型
            utils/                    # 工具类
              DateUtils.java          # 日期工具
              TransactionTypeUtils.java # 交易类型工具
            view/                     # 视图层
              MainFrame.java          # 主窗口
              ManagerPanel.java       # 交易管理面板
              BudgetPanel.java        # 预算管理面板
              ChartsPanel.java        # 图表面板
              SettingsPanel.java      # 设置面板
              LeftPanel.java          # 左侧导航面板
  test/                               # 测试代码
data/                                 # 数据文件目录
resources/                            # 资源文件目录
```
