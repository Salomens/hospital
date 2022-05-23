package com.atiguigu.yygh.task.scheduled;



import com.atiguigu.common.rabbit.constant.MqConst;
import com.atiguigu.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling

public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    //在每天的8点执行方法，就医提醒
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }

}
