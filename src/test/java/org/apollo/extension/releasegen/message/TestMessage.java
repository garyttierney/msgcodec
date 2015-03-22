package org.apollo.extension.releasegen.message;

import java.nio.ByteBuffer;

public class TestMessage {
    private int test;

    public void setTest(int test) {
        this.test = test;
    }

    public int getTest() {
        return test;
    }

    public static TestMessage create(ByteBuffer buffer) {
        TestMessage message = new TestMessage();
        int test = buffer.getInt();
        message.setTest(test);

        return message;
    }
}
