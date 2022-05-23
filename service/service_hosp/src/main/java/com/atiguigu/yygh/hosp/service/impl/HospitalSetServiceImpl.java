package com.atiguigu.yygh.hosp.service.impl;

import com.atiguigu.yygh.model.hosp.HospitalSet;
import com.atiguigu.yygh.vo.order.SignInfoVo;
import com.atiguigu.yygh.common.exception.YyghException;
import com.atiguigu.yygh.common.result.ResultCodeEnum;
import com.atiguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atiguigu.yygh.hosp.service.HospitalSetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    //2 根据传递过来医院编号，查询数据库，查询签名
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);

        return hospitalSet.getSignKey();
    }
//    @Autowired
//    private HospitalSetMapper hospitalSetMapper;
    //获取医院签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        System.out.println("进入了获取医院签名的方法");
        QueryWrapper<HospitalSet>wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(hospitalSet == null){
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        System.out.println("hspitalset:"+hospitalSet);
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());

        return signInfoVo;
    }
}
