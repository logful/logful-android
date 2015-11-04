package com.getui.logful.schedule;

import java.io.File;
import java.util.List;

import com.getui.logful.LoggerConstants;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.LogFileMeta;
import com.getui.logful.util.Checksum;
import com.getui.logful.util.DateTimeUtil;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.StringUtils;

public class RefreshScheduleTask extends AbstractTask {

    public RefreshScheduleTask(String name) {
        super(name);
    }

    @Override
    public void exec() {
        if (LogStorage.readable()) {
            List<LogFileMeta> metaList = DatabaseManager.findAllLogFileMeta();
            for (LogFileMeta meta : metaList) {
                String logDir = null;
                switch (meta.getLocation()) {
                    case LoggerConstants.LOCATION_EXTERNAL:
                        if (LogStorage.writable()) {
                            logDir = LogStorage.externalLogDir();
                        }
                        break;
                    case LoggerConstants.LOCATION_INTERNAL:
                        logDir = LogStorage.internalLogDir();
                        break;
                    default:
                        break;
                }
                if (logDir != null) {
                    File file = new File(logDir + "/" + meta.getFilename());
                    if (meta.getStatus() != LoggerConstants.STATE_DELETED) {
                        if (file.exists() && file.isFile()) {
                            // 判断日志文件是否存在
                            if (meta.isEof() && StringUtils.isEmpty(meta.getFileMD5())) {
                                // 计算已经写满的日志文件 MD5
                                meta.setFileMD5(Checksum.fileMD5(file.getAbsolutePath()));
                                DatabaseManager.saveLogFileMeta(meta);
                            }
                        } else {
                            meta.setStatus(LoggerConstants.STATE_DELETED);
                            meta.setDeleteTime(System.currentTimeMillis());
                            DatabaseManager.saveLogFileMeta(meta);
                        }
                    }
                    // 更新今天之前的日志文件状态信息
                    if (!meta.isEof() && meta.getStatus() == LoggerConstants.STATE_NORMAL) {
                        long dayStartTimeStamp = DateTimeUtil.dayStartTimestamp();
                        if (meta.getCreateTime() < dayStartTimeStamp) {
                            meta.setEof(true);
                            meta.setStatus(LoggerConstants.STATE_WILL_UPLOAD);
                            meta.setFileMD5(Checksum.fileMD5(file.getAbsolutePath()));
                            DatabaseManager.saveLogFileMeta(meta);
                        }
                    }
                }
            }
        }
    }
}
