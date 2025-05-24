package cn.edu.bupt.dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;

/**
 * Client for interacting with the DeepSeek AI API.
 * This class provides methods to send prompts to the DeepSeek language model
 * and process the responses. It is used for AI-assisted features such as
 * budget recommendations and transaction categorization.
 * 
 * The client handles all HTTP communication with the DeepSeek API,
 * including authentication, request formatting, and response parsing.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public class DeepSeekClient {    /**
     * The base URL for the DeepSeek API.
     */
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    
    /**
     * The API key for authenticating with the DeepSeek service.
     */
    private static final String API_KEY = "sk-bba8b78128c64ee89b871bf584a14ef6";// 内部类定义请求/响应结构
    /**
     * Represents a message in the chat API format.
     * Each message has a role (system, user, or assistant) and content.
     */
    static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }    /**
     * Represents the request data structure for the DeepSeek Chat API.
     * This class encapsulates all parameters needed for making a request,
     * including the model name, messages, temperature, and token limit.
     */
    static class ChatRequest {
        private String model;
        private List<Message> messages;
        private double temperature;
        private int max_tokens;

        public ChatRequest(String model, List<Message> messages, double temperature, int max_tokens) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
            this.max_tokens = max_tokens;
        }
    }    /**
     * Represents the response from the DeepSeek Chat API.
     * This class parses the JSON response into a structured object
     * containing the generated messages.
     */
    static class ChatResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        /**
         * Represents a single response choice from the API.
         * Each choice contains a message with the generated content.
         */
        static class Choice {
            private Message message;

            public Message getMessage() {
                return message;
            }
        }
    }    /**
     * Sends a request to the DeepSeek API and processes the response.
     * This method handles the HTTP communication, JSON serialization/deserialization,
     * and error handling for API requests.
     * 
     * @param requestBody The request data to send to the API
     * @return The generated text response, or an error message if the request fails
     */
    private static String sendRequest(ChatRequest requestBody) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        //将 ChatRequest 对象中封装的数据转为 JSON 格式
        String requestBodyJson = gson.toJson(requestBody);

//        System.out.println("请求体：");
//        System.out.println(requestBodyJson);

        try {
            //构建请求对象 并指定请求头内容格式及身份验证的key
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    //将JSON格式的字符串封装为 BodyPublishers 对象
                    .POST(BodyPublishers.ofString(requestBodyJson))
                    .build();  //构建请求对象

            // System.out.println(">>>已提交问题，正在思考中....");
            // 发送请求并获取响应对象
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //如果响应状态码为成功 200
            if (response.statusCode() == 200) {
            //    System.out.println("响应体：");
            //    System.out.println(response.body());
                // 解析响应 把响应体中的json字符串转为 ChatResponse 对象
                ChatResponse chatResponse = gson.fromJson(response.body(), ChatResponse.class);
                //按 JSON 格式的方式 从自定义的ChatResponse 对象中逐级取出最终的响应对象
                return chatResponse.getChoices().get(0).getMessage().getContent();
            } else {
                return "请求失败，状态码: " + response.statusCode() + ", 响应: " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "请求异常: " + e.getMessage();
        }
    }
      /**
     * Sends a prompt to the DeepSeek AI and returns the generated response.
     * This is the main method used by the application to interact with the AI service.
     * 
     * @param content The prompt or question to send to the AI
     * @param max_tokens The maximum number of tokens (words) to generate in the response
     * @return The AI-generated text response
     */
    public static String getAnswer(String content, int max_tokens) {
        // 创建消息列表
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", content));

        // 构建请求体
        ChatRequest requestBody = new ChatRequest(
                "deepseek-chat",  // 模型名称，根据文档调整
                messages,
                0.7,  // temperature
                max_tokens  // max_tokens
        );
        long startTime = System.currentTimeMillis();
        
        // 发送请求
        String response = sendRequest(requestBody);
        long endTime = System.currentTimeMillis();
        System.out.println("思考用时："+(endTime-startTime)/1000+"秒");
        return response;
    }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     String input = "";
    //     System.out.println("*** 我是 DeepSeek ，很高兴见到您 ****");
    //     while(true){
    //         System.out.println("---请说您问题：---");
    //         String question = scanner.next();
    //         if("bye".equals(question)){
    //             break;
    //         }
    //         ask(question);
    //         System.out.println();
    //     }
    //     System.out.println("拜拜，欢迎下次使用！");
    // }
    public static String WechatTransactionsReaderAIprompt = "​​​一、核心任务​​\r\n" + //
                "将用户提供的微信支付账单CSV文件，​​严格按以下规则转换为结构化交易数据​​，输出包含完整标签分类的标准CSV文件。不要回答除了CSV文件意外的任何内容。\r\n" + //
                "\r\n" + //
                "​​二、数据处理规则​​\r\n" + //
                "1. **ID生成**  \r\n" + //
                "   - 格式：`yyyy-MM-dd-数字x`（如2025-05-22-1）  \r\n" + //
                "   - 规则：同一天内按交易时间顺序编号（从1开始）\r\n" + //
                "\r\n" + //
                "2. **金额处理**  \r\n" + //
                "   - 转换：人民币元 → 分（`¥36.80 → 3680`）  \r\n" + //
                "   - 符号：无论支出还是收入均为正号\r\n" + //
                "\r\n" + //
                "3. **时间字段**  \r\n" + //
                "   - `datetime`：原始交易时间（精确到秒）  \r\n" + //
                "   - `created_at/modified_at`：固定为`2025-05-22 23:35:07`\r\n" + //
                "\r\n" + //
                "4. **描述优化**  \r\n" + //
                "   - 删除冗余：订单号、平台代码（如`-美团App-25052211100300001308766305053512`）  \r\n" + //
                "   - 翻译外文：商户名中非中文内容需翻译并标注原文（如`ドンキ秋葉原 → 唐吉诃德（东京秋叶原店）`)  \r\n" + //
                "   - 语义合并：提取核心信息（如`普通充值 → 课程充值`）\r\n" + //
                "\r\n" + //
                "5. **标签分类**  \r\n" + //
                "   - **第一标签**：`__EXPENSE__`（支出）或`__INCOME__`（收入）  \r\n" + //
                "   - **第二标签**：根据以下规则匹配（优先级从高到低）：  \r\n" + //
                "     （表格见下方《全量分类标签规则表》）\r\n" + //
                "​​三、全量分类标签规则表​​\r\n" + //
                "​​标签名称​​\t​​判断原则​​\t​​示例关键词/场景​​\r\n" + //
                "​​__EXPENSE_CAR__​​\t汽车相关消费\t加油、维修、保险、停车费\r\n" + //
                "​​__EXPENSE_CHILD__​​\t子女相关支出\t学费、奶粉、玩具、儿童医疗\r\n" + //
                "​​__EXPENSE_CLOTH__​​\t服装鞋帽购买\t优衣库、ZARA、裁缝服务\r\n" + //
                "​​__EXPENSE_DEVICE__​​\t电子设备购买/维修\t手机、电脑、相机、配件\r\n" + //
                "​​__EXPENSE_ENTERTAINMENT__​​\t娱乐消费\t电影、KTV、游戏充值、演出门票\r\n" + //
                "​​__EXPENSE_FOOD__​​\t餐饮相关支出\t外卖、餐厅​​_\r\n" + //
                "   EXPENSE_GIFT__​​\t礼物赠送\t节日礼品、红包（非收入）\r\n" + //
                "​​__EXPENSE_HOUSING__​​\t住房相关支出\t房租、房贷、物业费、水电燃气\r\n" + //
                "​​__EXPENSE_INTERNET__​​\t网络通信费用\t购买会员、话费充值、宽带费、VPN服务\r\n" + //
                "​​__EXPENSE_MAKEUP__​​\t化妆品/护肤品消费\t口红、精华液、美容院服务\r\n" + //
                "​​__EXPENSE_MEDICAL__​​\t医疗健康支出（紧急优先级）\t医院、药品、体检、牙科\r\n" + //
                "​​__EXPENSE_NECESSARY__​​\t日用品采购\t卫生纸、清洁剂、家庭消耗品\r\n" + //
                "​​__EXPENSE_PET__​​\t宠物相关消费\t宠物食品、医疗、寄养、用品\r\n" + //
                "​​__EXPENSE_SNACK__​​\t零食饮料购买\t水果、咖啡、奶茶、甜品、自动售货机\r\n" + //
                "​​__EXPENSE_SPORT__​​\t运动健身支出\t健身房、运动装备、赛事报名\r\n" + //
                "​​__EXPENSE_STUDY__​​\t教育学习支出\t课程、书籍、考试费、在线教育\r\n" + //
                "​​__EXPENSE_TABACCO_ALCOHOL__​​\t烟酒消费\t香烟、白酒、酒吧消费\r\n" + //
                "​​__EXPENSE_TRANSPORT__​​\t交通出行费用\t地铁、打车、共享单车、机票\r\n" + //
                "​​__EXPENSE_TRAVEL__​​\t旅行相关支出\t酒店、景点门票、旅行团、签证费\r\n" + //
                "​​__EXPENSE_OTHERS__​​\t无法归类到上述任何类别的支出\t未知商户、无法识别的交易\r\n" + //
                "​​__INCOME_HONGBAO__​​\t红包/转账收入\t微信红包、转账备注含\"红包\"\r\n" + //
                "​​__INCOME_SALARY__​​\t工资/劳务收入\t工资到账、奖金发放\r\n" + //
                "​​__INCOME_STOCK__​​\t投资理财收入\t股票分红、基金赎回、利息收入\r\n" + //
                "​​__INCOME_OTHERS__​​\t其他收入\t退款、二手交易收入、意外收入\r\n" + //
                "​​四、冲突解决与优先级​​\r\n" + //
                "1. **多标签冲突**：  \r\n" + //
                "   - 按紧急性和消费频率排序：  \r\n" + //
                "     `医疗 > 住房 > 教育/旅行 > 日常必要支出 > 其他`  \r\n" + //
                "   - 示例：`药店购买维生素` → 优先归为__EXPENSE_MEDICAL__而非__EXPENSE_NECESSARY__\r\n" + //
                "\r\n" + //
                "2. **复合场景**：  \r\n" + //
                "   - 主场景优先：`宠物主题餐厅消费` → 归为__EXPENSE_FOOD__（餐饮为主）\r\n" + //
                "\r\n" + //
                "3. **连锁品牌库**：  \r\n" + //
                "   - 预设商户-标签映射（如`链家`→__EXPENSE_HOUSING__，`和睦家`→__EXPENSE_MEDICAL__）\r\n" + //
                "​​五、输入输出示例​​\r\n" + //
                "​​原始数据​​\r\n" + //
                "\r\n" + //
                "交易时间, 交易类型, 交易对方, 商品, 收/支, 金额(元)  \r\n" + //
                "2025-06-01 10:30:00, 商户消费, 宠物医院, 狗狗疫苗注射, 支出, ¥200.00  \r\n" + //
                "2025-06-02 15:00:00, 转账, 公司财务, 2025年6月工资, 收入, ¥15000.00  \r\n" + //
                "2025-06-03 19:45:00, 商户消费, ドンキ秋葉原, Nintendo Switch游戏卡带, 支出, ¥399.00  \r\n" + //
                "​​AI处理后输出​​\r\n" + //
                "\r\n" + //
                "transaction_id,amount,datetime,created_at,modified_at,description,tags  \r\n" + //
                "2025-06-01-1,20000,2025-06-01 10:30:00,2025-05-22 23:35:07,2025-05-22 23:35:07,\"宠物医院-狗狗疫苗注射\",__EXPENSE__|__EXPENSE_PET__  \r\n" + //
                "2025-06-02-1,1500000,2025-06-02 15:00:00,2025-05-22 23:35:07,2025-05-22 23:35:07,\"工资收入：公司财务\",__INCOME__|__INCOME_SALARY__  \r\n" + //
                "2025-06-03-1,39900,2025-06-03 19:45:00,2025-05-22 23:35:07,2025-05-22 23:35:07,\"唐吉诃德（ドンキ秋葉原）-游戏卡带购买\",__EXPENSE__|__EXPENSE_ENTERTAINMENT__  \r\n" + //
                "​​六、错误处理​​\r\n" + //
                "1. **数据不完整**：  \r\n" + //
                "   - 关键字段（如金额、时间）缺失时丢弃该记录  \r\n" + //
                "   \r\n" + //
                "2. **金额异常**：  \r\n" + //
                "   - 非数字金额（如`¥-`）标记为`[无效数据]`并跳过  \r\n" + //
                "\r\n" + //
                "3. **未知外文**：  \r\n" + //
                "   - 无法翻译时保留原文并标记为`__EXPENSE_OTHERS__`";
    public static String BudgetAdvisorAIprompt = "生成2025预算，严格遵循以下规则：  \r\n" + //
                "\r\n" + //
                "基于Transaction.csv消费记录分析：  \r\n" + //
                "1. 按月份统计各标签（tags）支出占比  \r\n" + //
                "2. 识别节假日前后消费波动规律（春节/劳动节/跨年等）  \r\n" + //
                "  \r\n" + //
                "格式为csv，不要生成除了表格以外文字，应当生成表头，amount,starttime,endtime,description,tags\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "1. 字段定义  \r\n" + //
                "| 字段        | 规则  \r\n" + //
                "|-------------|---------------------------------------------------  \r\n" + //
                "| amount      | 整数金额（单位人民币分，如1500000）  \r\n" + //
                "| starttime   | ISO8601格式时间戳（精确到秒，如2024-09-01 00:00:00）  \r\n" + //
                "| endtime     | 必须≥starttime（跨日周期需到23:59:59）  \r\n" + //
                "| description | 含消费场景+时间特征（如\"双11服饰日用品预算\"）  \r\n" + //
                "| tags        | 标签组合（格式：__TAG1__|__TAG2__，规则见下文）  \r\n" + //
                "\r\n" + //
                "2. 标签体系  \r\n" + //
                "一级标签（不可组合使用）  \r\n" + //
                "__EXPENSE__    监控所有支出  \r\n" + //
                "__INCOME__     监控所有收入  \r\n" + //
                "\r\n" + //
                "二级标签（可多选组合，用|分隔）  \r\n" + //
                "• 支出类（必须与__EXPENSE__同级使用）：  \r\n" + //
                "__EXPENSE_CAR__          汽车费用  \r\n" + //
                "__EXPENSE_CHILD__        子女支出  \r\n" + //
                "__EXPENSE_CLOTH__        服装鞋帽  \r\n" + //
                "__EXPENSE_DEVICE__       电子设备  \r\n" + //
                "__EXPENSE_ENTERTAINMENT__娱乐消费  \r\n" + //
                "__EXPENSE_FOOD__         餐饮支出  \r\n" + //
                "__EXPENSE_GIFT__         节日赠礼  \r\n" + //
                "__EXPENSE_HOUSING__      房租物业  \r\n" + //
                "__EXPENSE_INTERNET__     网络费用  \r\n" + //
                "__EXPENSE_MAKEUP__       美妆护肤  \r\n" + //
                "__EXPENSE_MEDICAL__      医疗药品  \r\n" + //
                "__EXPENSE_NECESSARY__    日用品  \r\n" + //
                "__EXPENSE_PET__          宠物支出  \r\n" + //
                "__EXPENSE_SNACK__        零食饮料  \r\n" + //
                "__EXPENSE_SPORT__        健身运动  \r\n" + //
                "__EXPENSE_STUDY__        学习培训  \r\n" + //
                "__EXPENSE_TABACCO_ALCOHOL__烟酒  \r\n" + //
                "__EXPENSE_TRANSPORT__    公共交通  \r\n" + //
                "__EXPENSE_TRAVEL__       旅行费用  \r\n" + //
                "__EXPENSE_OTHERS__       其他支出  \r\n" + //
                "\r\n" + //
                "• 收入类（必须与__INCOME__同级使用）：  \r\n" + //
                "__INCOME_HONGBAO__       红包收入  \r\n" + //
                "__INCOME_SALARY__        工资奖金  \r\n" + //
                "__INCOME_STOCK__         投资收益  \r\n" + //
                "__INCOME_OTHERS__        其他收入  \r\n" + //
                "\r\n" + //
                "3. 生成规则  \r\n" + //
                "• 时间粒度：  \r\n" + //
                "\r\n" + //
                "  ▸ 短期（≤7天）：精确到小时（如双11当天08:00-24:00）  \r\n" + //
                "  ▸ 月度：自然月周期（如2024-10-01 00:00:00至2024-10-31 23:59:59）  \r\n" + //
                "  ▸ 季度：标注Q4（2024-10-01至2024-12-31）  \r\n" + //
                "\r\n" + //
                "• 标签逻辑：  \r\n" + //
                "\r\n" + //
                "  ▸ 使用__EXPENSE__时，不可添加二级标签（如错误示例：__EXPENSE__|__EXPENSE_FOOD__）  \r\n" + //
                "  ▸ 二级标签可以多个组合（如正确示例：__EXPENSE_CLOTH__|__EXPENSE_MAKEUP__）  \r\n" + //
                "\r\n" + //
                "4. 生成2025年新版预算表，要求包含：  \r\n" + //
                "   - 基础生活预算（餐饮/日用品）  \r\n" + //
                "   - 弹性娱乐预算（剧本杀/旅行）  \r\n" + //
                "   - 节假日专项预算  \r\n" + //
                "   - 应急准备金  \r\n" + //
                "   - budget时间范围应该既有前半年的也有后半年的，既有短期也有中场期的\r\n" + //
                "\r\n" + //
                "示例数据：  \r\n" + //
                "6800,2024-09-15 00:00:00,2024-09-30 23:59:59,中秋礼品+聚餐预算,__EXPENSE_GIFT__|__EXPENSE_FOOD__  \r\n" + //
                "42000,2024-10-01 00:00:00,2024-10-07 23:59:59,国庆旅行专项（交通+住宿）,__EXPENSE_TRANSPORT__|__EXPENSE_TRAVEL__";
}