package test.bhj.com.dialogleaktest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 模拟Dialog内存泄漏
 * */
public class FourActivity extends AppCompatActivity
{
    private HandlerThread mHandlerThread;
    private MyHandler mMyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mHandlerThread = new HandlerThread("test");
        mHandlerThread.start();
        mMyHandler = new MyHandler(mHandlerThread.getLooper());

        findViewById(R.id.btn_test1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Message msg = Message.obtain();
                msg.what = 1;
                mMyHandler.sendMessage(msg);
            }
        });

        //flushStackLocalLeaks(mHandlerThread.getLooper());
    }

    /**
     * 在库中使用该办法最为有效，不用考虑使用者有没有对Dialog进行过处理
     */
    static void flushStackLocalLeaks(Looper looper)
    {
        final Handler handler = new Handler(looper);
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler()
                {
                    @Override
                    public boolean queueIdle()
                    {
                        handler.sendMessageDelayed(handler.obtainMessage(), 1000);
                        return true;
                    }
                });
            }
        });
    }

    private class MyHandler extends Handler
    {
        public MyHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            if (msg.what == 1)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Dialog有一定几率会拿到HandlerThread泄漏的Message对象
                        // 只要拿到Dialog就把listener赋给了Message的obj变量，造成Activity内存泄漏
                        // 除非HandlerThread的本地变量被赋新值，从而释放对前一个Message的引用
                        // 否则Dialog销毁也解决不了这个泄漏问题
                        //
                        // 所以要么用包装类在Dialog销毁时切断与Message的关联；要么在HandlerThread空闲时发一个空消息
                        new AlertDialog.Builder(FourActivity.this)
                                .setPositiveButton("test", clickListener)
                                .show();
                    }
                });
            }
        }
    }

    final DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {

        }
    };
}