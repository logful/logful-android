package com.getui.logful.sample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.getui.logful.Logger;
import com.getui.logful.LoggerFactory;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText loggerNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.verbose_log_button).setOnClickListener(this);
        findViewById(R.id.debug_log_button).setOnClickListener(this);
        findViewById(R.id.info_log_button).setOnClickListener(this);
        findViewById(R.id.warn_log_button).setOnClickListener(this);
        findViewById(R.id.error_log_button).setOnClickListener(this);
        findViewById(R.id.exception_log_button).setOnClickListener(this);
        findViewById(R.id.fatal_log_button).setOnClickListener(this);

        findViewById(R.id.batch_log).setOnClickListener(this);
        findViewById(R.id.custom_logger).setOnClickListener(this);

        findViewById(R.id.crash_button).setOnClickListener(this);

        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggerFactory.syncLog();
            }
        });

        findViewById(R.id.interrupt_upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggerFactory.interruptThenSync();
            }
        });

        SwitchCompat switchButton = (SwitchCompat) findViewById(R.id.switch_log);
        switchButton.setChecked(LoggerFactory.isOn());
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LoggerFactory.turnOnLog();
                } else {
                    LoggerFactory.turnOffLog();
                }
            }
        });

        findViewById(R.id.capture_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureScreen();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.custom_logger:
                customLogger();
                break;
            case R.id.crash_button:
                crash();
                break;
        }
        log(view.getId(), "app");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void captureScreen() {
        LoggerFactory.debug(TAG, "some debug message", true);
    }

    private void crash() {
        throw new RuntimeException("force crash");
    }

    private void log(int viewId, String loggerName) {
        Logger logger = LoggerFactory.logger(loggerName);
        switch (viewId) {
            case R.id.verbose_log_button:
                logger.verbose(TAG, "some verbose message");
                break;
            case R.id.debug_log_button:
                logger.debug(TAG, "some|debug|message");
                break;
            case R.id.info_log_button:
                logger.info(TAG, "v:some|e:info|s:message:cid:4535345|d:sdssd:cid:34344");
                break;
            case R.id.warn_log_button:
                logger.warn(TAG, "some|e:warn|message");
                break;
            case R.id.error_log_button:
                logger.error(TAG, "v:some|e:500|r:234234234|b:43543534345345|c:32423.34534");
                break;
            case R.id.exception_log_button:
                logger.exception(TAG, "some exception message", null);
                break;
            case R.id.fatal_log_button:
                logger.fatal(TAG, "some fatal message");
                break;
            case R.id.batch_log:
                batchLog(logger);
                break;
        }
    }

    private void batchLog(final Logger logger) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int m = 0; m < 1000; m++) {
                    String msg = UUID.randomUUID().toString();
                    logger.verbose(TAG, msg);
                    logger.debug(TAG, msg);
                    logger.info(TAG, msg);
                    logger.warn(TAG, msg);
                    logger.error(TAG, msg);
                    logger.exception(TAG, msg, null);
                    logger.fatal(TAG, msg);
                }
            }
        }).start();
    }

    private void customLogger() {
        View dlgView = View.inflate(this, R.layout.dlg_custom_logger, null);
        loggerNameEditText = (EditText) dlgView.findViewById(R.id.logger_name);

        dlgView.findViewById(R.id.verbose_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.debug_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.info_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.warn_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.error_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.exception_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.fatal_log_button).setOnClickListener(dlgButtonOnClickListener);
        dlgView.findViewById(R.id.batch_log).setOnClickListener(dlgButtonOnClickListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dlgView);
        builder.setTitle(R.string.dlg_title);
        builder.setPositiveButton(R.string.dlg_confirm, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private View.OnClickListener dlgButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (loggerNameEditText != null) {
                String loggerName = loggerNameEditText.getText().toString();
                if (loggerName.length() > 0) {
                    log(view.getId(), loggerName);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.logger_name_empty_toast, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    };
}
