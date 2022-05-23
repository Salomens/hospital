package com.atiguigu.yygh.msm.controller;

import com.atiguigu.yygh.common.result.Result;
import com.atiguigu.yygh.msm.service.MsmService;
import com.atiguigu.yygh.msm.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")

public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码，如果获取获取到，返回ok
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果从redis获取不到，
        // 生成验证码，
        code = RandomUtil.getSixBitRandom();
//        System.out.println("密码是多少？");
//        System.out.println(code);
//        System.out.println("你好啊密码！");

        //调用service方法，通过整合短信服务进行发送
        //boolean isSend = msmService.send(phone,code);// 阿里云验证码无法使用
        //生成验证码放到redis里面，设置有效时间
        if(true) {
            //2分钟后失效
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            System.out.println("密码是多少？");
            System.out.println(code);
            System.out.println("你好啊密码！");
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }
}
