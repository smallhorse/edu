package com.ubt.alpha1e.edu.base.loopHandler;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/**
 * @作者：ubt
 * @日期: 2017/11/17 20:20
 * @描述:
 */


public final class LooperThread extends Thread {
    private static Handler handler; // in Android Handler should be static or leaks might occur
    private HandlerCallback handlerCallback;

    public LooperThread() {
    }

    public LooperThread(HandlerCallback handlerCallback) {
        this.handlerCallback = handlerCallback;
    }

    @Override public void run() {
        // Note: Looper is usually already created in the Activity
        boolean looperIsNotPreparedInCurrentThread = Looper.myLooper() == null;


        if (looperIsNotPreparedInCurrentThread) {
            Looper.prepare();
        }
        handler = new Handler(new Handler.Callback() {
            @Override public boolean handleMessage(Message message) {
                Log.d(getClass().getSimpleName(), message.getData().toString());

                if (handlerCallback != null) {
                    // note: we can avoid this callback by using event bus depending on concrete use case
                    handlerCallback.handleMessage(message.getData());
                }
                return true;
            }
        });

        if (looperIsNotPreparedInCurrentThread) {
            Looper.loop();
        }
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }

    public static void send(Message message) {
        while(handler==null);
        handler.sendMessage(message);
    }
    public static void sendDelayMessage(Message message,long delay){
        handler.sendMessageDelayed(message,delay);
    }
}