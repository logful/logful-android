package com.getui.logful.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.appender.LogEvent;
import com.getui.logful.entity.AttachmentFileMeta;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.entity.LogFileMeta;
import com.getui.logful.entity.MsgLayout;
import com.getui.logful.util.DateTimeUtil;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "logful.db";

    private static final String TABLE_LOG_FILE_META = "logful_log_meta";
    private static final String TABLE_CRASH_REPORT_FILE_META = "logful_crash_meta";
    private static final String TABLE_MSG_LAYOUT = "logful_layout";
    private static final String TABLE_ATTACHMENT_FILE_META = "logful_attachment_meta";

    // Log file meta table field.
    private static final String LOG_FIELD_ID = "id";
    private static final String LOG_FIELD_LOGGER_NAME = "logger_name";
    private static final String LOG_FIELD_FILENAME = "filename";
    private static final String LOG_FIELD_FRAGMENT = "fragment";
    private static final String LOG_FIELD_DATE_STRING = "date";
    private static final String LOG_FIELD_STATUS = "status";
    private static final String LOG_FIELD_MD5 = "md5";
    private static final String LOG_FIELD_LOCATION = "location";
    private static final String LOG_FIELD_LEVEL = "level";
    private static final String LOG_FIELD_EOF = "eof";
    private static final String LOG_FIELD_CREATE_TIME = "create_time";
    private static final String LOG_FIELD_DELETE_TIME = "delete_time";

    // Crash report file meta table field.
    private static final String CRASH_FIELD_ID = "id";
    private static final String CRASH_FIELD_FILENAME = "filename";
    private static final String CRASH_FIELD_LOCATION = "location";
    private static final String CRASH_FIELD_CAUSE = "cause";
    private static final String CRASH_FIELD_CREATE_TIME = "create_time";
    private static final String CRASH_FIELD_DELETE_TIME = "delete_time";
    private static final String CRASH_FIELD_STATUS = "status";
    private static final String CRASH_FIELD_FILE_MD5 = "md5";

    // Msg layout table field.
    private static final String MSG_LAYOUT_FIELD_ID = "id";
    private static final String MSG_LAYOUT_FIELD_LAYOUT = "layout";

    // Attachment file meta table field.
    private static final String ATTACHMENT_FIELD_ID = "id";
    private static final String ATTACHMENT_FIELD_FILENAME = "filename";
    private static final String ATTACHMENT_FIELD_LOCATION = "location";
    private static final String ATTACHMENT_FIELD_SEQUENCE = "sequence";
    private static final String ATTACHMENT_FIELD_CREATE_TIME = "create_time";
    private static final String ATTACHMENT_FIELD_DELETE_TIME = "delete_time";
    private static final String ATTACHMENT_FIELD_STATUS = "status";
    private static final String ATTACHMENT_FIELD_FILE_MD5 = "md5";

    private static final String AND = " AND ";

    private static final int SCHEMA_VERSION = 11;

    private static ConcurrentHashMap<String, Short> layoutMap = new ConcurrentHashMap<String, Short>();

    private static DatabaseManager instance;

    public static synchronized DatabaseManager manager() {
        if (instance == null) {
            Context context = LoggerFactory.context();
            if (context != null) {
                instance = new DatabaseManager(context.getApplicationContext());
            }
        }
        return instance;
    }

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String text = " TEXT,";
        String integer = " INTEGER,";

        // Create log file meta table.
        String createLogFileMetaTable =
                "CREATE TABLE " + TABLE_LOG_FILE_META + "(" + LOG_FIELD_ID + " INTEGER PRIMARY KEY,"
                        + LOG_FIELD_LOGGER_NAME + text + LOG_FIELD_FILENAME + " TEXT NOT NULL," + LOG_FIELD_FRAGMENT
                        + integer + LOG_FIELD_DATE_STRING + text + LOG_FIELD_STATUS + integer + LOG_FIELD_MD5 + text
                        + LOG_FIELD_LOCATION + integer + LOG_FIELD_LEVEL + integer + LOG_FIELD_EOF + integer
                        + LOG_FIELD_CREATE_TIME + integer + LOG_FIELD_DELETE_TIME + " INTEGER" + ")";

        // Create crash report file meta table.
        String createCrashReportFileMetaTable =
                "CREATE TABLE " + TABLE_CRASH_REPORT_FILE_META + "(" + CRASH_FIELD_ID + " INTEGER PRIMARY KEY,"
                        + CRASH_FIELD_FILENAME + text + CRASH_FIELD_LOCATION + integer + CRASH_FIELD_CAUSE + text
                        + CRASH_FIELD_CREATE_TIME + integer + CRASH_FIELD_DELETE_TIME + integer + CRASH_FIELD_STATUS + integer
                        + CRASH_FIELD_FILE_MD5 + " TEXT" + ")";

        // Create msg layout table.
        String createMsgLayoutTable =
                "CREATE TABLE " + TABLE_MSG_LAYOUT + "(" + MSG_LAYOUT_FIELD_ID + " INTEGER PRIMARY KEY,"
                        + MSG_LAYOUT_FIELD_LAYOUT + " TEXT NOT NULL UNIQUE" + ")";

        // Create attachment file meta table.
        String createAttachmentTable =
                "CREATE TABLE " + TABLE_ATTACHMENT_FILE_META + "(" + ATTACHMENT_FIELD_ID + " INTEGER PRIMARY KEY,"
                        + ATTACHMENT_FIELD_FILENAME + text + ATTACHMENT_FIELD_LOCATION + integer
                        + ATTACHMENT_FIELD_SEQUENCE + integer + ATTACHMENT_FIELD_CREATE_TIME + integer
                        + ATTACHMENT_FIELD_DELETE_TIME + integer + ATTACHMENT_FIELD_STATUS + integer
                        + ATTACHMENT_FIELD_FILE_MD5 + " TEXT" + ")";

        db.execSQL(createLogFileMetaTable);
        db.execSQL(createCrashReportFileMetaTable);
        db.execSQL(createMsgLayoutTable);
        db.execSQL(createAttachmentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    /**
     * Get layout string id.
     *
     * @param layoutString Layout string
     * @return Layout id
     */
    public static short layoutId(String layoutString) {
        if (StringUtils.isEmpty(layoutString)) {
            return -1;
        }
        Short layoutId = layoutMap.get(layoutString);
        if (layoutId == null) {
            MsgLayout msgLayout = new MsgLayout();
            msgLayout.setLayout(layoutString);
            layoutId = (short) DatabaseManager.saveMsgLayout(msgLayout);
            layoutMap.put(layoutString, layoutId);
        }
        return layoutId;
    }

    /**
     * Insert or update {@link LogFileMeta}.
     *
     * @param meta {@link LogFileMeta}
     */
    public static boolean saveLogFileMeta(LogFileMeta meta) {
        if (meta == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(LOG_FIELD_LOGGER_NAME, meta.getLoggerName());
        values.put(LOG_FIELD_FILENAME, meta.getFilename());
        values.put(LOG_FIELD_FRAGMENT, meta.getFragment());
        values.put(LOG_FIELD_STATUS, meta.getStatus());
        values.put(LOG_FIELD_MD5, meta.getFileMD5());
        values.put(LOG_FIELD_LOCATION, meta.getLocation());
        values.put(LOG_FIELD_LEVEL, meta.getLevel());
        values.put(LOG_FIELD_EOF, meta.isEof());
        values.put(LOG_FIELD_CREATE_TIME, meta.getCreateTime());
        values.put(LOG_FIELD_DELETE_TIME, meta.getDeleteTime());

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        if (meta.getId() == -1) {
            // 插入当前日期方便查询
            values.put(LOG_FIELD_DATE_STRING, DateTimeUtil.dateString());
            long result = db.insert(TABLE_LOG_FILE_META, null, values);
            return result != -1;
        } else {
            int result = db.update(TABLE_LOG_FILE_META, values, LOG_FIELD_ID + "=" + meta.getId(), null);
            return result > 0;
        }
    }

    public static boolean saveCrashFileMeta(CrashReportFileMeta meta) {
        if (meta == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(CRASH_FIELD_FILENAME, meta.getFilename());
        values.put(CRASH_FIELD_LOCATION, meta.getLocation());
        values.put(CRASH_FIELD_CAUSE, meta.getCause());
        values.put(CRASH_FIELD_CREATE_TIME, meta.getCreateTime());
        values.put(CRASH_FIELD_DELETE_TIME, meta.getDeleteTime());
        values.put(CRASH_FIELD_STATUS, meta.getStatus());
        values.put(CRASH_FIELD_FILE_MD5, meta.getFileMD5());

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        if (meta.getId() == -1) {
            long result = db.insert(TABLE_CRASH_REPORT_FILE_META, null, values);
            return result != -1;
        } else {
            int result = db.update(TABLE_CRASH_REPORT_FILE_META, values, LOG_FIELD_ID + "=" + meta.getId(), null);
            return result > 0;
        }
    }

    public static long saveMsgLayout(MsgLayout layout) {
        if (layout == null) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(MSG_LAYOUT_FIELD_LAYOUT, layout.getLayout());

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        long rowId = -1;
        if (layout.getId() == -1) {
            String selection = MSG_LAYOUT_FIELD_LAYOUT + "=?";
            String[] selectionArgs = new String[]{layout.getLayout()};
            Cursor cursor = db.query(TABLE_MSG_LAYOUT, null, selection, selectionArgs, null, null, null, "1");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    rowId = cursor.getLong(cursor.getColumnIndex(MSG_LAYOUT_FIELD_ID));
                }
                cursor.close();
            }

            if (rowId == -1) {
                rowId = db.insert(TABLE_MSG_LAYOUT, null, values);
            }
        } else {
            int result = db.update(TABLE_MSG_LAYOUT, values, MSG_LAYOUT_FIELD_ID + "=" + layout.getId(), null);
            if (result > 0) {
                rowId = layout.getId();
            }
        }

        return rowId;
    }

    public static boolean saveAttachmentFileMeta(AttachmentFileMeta meta) {
        if (meta == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(ATTACHMENT_FIELD_FILENAME, meta.getFilename());
        values.put(ATTACHMENT_FIELD_LOCATION, meta.getLocation());
        values.put(ATTACHMENT_FIELD_SEQUENCE, meta.getSequence());
        values.put(ATTACHMENT_FIELD_CREATE_TIME, meta.getCreateTime());
        values.put(ATTACHMENT_FIELD_DELETE_TIME, meta.getDeleteTime());
        values.put(ATTACHMENT_FIELD_STATUS, meta.getStatus());
        values.put(ATTACHMENT_FIELD_FILE_MD5, meta.getFileMD5());

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        if (meta.getId() == -1) {
            long result = db.insert(TABLE_ATTACHMENT_FILE_META, null, values);
            return result != -1;
        } else {
            int result = db.update(TABLE_ATTACHMENT_FILE_META, values, ATTACHMENT_FIELD_ID + "=" + meta.getId(), null);
            return result > 0;
        }
    }

    /**
     * Find max attachment sequence int number. <br/>
     * <br/>
     * Return -1 when no record exist.
     *
     * @return Sequence number
     */
    public static int findMaxAttachmentSequence() {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor cursor =
                db.query(TABLE_ATTACHMENT_FILE_META, null, null, null, null, null, ATTACHMENT_FIELD_SEQUENCE + " DESC",
                        "1");

        int sequence = -1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sequence = cursor.getInt(cursor.getColumnIndex(ATTACHMENT_FIELD_SEQUENCE));
            }
            cursor.close();
        }

        return sequence;
    }

    public static boolean deleteLogFileMeta(LogFileMeta meta) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        int result = db.delete(TABLE_LOG_FILE_META, LOG_FIELD_ID + "=" + meta.getId(), null);
        return result > 0;
    }

    public static boolean deleteCrashFileMeta(CrashReportFileMeta meta) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        int result = db.delete(TABLE_CRASH_REPORT_FILE_META, LOG_FIELD_ID + "=" + meta.getId(), null);
        return result > 0;
    }

    public static boolean deleteMsgLayout(MsgLayout layout) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        int result = db.delete(TABLE_MSG_LAYOUT, MSG_LAYOUT_FIELD_ID + "=" + layout.getId(), null);
        return result > 0;
    }

    private static LogFileMeta findByFilename(String filename) {
        String selection = LOG_FIELD_FILENAME + "=?";
        String[] selectionArgs = new String[]{filename};

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor cursor = db.query(TABLE_LOG_FILE_META, null, selection, selectionArgs, null, null, null, "1");
        LogFileMeta meta = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                meta = manager.getLogFileMeta(cursor);
            }
            cursor.close();
        }
        return meta;
    }

    public static boolean closeLogFile(String loggerName, int level, int fragment) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        String date = DateTimeUtil.dateString();

        ContentValues values = new ContentValues();
        values.put(LOG_FIELD_EOF, true);
        values.put(LOG_FIELD_STATUS, LoggerConstants.STATE_WILL_UPLOAD);

        String[] conditions =
                new String[]{String.format("%s='%s'", LOG_FIELD_LOGGER_NAME, loggerName),
                        String.format("%s=%d", LOG_FIELD_LEVEL, level),
                        String.format("%s=%d", LOG_FIELD_FRAGMENT, fragment),
                        String.format("%s=%s", LOG_FIELD_DATE_STRING, date)};

        int result = db.update(TABLE_LOG_FILE_META, values, StringUtils.join(conditions, " AND "), null);
        return result > 0;
    }

    /**
     * 关闭所有正在写入的日志文件.
     *
     * @return result
     */
    public static boolean closeAllFile() {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOG_FIELD_EOF, true);
        values.put(LOG_FIELD_STATUS, LoggerConstants.STATE_WILL_UPLOAD);
        int result = db.update(TABLE_LOG_FILE_META, values, LOG_FIELD_EOF + "=0", null);

        return result > 0;
    }

    public static LogFileMeta findMaxFragment(LogEvent logEvent) {
        if (logEvent == null) {
            return null;
        }
        String loggerName = logEvent.getLoggerName();
        int level = logEvent.getLevel();
        String date = DateTimeUtil.dateString();
        String selection =
                LOG_FIELD_LOGGER_NAME + "=?" + AND + LOG_FIELD_LEVEL + "=?" + AND + LOG_FIELD_DATE_STRING + "=?";
        String[] selectionArgs = new String[]{loggerName, String.valueOf(level), date};

        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_LOG_FILE_META, null, selection, selectionArgs, null, null, LOG_FIELD_FRAGMENT + " DESC",
                        "1");
        LogFileMeta meta = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                meta = manager.getLogFileMeta(cursor);
            }
            cursor.close();
        }
        return meta;
    }

    /**
     * 查找指定 level 的日志文件记录.
     *
     * @param levels 日志 levels
     * @param status 文件状态
     * @return {@link LogFileMeta} list
     */
    public static List<LogFileMeta> findAllLogFileMetaByLevel(int[] levels, int status) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();
        int size = levels.length;
        if (size > 0) {
            String[] selectionArray = new String[size];
            String[] selectionArgs = new String[size];
            for (int i = 0; i < size; i++) {
                selectionArray[i] = LOG_FIELD_LEVEL + "=?";
                selectionArgs[i] = String.valueOf(levels[i]);
            }
            String selection = StringUtils.join(selectionArray, " OR ");

            if (status != LoggerConstants.STATE_ALL) {
                selection = String.format("(%s) AND %s = %d", selection, LOG_FIELD_STATUS, status);
            }

            Cursor cursor = db.query(TABLE_LOG_FILE_META, null, selection, selectionArgs, null, null, null, null);
            return manager.exactLogFileMetaCursor(cursor);
        }

        return new ArrayList<LogFileMeta>();
    }

    /**
     * 查找指定 level 和时间间隔内创建的日志文件记录.
     *
     * @param levels    日志 levels
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param status    文件状态
     * @return {@link LogFileMeta} list
     */
    public static List<LogFileMeta> findAllLogFileMetaByLevelAndTime(int[] levels, long startTime, long endTime,
                                                                     int status) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        int length = levels.length;
        if (length > 0) {
            String[] selectionArray = new String[length];
            String[] selectionArgs = new String[length + 2];
            for (int i = 0; i < length; i++) {
                selectionArray[i] = LOG_FIELD_LEVEL + "=?";
                selectionArgs[i] = String.valueOf(levels[i]);
            }
            String selection =
                    StringUtils.join(selectionArray, " OR ") + AND + LOG_FIELD_CREATE_TIME + ">=?" + AND
                            + LOG_FIELD_CREATE_TIME + "<=?";
            selectionArgs[length] = String.valueOf(startTime);
            selectionArgs[length + 1] = String.valueOf(endTime);

            if (status != LoggerConstants.STATE_ALL) {
                selection = String.format("(%s) AND %s = %d", selection, LOG_FIELD_STATUS, status);
            }

            Cursor cursor = db.query(TABLE_LOG_FILE_META, null, selection, selectionArgs, null, null, null, null);
            return manager.exactLogFileMetaCursor(cursor);
        }

        return new ArrayList<LogFileMeta>();
    }

    /**
     * 查找指定时间间隔内创建的日志文件记录.
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param status    文件状态
     * @return {@link LogFileMeta} list
     */
    public static List<LogFileMeta> findAllLogFileMetaByTime(long startTime, long endTime, int status) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        String selection = LOG_FIELD_CREATE_TIME + ">=?" + AND + LOG_FIELD_CREATE_TIME + "<=?";
        String[] selectionArgs = new String[]{String.valueOf(startTime), String.valueOf(endTime)};

        if (status != LoggerConstants.STATE_ALL) {
            selection = String.format("(%s) AND %s = %d", selection, LOG_FIELD_STATUS, status);
        }

        Cursor cursor = db.query(TABLE_LOG_FILE_META, null, selection, selectionArgs, null, null, null, null);

        return manager.exactLogFileMetaCursor(cursor);
    }

    /**
     * 读取所有的日志文件记录.
     *
     * @return {@link LogFileMeta} list
     */
    public static List<LogFileMeta> findAllLogFileMeta() {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor cursor = db.query(TABLE_LOG_FILE_META, null, null, null, null, null, null, null);
        return manager.exactLogFileMetaCursor(cursor);
    }

    /**
     * 读取所有的崩溃日志文件记录.
     *
     * @param status 文件状态
     * @return {@link CrashReportFileMeta} list
     */
    public static List<CrashReportFileMeta> findAllCrashFileMeta(int status) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        String selection = null;
        if (status != LoggerConstants.STATE_ALL) {
            selection = String.format("%s = %d", CRASH_FIELD_STATUS, status);
        }

        Cursor cursor = db.query(TABLE_CRASH_REPORT_FILE_META, null, selection, null, null, null, null, null);
        return manager.exactCrashFileMetaCursor(cursor);
    }

    /**
     * 读取所有的附件文件记录.
     *
     * @param status 文件状态
     * @return {@link AttachmentFileMeta} list
     */
    public static List<AttachmentFileMeta> findAllAttachmentFileMeta(int status) {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        String selection = null;
        if (status != LoggerConstants.STATE_ALL) {
            selection = String.format("%s = %d", ATTACHMENT_FIELD_STATUS, status);
        }

        Cursor cursor = db.query(TABLE_ATTACHMENT_FILE_META, null, selection, null, null, null, null, null);
        return manager.exactAttachmentFileMetaCursor(cursor);
    }

    /**
     * 读取所有 Msg layout 记录.
     *
     * @return {@link MsgLayout} list
     */
    public static List<MsgLayout> getMsgLayoutList() {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        Cursor cursor = db.query(TABLE_MSG_LAYOUT, null, null, null, null, null, null, null);
        return manager.exactMsgLayoutCursor(cursor);
    }

    public static String getLayoutJson() {
        DatabaseManager manager = DatabaseManager.manager();
        SQLiteDatabase db = manager.getWritableDatabase();

        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.query(TABLE_MSG_LAYOUT, null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MSG_LAYOUT_FIELD_ID, cursor.getLong(cursor.getColumnIndex(MSG_LAYOUT_FIELD_ID)));
                    jsonObject.put(MSG_LAYOUT_FIELD_LAYOUT,
                            cursor.getString(cursor.getColumnIndex(MSG_LAYOUT_FIELD_LAYOUT)));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    LogUtil.e("DatabaseManager", "JSONException", e);
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        return jsonArray.toString();
    }

    private List<LogFileMeta> exactLogFileMetaCursor(Cursor cursor) {
        List<LogFileMeta> metaList = new ArrayList<LogFileMeta>();
        if (cursor == null) {
            return metaList;
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                LogFileMeta meta = getLogFileMeta(cursor);
                metaList.add(meta);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return metaList;
    }

    private LogFileMeta getLogFileMeta(Cursor cursor) {
        LogFileMeta meta = new LogFileMeta();
        meta.setId(cursor.getLong(cursor.getColumnIndex(LOG_FIELD_ID)));
        meta.setLoggerName(cursor.getString(cursor.getColumnIndex(LOG_FIELD_LOGGER_NAME)));
        meta.setFilename(cursor.getString(cursor.getColumnIndex(LOG_FIELD_FILENAME)));
        meta.setFragment(cursor.getInt(cursor.getColumnIndex(LOG_FIELD_FRAGMENT)));
        meta.setStatus(cursor.getInt(cursor.getColumnIndex(LOG_FIELD_STATUS)));
        meta.setFileMD5(cursor.getString(cursor.getColumnIndex(LOG_FIELD_MD5)));
        meta.setLocation(cursor.getInt(cursor.getColumnIndex(LOG_FIELD_LOCATION)));
        meta.setLevel(cursor.getInt(cursor.getColumnIndex(LOG_FIELD_LEVEL)));
        int eofValue = cursor.getInt(cursor.getColumnIndex(LOG_FIELD_EOF));
        meta.setEof(eofValue != 0);
        meta.setCreateTime(cursor.getLong(cursor.getColumnIndex(LOG_FIELD_CREATE_TIME)));
        meta.setDeleteTime(cursor.getLong(cursor.getColumnIndex(LOG_FIELD_DELETE_TIME)));
        return meta;
    }

    private List<CrashReportFileMeta> exactCrashFileMetaCursor(Cursor cursor) {
        List<CrashReportFileMeta> metaList = new ArrayList<CrashReportFileMeta>();
        if (cursor == null) {
            return metaList;
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                CrashReportFileMeta meta = getCrashFileMeta(cursor);
                metaList.add(meta);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return metaList;
    }

    private CrashReportFileMeta getCrashFileMeta(Cursor cursor) {
        CrashReportFileMeta meta = new CrashReportFileMeta();
        meta.setId(cursor.getLong(cursor.getColumnIndex(CRASH_FIELD_ID)));
        meta.setFilename(cursor.getString(cursor.getColumnIndex(CRASH_FIELD_FILENAME)));
        meta.setLocation(cursor.getInt(cursor.getColumnIndex(CRASH_FIELD_LOCATION)));
        meta.setCause(cursor.getString(cursor.getColumnIndex(CRASH_FIELD_CAUSE)));
        meta.setCreateTime(cursor.getLong(cursor.getColumnIndex(CRASH_FIELD_CREATE_TIME)));
        meta.setDeleteTime(cursor.getLong(cursor.getColumnIndex(CRASH_FIELD_DELETE_TIME)));
        meta.setStatus(cursor.getInt(cursor.getColumnIndex(CRASH_FIELD_STATUS)));
        meta.setFileMD5(cursor.getString(cursor.getColumnIndex(CRASH_FIELD_FILE_MD5)));
        return meta;
    }

    private List<MsgLayout> exactMsgLayoutCursor(Cursor cursor) {
        List<MsgLayout> layoutList = new ArrayList<MsgLayout>();
        if (cursor == null) {
            return layoutList;
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MsgLayout layout = getMsgLayout(cursor);
                layoutList.add(layout);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return layoutList;
    }

    private MsgLayout getMsgLayout(Cursor cursor) {
        MsgLayout layout = new MsgLayout();
        layout.setId(cursor.getLong(cursor.getColumnIndex(MSG_LAYOUT_FIELD_ID)));
        layout.setLayout(cursor.getString(cursor.getColumnIndex(MSG_LAYOUT_FIELD_LAYOUT)));
        return layout;
    }

    private List<AttachmentFileMeta> exactAttachmentFileMetaCursor(Cursor cursor) {
        List<AttachmentFileMeta> metaList = new ArrayList<AttachmentFileMeta>();
        if (cursor == null) {
            return metaList;
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                AttachmentFileMeta meta = getAttachmentFileMeta(cursor);
                metaList.add(meta);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return metaList;
    }

    private AttachmentFileMeta getAttachmentFileMeta(Cursor cursor) {
        AttachmentFileMeta meta = new AttachmentFileMeta();
        meta.setId(cursor.getLong(cursor.getColumnIndex(ATTACHMENT_FIELD_ID)));
        meta.setFilename(cursor.getString(cursor.getColumnIndex(ATTACHMENT_FIELD_FILENAME)));
        meta.setLocation(cursor.getInt(cursor.getColumnIndex(ATTACHMENT_FIELD_LOCATION)));
        meta.setSequence(cursor.getInt(cursor.getColumnIndex(ATTACHMENT_FIELD_SEQUENCE)));
        meta.setCreateTime(cursor.getLong(cursor.getColumnIndex(ATTACHMENT_FIELD_CREATE_TIME)));
        meta.setDeleteTime(cursor.getLong(cursor.getColumnIndex(ATTACHMENT_FIELD_DELETE_TIME)));
        meta.setStatus(cursor.getInt(cursor.getColumnIndex(ATTACHMENT_FIELD_STATUS)));
        meta.setFileMD5(cursor.getString(cursor.getColumnIndex(ATTACHMENT_FIELD_FILE_MD5)));
        return meta;
    }
}
