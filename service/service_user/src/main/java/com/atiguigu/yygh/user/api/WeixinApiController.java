package com.atiguigu.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.atiguigu.yygh.common.helper.JwtHelper;
import com.atiguigu.yygh.common.result.Result;
import com.atiguigu.yygh.model.user.UserInfo;
import com.atiguigu.yygh.user.service.UserInfoService;
import com.atiguigu.yygh.user.utils.ConstantWxPropertiesUtils;
//import com.atiguigu.yygh.user.utils.HttpClientUtils;

import com.atiguigu.yygh.user.utils.HttpClientUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
//微信操作接口
@Slf4j
@Controller //不返回数据，可以做页面跳转
@RequestMapping("/api/ucenter/wx")

public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //2 微信扫描后回调的方法
    @GetMapping("callback")
    public String callback(String code,String state){
        //第一步 获取零时票据code
        System.out.println("code"+code);
        //第二步 拿着code和微信id和密钥，请求微信固定地址，得到两个值
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        String accesstokenInfo = null;
        try {
            accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo:"+accesstokenInfo);

            JSONObject resultJson = JSONObject.parseObject(accesstokenInfo);
            String access_token = resultJson.getString("access_token");
            String openid = resultJson.getString("openid");
            System.out.println("access_token:"+access_token);
            System.out.println("openid:"+openid);

            //判断数据库是否存在微信的扫码人的信息
            //根据openid判断
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if(userInfo==null){//说明数据里里不存在该用户
                //第三步 拿着openid和access_token请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo:"+resultUserInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);

                //解析用户信息
                //用户昵称
                String nickname = resultUserInfoJson.getString("nickname");
                System.out.println("nickname:"+nickname);
                //用户头像
                String headimgurl = resultUserInfoJson.getString("headimgurl");
                System.out.println("headimgurl:"+headimgurl);

                //将扫描人的信息加入到数据库
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
                System.out.println("没出错！！！！！！！！！！！！！！！");
            }


            //返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }

            map.put("name", name);
            System.out.println("map:"+map);
            //判断userInfo是否有手机号，如果手机号为空，则返回openid
            //如果手机号不为空，返回openid值是空字符串
            //前端判断:如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }


            //使用jwt生成一个token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转前端页面
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL +
                    "/weixin/callback?token="+map.get("token")+"&openid="+
                    map.get("openid")+"&name="+URLEncoder.encode(URLEncoder.encode((String) map.get("name"),"utf-8")); //有可能会报错

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //1 生成微信扫描二维码
    //返回生成二维码需要参数
    @GetMapping("getLoginParam")
    @ResponseBody  //威信登陆 进入方法
    @ApiOperation(value = "威信登陆")
    public Result genQrConnect() {
//        System.out.println("威信登陆");
        try {
//            System.out.println("进入try了");
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            System.out.println("第一次map集合"+map);
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri",wxOpenRedirectUrl);
            map.put("state",System.currentTimeMillis()+"");
//            System.out.println("最总结给市"+map);
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 2 回调的方法，得到扫描人的身份信息
}
