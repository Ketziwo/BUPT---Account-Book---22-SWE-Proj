# 0 前言 - 如何部署本项目？

## 1 安装maven

- Apache Maven 是一个软件项目管理和综合工具。基于项目对象模型 (POM) 的概念，Maven 可以从一个中心信息源管理项目的构建、报告和文档。  

- maven官网下载链接：https://maven.apache.org/download.cgi  

具体流程可见：【一小时Maven教程】 https://www.bilibili.com/video/BV1uApMeWErY/?p=3&share_source=copy_web&vd_source=9fdf279c769c4839dbb45b8625a6f4cf

## 2 部署git
- 安装 git，按照教程走，在git bash里登录账号和邮箱
- 找个空文件夹执行命令： git clone https://github.com/Ketziwo/BUPT---Account-Book---22-SWE-Proj 
- 用IDE打开运行

# 1 项目结构

工程目录设计，采用分层架构便于维护和扩展：

```
BUPT---Account-Book---22-SWE-Proj/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cn/edu/bupt/
│   │   │       ├── model/             # 数据模型
│   │   │       │   └── Transaction.java
│   │   │       │   └── Tag.java
│   │   │       │
│   │   │       ├── dao/               # 数据存取 （Data Access Object，数据访问对象）
│   │   │       │   ├── TransactionDao.java
│   │   │       │   └── CsvTransactionDao.java
│   │   │       │
│   │   │       ├── service/           # 业务逻辑
│   │   │       │   ├── TransactionService.java
│   │   │       │   └── AnalysisService.java
│   │   │       │
│   │   │       ├── utils/             # 工具类
│   │   │       │   ├── DateUtils.java
│   │   │       │   ├── CsvParser.java
│   │   │       │   └── WeChatParser.java
│   │   │       │
│   │   │       ├── view/              # 界面组件
│   │   │       │   ├── components/
│   │   │       │   │   ├── ChartPanel.java
│   │   │       │   │   ├── TablePanel.java
│   │   │       │   │   └── InputDialog.java
│   │   │       │   └── MainFrame.java
│   │   │       │
│   │   │       ├── config/            # 配置管理
│   │   │       │   └── AppConfig.java
│   │   │       │
│   │   │       └── Main.java          # 程序入口
│   │   │
│   │   └── resources/            # 资源文件
│   │       ├── categories.json   # 分类配置文件
│   │       └── icons/            # 图标资源
│   │
│   └── test/                    # 单元测试
│       └── java/
│           └── cn/edu/bupt/
│                          └── test/
├── data/                       # 数据存储目录
│   └── transactions.csv
└── pom.xml                    # Maven依赖配置
```

核心文件说明：

1. **模型层（model）**
- `Transaction.java`：交易实体类
  ```java
  public class Transaction {
      private LocalDateTime date;
      private BigDecimal amount;
      private String description;
      private TransactionType type; // 枚举：INCOME/EXPENSE
      private String category;      // 分类：餐饮、娱乐等
      // 构造方法/getter/setter
  }
  ```

2. **数据访问层（dao）**
- `TransactionDao.java`（接口）：
  ```java
  public interface TransactionDao {
      List<Transaction> loadTransactions();
      void saveTransactions(List<Transaction> transactions);
  }
  ```
- `CsvTransactionDao.java`：实现CSV文件读写

3. **业务逻辑层（service）**
- `TransactionService.java`：处理交易的新增、删除、修改、查询
- `AnalysisService.java`：数据分析逻辑
  ```java
  public class AnalysisService {
      public Map<String, BigDecimal> getCategorySummary() { ... }
      public String generateSummaryReport() { ... }
  }
  ```

4. **视图层（view）**
- `MainFrame.java`：主窗口（继承JFrame）
- `TablePanel.java`：带JTable的交易列表
- `ChartPanel.java`：使用JFreeChart实现的图表容器
- `InputDialog.java`：交易输入弹窗

5. **工具类（utils）**
- `WeChatParser.java`：微信CSV专用解析器
- `DateUtils.java`：日期格式转换工具
- `CsvParser.java`：通用CSV解析工具

6. **配置管理**
- `AppConfig.java`：管理分类映射等配置
- `categories.json`：分类配置（示例）：
  ```json
  {
    "餐饮": ["美团", "饿了么"],
    "交通": ["滴滴出行", "地铁"]
  }
  ```

技术选型建议：
1. GUI组件：使用SwingX扩展组件提升表格体验
2. 图表库：集成JFreeChart（需在pom.xml添加依赖）
3. 日期处理：Java 8 Time API
4. CSV解析：Apache Commons CSV

实现顺序建议：
1. 先完成CSV读写核心逻辑
2. 开发基础交易管理功能
3. 实现基本GUI框架
4. 添加数据分析与图表功能
5. 最后进行界面美化与交互优化

扩展性考虑：
1. 使用接口隔离各层实现
2. 配置驱动分类映射
3. 预留多种数据源接入能力
4. 图表模块采用策略模式便于扩展新图表类型

测试重点：
1. CSV文件不同编码的读写测试
2. 金额计算精度测试（使用BigDecimal）
3. 微信支付CSV格式兼容性测试
4. 大数据量（10万+条）性能测试

# 2 数据存储方案

### 一、CSV存储模式设计
建议在`data/transactions.csv`文件中按以下字段结构存储：

```csv
transaction_id,datetime,amount,currency,transaction_type,category,source,description,tags,created_at,modified_at
20230901-001,2023-09-01T12:30:00,128.50,CNY,EXPENSE,餐饮,微信支付,星巴克下午茶,咖啡;工作餐,2023-09-01T13:00:00,2023-09-01T13:00:00
20230902-002,2023-09-02T19:45:00,5000.00,CNY,INCOME,工资,银行转账,九月工资,,2023-09-02T20:00:00,2023-09-02T20:00:00
```

### 二、字段说明
| 字段名          | 格式要求                          | 说明                                 |
|-----------------|----------------------------------|--------------------------------------|
| transaction_id  | 年月日-序号（如20230901-001）    | 唯一标识，可通过日期+序列号生成       |
| datetime        | ISO 8601格式（yyyy-MM-ddTHH:mm:ss） | 精确到分钟的交易时间                 |
| amount          | 数值型字符串（避免浮点精度问题）   | 使用BigDecimal处理的金额             |
| currency        | 3字母货币代码（CNY/USD等）        | 默认CNY，支持扩展                    |
| transaction_type| INCOME/EXPENSE                   | 收入或支出类型                       |
| category        | 预定义分类（餐饮、交通等）         | 来自categories.json的配置            |
| source          | 交易渠道（微信支付/现金/银行卡等） | 用户可自定义                         |
| description     | 自由文本（建议限制100字符）        | 交易详情描述                         |
| tags            | 分号分隔的标签（咖啡;工作餐）      | 用户自定义标签系统                   |
| created_at      | ISO 8601格式                     | 记录创建时间                         |
| modified_at     | ISO 8601格式                     | 最后修改时间（初始值同created_at）   |

### 三、设计原则
1. **兼容性原则**
   - 保留微信支付原始字段（如微信的交易单号、商户单号等）作为隐藏列
   - 在`WeChatParser.java`中处理原始CSV到标准格式的转换
   ```csv
   # 微信原始字段（示例）
   wechat_transaction_id,wechat_merchant_id,...
   ***************,***************,...
   ```

2. **扩展性原则**
   - 预留5个`custom_field_{1-5}`字段供用户扩展
   - 使用末尾列添加策略（新字段始终添加到CSV末尾）

3. **数据校验要求**
   - 金额字段必须符合`^\\d+(\\.\\d{1,2})?$`正则表达式
   - 分类字段必须存在于`categories.json`配置中
   - 时间字段必须能通过`DateTimeFormatter.ISO_LOCAL_DATE_TIME`解析

### 四、关联数据存储
1. **分类配置**（resources/categories.json）
```json
{
  "支出分类": {
    "餐饮": ["外卖", "餐厅", "食材采购"],
    "交通": ["出租车", "地铁", "加油"],
    "娱乐": ["电影", "游戏", "旅行"]
  },
  "收入分类": {
    "工资": ["月薪", "奖金"],
    "投资": ["股票", "基金"],
    "其他": ["红包", "退款"]
  }
}
```

2. **用户偏好**（data/preferences.properties）
```properties
# 存储用户最近使用的分类
default.currency=CNY
recent.categories=餐饮,交通,娱乐
last.used.dir=/Documents/WeChatBills
```

### 五、数据版本控制
1. 在CSV首行添加版本标识：
```csv
# FinanceApp Data v1.2
```

2. 变更日志文件（data/changelog.csv）
```csv
timestamp,operation,user,affected_ids
2023-09-01T14:00:00,IMPORT,system,20230901-001;20230901-002
2023-09-01T15:30:00,UPDATE,admin,20230901-001
```

### 六、特殊场景处理
1. **多文件存储方案**
   ```bash
   data/
   ├── transactions/         # 按年分目录
   │   ├── 2023/
   │   │   ├── 09.csv       # 按月分文件
   │   │   └── 10.csv
   │   └── 2024/
   ├── archives/            # 压缩归档文件
   │   └── 2023-Q3.zip
   └── templates/           # CSV模板
       └── wechat-template.csv
   ```

2. **大数据量优化**
   - 采用流式CSV解析（使用Apache Commons CSV的CSVParser）
   - 实现分页加载机制（每次加载100条）
   - 建立内存索引（使用TransactionID的HashMap）

3. **数据恢复机制**
   - 每次保存时创建备份文件（transactions.csv.bak）
   - 每天自动生成增量备份（按时间戳命名）
   - 实现CSV文件完整性校验（MD5校验和）

### 七、安全考虑
1. 敏感字段加密（如金额字段使用AES加密）
2. 文件访问锁机制（使用FileLock防止并发写入）
3. 变更审计跟踪（通过changelog.csv记录所有修改）

这种设计方案可以：
1. 保持CSV的人类可读性
2. 支持复杂数据分析需求
3. 兼容原始微信支付数据
4. 提供基本的数据安全保护
5. 方便未来扩展数据库存储（保持字段对应关系）

建议在`CsvTransactionDao`中实现以下核心方法：
```java
public class CsvTransactionDao implements TransactionDao {
    // 带缓存的读取实现
    public List<Transaction> loadTransactions() {
        // 1. 检查文件MD5校验和
        // 2. 使用内存映射文件加速读取
        // 3. 解析时自动合并多文件
    }
    
    // 原子性写入实现
    public void saveTransactions(List<Transaction> transactions) {
        // 1. 创建临时文件写入
        // 2. 计算新文件的MD5
        // 3. 原子替换原文件
        // 4. 更新备份文件
    }
}
```