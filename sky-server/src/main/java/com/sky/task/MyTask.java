package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/* 定时任务类 */
@Component
@Slf4j
public class MyTask {

    // @Scheduled(cron = "0 * * * * ?")
    // public  void  executeTask(){
    //     log.info("定时任务开始执行");
    // }
}
