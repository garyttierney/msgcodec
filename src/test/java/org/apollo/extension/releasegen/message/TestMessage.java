package org.apollo.extension.releasegen.message;

public class TestMessage {
    private int test;
    private short testB;
    private short[] testC;

    public void setTest(int test) {
        this.test = test;
    }

    public int getTest() {
        return test;
    }

    public void setTestB(short testB) {
        this.testB = testB;
    }

    public void setTestC(short[] testC) {
        this.testC = testC;
    }

    public short[] getTestC() {
        return testC;
    }

    public short getTestB() {
        return testB;
    }
}
