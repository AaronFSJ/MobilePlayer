package com.aaron.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;

/**
 *
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去掉标题,如果继承AppCompatActivity不起作用，要继承Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //延时2s加载
        new Handler().postDelayed(new Thread(){
            @Override
            public void run() {
                startViewList();
            }
        },2000);

    }

    private boolean isStart = false;//是否已经启动，false未启动,true已经启动
    public void startViewList(){
        if(!isStart){
            Intent intent = new Intent(this,VideoListActivity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
            isStart = true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startViewList();
        return true;
    }
}
