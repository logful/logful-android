# Logful

[![Build Status](https://travis-ci.org/logful/logful-android.svg?branch=master)](https://travis-ci.org/logful/logful-android)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/logful/logful-android/blob/master/LICENSE)

Logful remote logging sdk for android

## Dependency

```
dependencies {
    compile('com.getui:logful:0.2.0')
}
```

## Usage

### Initialize logful android sdk

``` java
import android.app.Application;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerFactory;
import com.getui.logful.annotation.LogProperties;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerFactory.init(this);
    }
}
```

### Log message use logful android sdk

``` java
// Use default logger
LoggerFactory.debug(TAG, "debug message");

// Use custom logger
Logger logger = LoggerFactory.logger("sample");
logger.verbose(TAG, "verbose message");
```

### Log message with screenshot use logful android sdk

``` java
// Use default logger
LoggerFactory.debug(TAG, "debug message", true);

// Use custom logger
Logger logger = LoggerFactory.logger("sample");
logger.verbose(TAG, "verbose message", true);
```

## License
The MIT License (MIT)

Copyright (c) 2015 Zhejiang Meiri Hudong Network Technology Co. Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
