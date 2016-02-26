package com.getui.logful.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.getui.logful.db.DatabaseManager;

public class UniqueSequenceUtils {

    private AtomicInteger atomicInteger;

    private static class ClassHolder {
        static UniqueSequenceUtils tool = new UniqueSequenceUtils();
    }

    public static UniqueSequenceUtils too() {
        return ClassHolder.tool;
    }

    public UniqueSequenceUtils() {
        int maxId = DatabaseManager.findMaxAttachmentSequence();
        if (maxId == -1) {
            Random random = new Random();
            int start = random.nextInt(6000000 - 1000000 + 1) + 1000000;
            atomicInteger = new AtomicInteger(start);
        } else {
            atomicInteger = new AtomicInteger(maxId + 1);
        }
    }

    /**
     * Generate unique sequence int number.
     * 
     * @return Unique sequence int number
     */
    public static int sequence() {
        UniqueSequenceUtils tool = too();
        return tool.atomicInteger.getAndIncrement();
    }
}
