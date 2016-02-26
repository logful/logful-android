package com.getui.logful.crash;

public class CrashReportField {

    public static class Report {

        public static final String REPORT = "report";

        public static final String SYSTEM = "system";

        public static final String CRASH = "crash";

        public static final String TIMESTAMP = "timestamp";

        public static final String THREADS = "threads";

        public static final String ERROR = "error";

        public static final String LOGCAT = "logcat";

        public static final String EXCEPTION = "exception";
    }

    public static class Common {

        public static final String NAME = "name";

        public static final String UUID = "uuid";

        public static final String ID = "id";

        public static final String TOTAL = "total";

        public static final String AVAILABLE = "available";
    }

    public static class System {

        public static final String APP_START_TIME = "app_start_time";

        public static final String MEMORY = "memory";

        public static final String PROCESS_ID = "process_id";

        public static final String PROCESS_NAME = "process_name";

        public static final String ROOTED = "rooted";

        public static final String PACKAGE_NAME = "package_name";

        public static final String VERSION = "version";

        public static final String VERSION_NAME = "version_name";

        public static final String MODEL = "model";

        public static final String OS_VERSION = "os_version";

        public static final String SDK_VERSION = "sdk_version";

        public static final String SUPPORTED_ABI = "supported_abi";

        public static final String DISK = "disk";

        public static final String INTERNAL = "internal";

        public static final String EXTERNAL = "external";

        public static final String CPU_ARCH = "cpu_arch";
    }

    public static class Thread {

        public static final String PRIORITY = "priority";

        public static final String GROUP_NAME = "group_name";

        public static final String STACK_TRACE = "stack_trace";

        public static final String CRASHED = "crashed";
    }

    public static class StackTrace {

        public static final String CLASS_NAME = "class_name";

        public static final String METHOD_NAME = "method_name";

        public static final String FILE_NAME = "file_name";

        public static final String LINE_NUMBER = "line_number";
    }

    public static class Logcat {

        public static final String LOGCAT = "logcat";

        public static final String EVENTS = "events";

        public static final String RADIO = "radio";
    }

    public static class Crash {

        public static final String REASON = "reason";

        public static final String TYPE = "type";
    }
}
