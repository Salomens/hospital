package com.atiguigu.yygh.hosp.controller;


import com.atiguigu.yygh.model.hosp.Hospital;
import com.atiguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atiguigu.yygh.common.result.Result;
import com.atiguigu.yygh.hosp.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "医院查询")
@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin //跨域
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    //医院列表
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);//数据存在mongodb中
        List<Hospital> content = pageModel.getContent();
        long totalElements = pageModel.getTotalElements();
        return Result.ok(pageModel);
    }
    //更新一样上线状态
    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    //显示医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public  Result showHospDetail(@PathVariable String id){
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }



}
