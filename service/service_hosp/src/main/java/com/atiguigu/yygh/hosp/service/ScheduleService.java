package com.atiguigu.yygh.hosp.service;

import com.atiguigu.yygh.model.hosp.Schedule;
import com.atiguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atiguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    //查询排班接口
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //删除排班接口
    void remove(String hoscode, String hosScheduleId);

    //根据医院编号和科室编号，查询排班情况
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    //根据医院编号、科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获取可预约排班数据  hoscode医院编号  depcode部门编号
    Map<String,Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    //根据排班id获取排班数据
    Schedule getScheduleId(String id);

    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //下单成功更新预约数
    void update(Schedule schedule);
}
