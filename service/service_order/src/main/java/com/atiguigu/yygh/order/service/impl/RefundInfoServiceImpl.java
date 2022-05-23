package com.atiguigu.yygh.order.service.impl;

import com.atiguigu.yygh.enums.RefundStatusEnum;
import com.atiguigu.yygh.model.order.PaymentInfo;
import com.atiguigu.yygh.model.order.RefundInfo;
import com.atiguigu.yygh.order.mapper.RefundInfoMapper;
import com.atiguigu.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
    @Autowired
    private RefundInfoMapper refundInfoMapper;

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        //判断是否有重复数据添加
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", paymentInfo.getOrderId());
        queryWrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = refundInfoMapper.selectOne(queryWrapper);
        if(null != refundInfo) return refundInfo;//说明有相同的数据
        // 保存交易记录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());//当前时间
        refundInfo.setOrderId(paymentInfo.getOrderId());//订单id
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        //paymentInfo.setSubject("test");
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfoMapper.insert(refundInfo);
        return refundInfo;
    }


}
