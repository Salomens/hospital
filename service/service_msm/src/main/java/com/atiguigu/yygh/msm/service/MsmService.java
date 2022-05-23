package com.atiguigu.yygh.msm.service;

import com.atiguigu.yygh.vo.msm.MsmVo;

public interface MsmService {
    //调用service方法，通过整合短信服务进行发送
    boolean send(String phone, String code);

    //mq发送短信的封装
    boolean send(MsmVo msmVo);
}
