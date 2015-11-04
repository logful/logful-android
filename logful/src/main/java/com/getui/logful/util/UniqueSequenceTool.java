package com.getui.logful.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.getui.logful.db.DatabaseManager;

public class UniqueSequenceTool {

    private AtomicInteger atomicInteger;

    private static class ClassHolder {
        static UniqueSequenceTool tool = new UniqueSequenceTool();
    }

    public static UniqueSequenceTool too() {
        return ClassHolder.tool;
    }

    public UniqueSequenceTool() {
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
        UniqueSequenceTool tool = too();
        return tool.atomicInteger.getAndIncrement();
    }
}
