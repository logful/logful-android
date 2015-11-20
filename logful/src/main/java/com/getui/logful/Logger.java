package com.getui.logful;

public interface Logger {

    /**
     * 获取 logger 设置的名称.
     *
     * @return Logger 名称
     */
    String getName();

    /**
     * 设置消息模板.
     *
     * @param layout 模板内容
     */
    void setMsgLayout(String layout);

    /**
     * 返回设置的消息模板信息.
     *
     * @return 模板内容
     */
    String getMsgLayout();

    /**
     * 是否打印当前 level 的日志信息.
     *
     * @param level 日志 level
     * @return 是否开启
     */
    boolean isEnabled(int level);

    /**
     * 设置需要打印的日志级别.
     *
     * @param levels 日志级别数组
     */
    void recordLogLevel(int... levels);

    /**
     * 打印 verbose 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void verbose(String tag, String msg);

    /**
     * 打印 verbose 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void verbose(String tag, String msg, boolean capture);

    /**
     * 打印 debug 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void debug(String tag, String msg);

    /**
     * 打印 debug 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void debug(String tag, String msg, boolean capture);

    /**
     * 打印 info 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void info(String tag, String msg);

    /**
     * 打印 info 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void info(String tag, String msg, boolean capture);

    /**
     * 打印 warn 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void warn(String tag, String msg);

    /**
     * 打印 warn 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void warn(String tag, String msg, boolean capture);

    /**
     * 打印 error 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void error(String tag, String msg);

    /**
     * 打印 error 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void error(String tag, String msg, boolean capture);

    /**
     * 打印 exception 信息.
     *
     * @param tag       Tag
     * @param msg       Message
     * @param throwable Throwable
     */
    void exception(String tag, String msg, Throwable throwable);

    /**
     * 打印 exception 信息.
     *
     * @param tag       Tag
     * @param msg       Message
     * @param throwable Throwable
     * @param capture   Capture screen
     */
    void exception(String tag, String msg, Throwable throwable, boolean capture);

    /**
     * 打印 fatal 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    void fatal(String tag, String msg);

    /**
     * 打印 fatal 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    void fatal(String tag, String msg, boolean capture);

}
