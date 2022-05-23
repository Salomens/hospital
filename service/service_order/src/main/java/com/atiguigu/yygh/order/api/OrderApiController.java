package com.atiguigu.yygh.order.api;

import com.atiguigu.yygh.enums.OrderStatusEnum;
import com.atiguigu.yygh.model.order.OrderInfo;
import com.atiguigu.yygh.vo.order.OrderCountQueryVo;
import com.atiguigu.yygh.vo.order.OrderQueryVo;
import com.atiguigu.yygh.common.result.Result;
import com.atiguigu.yygh.common.utils.AuthContextHolder;
import com.atiguigu.yygh.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    //生成挂号订单
    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrders(@PathVariable  String scheduleId,
                             @PathVariable Long patientId){
        Long orderId = orderService.saveOrder(scheduleId,patientId);
        return Result.ok(orderId);
    }
    //根据订单id查询订单详细
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo= orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }
    //订单列表（条件查询带分页）
    @ApiOperation("平台用户管理")
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                orderService.selectPage(pageParam,orderQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    //取消预约
    @ApiOperation(value = "取消预约")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        Boolean isOrder = orderService.cancelOrder(orderId);
        return  Result.ok(isOrder);
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }



}
