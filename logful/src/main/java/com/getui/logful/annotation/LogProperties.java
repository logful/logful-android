package com.getui.logful.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.getui.logful.LoggerConstants;

@Retention(RetentionPolicy.RUNTIME)
public @interface LogProperties {

    /**
     * 默认的 logger 名称.
     *
     * @return Logger 名称
     */
    String defaultLogger() default LoggerConstants.DEFAULT_LOGGER_NAME;

    /**
     * 默认的消息模板.
     *
     * @return 模板内容
     */
    String defaultMsgLayout() default LoggerConstants.DEFAULT_MSG_LAYOUT;
}
