package test.bhj.com.dialogleaktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 模拟本地变量一直引用Message，导致Message无法被回收的内存泄漏现象
 */
public class SecondActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.btn_test1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MyMessage message = new MyMessage("Hello Leaking World: ", SecondActivity.this);
                startThread(message);
            }
        });
    }

    static void startThread(MyMessage message)
    {
        final BlockingQueue<MyMessage> queue = new LinkedBlockingQueue<>();
        queue.offer(message);

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    loop(queue);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    static void loop(BlockingQueue<MyMessage> queue) throws InterruptedException
    {
        while (true)
        {
            MyMessage message = queue.take();
            Log.v("", "Received: " + message);
            // 局部变量的生命周期在Dalvik VM跟ART/JVM中有区别。
            // 在DVM中，假如线程死循环或者阻塞，那么线程栈帧中的局部变量假如没有被置为null，那么就不会被回收。
            // 在VM 中，每一个栈帧都是本地变量的集合，而垃圾回收器是保守的：只要存在一个存活的引用，就不会回收它。
            // 在每次循环结束后，本地变量不再可访问，然而本地变量仍持有对 Message 的引用，
            // interpreter/JIT 理论上应该在本地变量不可访问时将其引用置为 null，然而它们并没有这样做，
            // 引用仍然存活，而且不会被置为 null，使得它不会被回收！！
            //
            // 例如这里的message本来用完后就被回收了，但实际上没有，所以需要手动置为null，并且log一下，防止被编译器优化掉
            // 另外该问题只出现在DVM中，也就是5.0以下。
            //message = null;
            //Log.v("", "Received: " + message);
        }
    }
}