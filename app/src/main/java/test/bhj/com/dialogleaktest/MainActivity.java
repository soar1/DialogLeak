package test.bhj.com.dialogleaktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 重现步骤：
 * 运行app，在MainActivity页面点击按钮打开FourActivity，点击发送消息，再返回到MainActivity。
 * 再继续同样操作，如此反复来回切换两个Activity，即可重现内存泄漏现象，LeakCanary会提示并记录。
 */
public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MainActivity.this.startActivity(new Intent(MainActivity.this, FourActivity.class));
            }
        });
    }
}