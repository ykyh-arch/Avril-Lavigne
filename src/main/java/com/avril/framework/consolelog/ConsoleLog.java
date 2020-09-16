package com.avril.framework.consolelog;

/**
 * 页面控制台日志实体
 *
 * @author ykyh-arch
 * @link https://cloud.tencent.com/developer/article/1096792
 **/
public class ConsoleLog {

    public static final String VIEW_PERM = "monitor:consolelog:view";


    public ConsoleLog() {
    }

    public ConsoleLog(String body, String timestamp, String fileName, int lineNumber, String threadName, String level) {
        this.body = body;
        this.timestamp = timestamp;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.threadName = threadName;
        this.level = level;
    }

    /**
     * 日志内容
     */
    private String body;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 类名
     */
    private String fileName;
    /**
     * 行号
     */
    private int lineNumber;
    /**
     * 线程名
     */
    private String threadName;
    /**
     * 日志等级
     */
    private String level;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
