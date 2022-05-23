package com.atiguigu.yygh.hosp.service;

import com.atiguigu.yygh.model.hosp.HospitalSet;
import com.atiguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;


//Iservice是继承自mybatisplus
public interface HospitalSetService extends IService<HospitalSet> {

    //2 根据传递过来医院编号，查询数据库，查询签名
    String getSignKey(String hoscode);

    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
