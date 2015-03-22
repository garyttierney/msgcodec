package sfix.msgcodec.message;

public class TestMessage {
    public int test;
    public short testB;
    public short[] testC;
    public TestCompoundMessage[] testD;

    public TestCompoundMessage[] getTestD() {
        return testD;
    }

    public void setTestD(TestCompoundMessage[] testD) {
        this.testD = testD;
    }

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
