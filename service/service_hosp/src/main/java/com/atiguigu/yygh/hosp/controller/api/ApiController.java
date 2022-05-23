package com.atiguigu.yygh.hosp.controller.api;

import com.atiguigu.yygh.model.hosp.Department;
import com.atiguigu.yygh.model.hosp.Hospital;
import com.atiguigu.yygh.model.hosp.Schedule;
import com.atiguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atiguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.atiguigu.yygh.common.exception.YyghException;
import com.atiguigu.yygh.common.helper.HttpRequestHelper;
import com.atiguigu.yygh.common.result.Result;
import com.atiguigu.yygh.common.result.ResultCodeEnum;
import com.atiguigu.yygh.common.utils.MD5;
import com.atiguigu.yygh.hosp.service.DepartmentService;
import com.atiguigu.yygh.hosp.service.HospitalService;
import com.atiguigu.yygh.hosp.service.HospitalSetService;
import com.atiguigu.yygh.hosp.service.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
//@CrossOrigin
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //删除排班接口
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String)paramMap.get("hoscode");
        //必填
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();

    }
    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);


        //获取医院的编号
        String hoscode = (String) paramMap.get("hoscode");
        //科室编号
        String depcode = (String) paramMap.get("depcode");
        //当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page"))? 1: Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))? 1: Integer.parseInt((String) paramMap.get("limit"));

        //签名校验
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    //上传排班的接口
    @PostMapping("saveSchedule")
    public  Result saveSchedule(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //签名校验
        //必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);
        return  Result.ok();
    }


    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //医院编号与科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院的编号
        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isEmpty(paramMap.get("page"))? 1: Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))? 1: Integer.parseInt((String) paramMap.get("limit"));

        //签名校验
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    //医院科室上传
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取医院传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名 ;签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");

        //2 根据传递过来医院编号，查询数据库，查询签名 j
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3 把查询的数据进行MD5的进行加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //4 判断签名是否一致 如果不一致
        if(!hospSign.equals(signKeyMD5)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service中的方法
        departmentService.save(paramMap);
        return  Result.ok();

    }

    //查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取医院传递来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String)paramMap.get("hoscode");

        //1获取医院系统传递过来的签名 ;签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");

        //2 根据传递过来医院编号，查询数据库，查询签名 j
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3 把查询的数据进行MD5的进行加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //4 判断签名是否一致
        if(!hospSign.equals(signKeyMD5)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法实现根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        System.out.println("=================");
        System.out.println(hospital);
        System.out.println("--------------------");
        return Result.ok(hospital);
    }

    //上传医院的接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取医院传递来的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名 ;签名进行MD5加密
        String hospSign = (String)paramMap.get("sign");
        
        //2 根据传递过来医院编号，查询数据库，查询签名 j
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        
        //3 把查询的数据进行MD5的进行加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //4 判断签名是否一致
        if(!hospSign.equals(signKeyMD5)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData =(String)paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData",logoData);
        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }

}
