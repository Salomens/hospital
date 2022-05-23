package com.atiguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atiguigu.yygh.model.hosp.Hospital;
import com.atiguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atiguigu.yygh.cmn.client.DictFeignClient;
import com.atiguigu.yygh.hosp.repository.HospitalRepository;
import com.atiguigu.yygh.hosp.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        //先把参数map集合转换为对象Hospital
        //医院那边是json数据，这边是mysql，需要转换格式
        String mapString = JSONObject.toJSONString(paramMap);//把map集合变成字符串
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);//把字符串在变成对象

        //先判断是否存在相同的数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);//mongo会自动实现，根据名字

        //如果不存在，进行添加
        if(hospitalExist!=null){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{//如果存储，进行修改
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital= hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    //医院列表（条件查询分页）
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建Pageable对象
        Pageable pageable = PageRequest.of(page-1,limit);

        //创建条件匹配器
        ExampleMatcher matcher  = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //hospitalQuery转换Hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital,matcher);

        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        pages.getContent().stream().forEach(item->{
            this.setHospitalHosType(item);
        });
        return pages;
    }


    //获取查询list集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital){
/*        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省 、市 地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;*/
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省 市  地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }

    //更新一样上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    //显示医院详情信息
    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> result = new HashMap<>();
        //Hospital hospital = hospitalRepository.findById(id).get();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());

        result.put("hospital",hospital);
        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);

        return result;
    }

    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospitalByHoscode!=null){
            return hospitalByHoscode.getHosname();
        }
        return null;
    }

    //根据医院名称查询


    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    //根据医院编号获取医院预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;

    }
}
