package test.bhj.com.dialogleaktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ThreeActivity extends AppCompatActivity
{
    /**
     * 非静态内部类的静态实例----会产生内存泄漏
     * 成员内部类是依附外部类而存在的，也就是说，如果要创建成员内部类的对象，前提是必须存在一个外部类的对象。
     * http://www.cnblogs.com/dolphin0520/p/3811445.html
     * 外部实例化new ThreeActivity().new Demo()
     * 内部实例化内部类，是编译器默认给内部类加了一个外部类的引用，所以才可以随意访问外部类的成员
     */
    static Demo sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if (sInstance == null)
        {
            sInstance = new Demo();
        }
    }

    public ThreeActivity()
    {
        System.out.print("dosth.");
    }

    class Demo
    {
        void doSomething()
        {
            System.out.print("dosth.");
        }
    }
}