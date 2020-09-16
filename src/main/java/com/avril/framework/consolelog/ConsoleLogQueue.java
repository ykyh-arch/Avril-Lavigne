package com.avril.framework.consolelog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 创建一个阻塞队列，作为日志系统输出的日志的一个临时载体
 *
 * @author Ykyh
 * @link https://cloud.tencent.com/developer/article/1096792
 */
public class ConsoleLogQueue {

    /**
     * 队列大小
     */
    public static final int QUEUE_MAX_SIZE = 10000;

    private static Logger logger = LoggerFactory.getLogger(ConsoleLogQueue.class);

    private static final ConsoleLogQueue alarmMessageQueue = new ConsoleLogQueue();
    /**
     * 阻塞队列
     */
    private final BlockingQueue blockingQueue = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);

    private ConsoleLogQueue() {
    }

    public static ConsoleLogQueue getInstance() {
        return alarmMessageQueue;
    }

    /**
     * 消息入队
     *
     * @param log
     * @return
     */
    public boolean push(ConsoleLog log) {
        return this.blockingQueue.add(log);
    }

    /**
     * 消息出队
     *
     * @return
     */
    public ConsoleLog poll() {
        ConsoleLog result = null;
        try {
            result = (ConsoleLog) this.blockingQueue.take();
        } catch (InterruptedException e) {
            logger.warn("消息出列异常:{}", e.getMessage());
        }
        return result;
    }
}
