package cn.edu.bupt.service;

import cn.edu.bupt.model.*;

final public class transactionAdder {
    // 单例实现
    private static final transactionAdder INSTANCE = new transactionAdder();
    private transactionAdder() {}
    public static transactionAdder getInstance() { return INSTANCE; }

    /*
        Transaction必有的tag：
        收入 / 支出
        三餐 / 零食 / 衣服 / 交通 / 旅行 / 孩子 / 宠物 / 烟酒 / 学习 / 日用品 / 住房 / 美妆 / 医疗 / 红包 / 汽车 / 娱乐 / 礼物 / 电子 / 运动 / 其他

    */ 

    // public Transaction create() {

    // }
}
