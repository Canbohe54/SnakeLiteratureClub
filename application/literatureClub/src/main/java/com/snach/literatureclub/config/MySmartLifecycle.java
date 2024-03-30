package com.snach.literatureclub.config;

import com.snach.literatureclub.service.DBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MySmartLifecycle implements SmartLifecycle {

    private volatile boolean running = false;

    @Autowired
    private DBService dbService;

    /**
     * 如果该`Lifecycle`类所在的上下文在调用`refresh`时,希望能够自己自动进行回调，则返回`true`,
     * false的值表明组件打算通过显式的start()调用来启动，类似于普通的Lifecycle实现。
     */
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * SmartLifecycle子类的才有的方法，当isRunning方法返回true时，该方法才会被调用。
     * 很多框架中的源码中，都会把真正逻辑写在stop()方法内。
     * 比如quartz和Redis的spring支持包。
     */
    @Override
    public void stop(Runnable callback) {
        log.info("MySmartLifecycle容器停止，执行回调函数");
        stop();
        // 如果你让isRunning返回true，需要执行stop这个方法，那么就不要忘记调用callback.run()。
        // 否则在程序退出时，Spring的DefaultLifecycleProcessor会认为这个MySmartLifecycle没有stop完成，程序会一直卡着结束不了，等待一定时间（默认超时时间30秒）后才会自动结束。
        callback.run();
    }

    /**
     * 1. 主要在该方法中启动任务或者其他异步服务，比如开启MQ接收消息
     * 2. 当上下文被刷新（所有对象已被实例化和初始化之后）时，将调用该方法，
     * 默认生命周期处理器将检查每个SmartLifecycle对象的isAutoStartup()方法返回的布尔值。
     * 如果为“true”，则该方法会被调用，而不是等待显式调用自己的start()方法。
     */
    @Override
    public void start() {
        log.info("MySmartLifecycle容器启动完成 ...");
        running = true;
    }

    /**
     * 接口Lifecycle子类的方法，只有非SmartLifecycle的子类才会执行该方法。
     * 1. 该方法只对直接实现接口Lifecycle的类才起作用，对实现SmartLifecycle接口的类无效。
     * 2. 方法stop()和方法stop(Runnable callback)的区别只在于，后者是SmartLifecycle子类的专属。
     */
    @Override
    public void stop() {
        log.info("程序停止前执行redis持久化...");
        //将 Redis 里的点赞信息同步到数据库里
        dbService.transLikedFromRedis2DB();
        dbService.transLikedCountFromRedis2DB();
        dbService.transViewCountFromRedis2DB();
        running = false;
    }

    /**
     * 1. 只有该方法返回false时，start方法才会被执行。
     * 2. 只有该方法返回true时，stop(Runnable callback)或stop()方法才会被执行。
     */
    @Override
    public boolean isRunning() {
        log.info("MySmartLifecycle检查运行状态 ...");
        return running;
    }

    /**
     * 如果有多个实现接口SmartLifecycle的类，则这些类的start的执行顺序按getPhase方法返回值从小到大执行。
     * 例如：1比2先执行，-1比0先执行。stop方法的执行顺序则相反，getPhase返回值较大类的stop方法先被调用，小的后被调用。
     *
     */
    @Override
    public int getPhase() {
        return 0;
    }
}
