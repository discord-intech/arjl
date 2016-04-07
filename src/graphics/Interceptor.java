package graphics;


import java.io.OutputStream;
import java.io.PrintStream;

public class Interceptor extends PrintStream {
    public Interceptor(OutputStream out) {
        super(out, true);
    }

    @Override
    public synchronized void print(String s) {
        DeviceManager.addLineVerbose(s);
    }

    @Override
    public synchronized void println(String s) {
        DeviceManager.addLineVerbose(s);
    }

    @Override
    public synchronized void print(int i) {
        String s = "" + i;
        DeviceManager.addLineVerbose(s);
    }

    @Override
    public synchronized void println(int i) {
        String s = "" + i;
        DeviceManager.addLineVerbose(s);
    }

    @Override
    public synchronized void print(long i) {
        String s = "" + i;
        DeviceManager.addLineVerbose(s);
    }

    @Override
    public synchronized void println(long i) {
        String s = "" + i;
        DeviceManager.addLineVerbose(s);
    }
}


