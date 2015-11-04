package com.getui.logful.util;

public class VerifyMsgLayout {

    /**
     * 验证日志内容格式模板时候配置正确.
     * 
     * @param template 模板内容
     */
    public static void verify(String template) {
        if (template == null || template.length() == 0) {
            return;
        }
        if (template.contains("|")) {
            String[] fields = template.split("\\|");
            for (String field : fields) {
                VerifyMsgLayout.verifyAttributes(field);
            }
        } else {
            VerifyMsgLayout.verifyAttributes(template);
        }
    }

    private static void verifyAttributes(String attribute) {
        String[] attributes = attribute.split(",");
        if (attributes.length != 3) {
            throw new IllegalArgumentException("Must have three part");
        }

        if (attributes[0].length() == 0 || attributes[1].length() == 0) {
            throw new IllegalArgumentException("Must set a abbr and a full name");
        }

        if (!attributes[2].equalsIgnoreCase("%s") && !attributes[2].equalsIgnoreCase("%n")) {
            throw new IllegalArgumentException("Must set a type");
        }
    }

}
