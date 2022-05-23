package com.atiguigu.yygh.order.service;

import com.atiguigu.yygh.model.order.OrderInfo;
import com.atiguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    // 2.保存交易记录 想支付记录表添加信息
    void savePaymentInfo(OrderInfo order, Integer status);

    //更改订单状态，处理支付结果
    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

}
