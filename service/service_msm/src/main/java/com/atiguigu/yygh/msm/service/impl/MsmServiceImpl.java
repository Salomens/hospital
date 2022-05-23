package com.atiguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atiguigu.yygh.vo.msm.MsmVo;
import com.atiguigu.yygh.msm.utils.ConstantPropertiesUtils;
import com.atiguigu.yygh.msm.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {


    //调用service方法，通过整合短信服务进行发送
    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合阿里云短信服务
        //设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "医院预约挂号平台");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_180051135");
        //验证码  使用json格式   {"code":"123456"}
        Map<String,Object> param = new HashMap();
        param.put("code",code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    //mq发送短信的封装
    @Override
    public boolean send(MsmVo msmVo) {
        //手机号不为空才发送
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            boolean isSend  = this.send(msmVo.getPhone(),msmVo.getParam());
            return isSend;
        }
        return false;
    }

    private boolean send(String phone, Map<String, Object> param) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        try {
           /* Map<String, String> params = new HashMap<String, String>();
            params.put("apikey", ConstantPropertiesUtils.YP_API_KEY);
            String paramString = JSONObject.toJSONString(param);
            params.put("text", paramString);
            params.put("mobile", phone);
            post("https://sms.yunpian.com/v2/sms/single_send.json", params);*/
            System.out.println(param);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
