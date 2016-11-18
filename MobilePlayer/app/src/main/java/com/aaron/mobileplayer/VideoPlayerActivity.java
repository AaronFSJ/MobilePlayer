package com.aaron.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aaron.util.Utils;

import java.util.ArrayList;

/**
 * 视频播放器
 * Created by hsl1106 on 2016/11/15.
 */

public class VideoPlayerActivity extends Activity implements View.OnClickListener {

    /**
     * 更新进度的消息
     */
    private final  static int GROGRESS = 0;
    /**
     * 隐藏底部控制条
     */
    private final static int HIDE_TOPBOTTOMLAYOUT =1;
    /**
     * 整个底部控制面板
     */
    private LinearLayout control_player;

    //手势识别器
    private GestureDetector detector;

    private VideoView videoview;

    /**
     * 是否播放中，false否，true是
     */
    private boolean isPlaying = true;

    /**
     * 播放地址
     */
    private Uri uri;
    /**
     * 前一个
     */
    private Button btn_player_pre;
    /**
     * 播放/暂停
     */
    private Button btn_player_pause;
    /**
     * 下一个
     */
    private Button btn_player_next;
    /**
     * 当前时间
     */
    private TextView tv_current_time;
    /**
     * 进度条SeekBar
     */
    private SeekBar seekbar;
    /**
     * 总时长
     */
    private TextView tv_duration;

    /**
     * 当前播放的视频的列表的位置
     */
    private int position;
    /**
     * 顺序地址数据集
     */
    private ArrayList<String> pathList;
    /**
     * 当前视频的播放位置

    private int currentPosition;*/
    /**
     * 是否结束或销毁，false未结束
     */
    private boolean isDestroy=false;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GROGRESS:{//更新状态
                    //得到当前播放进程的位置
                    int currentPosition = videoview.getCurrentPosition();
                    //获取总时长
                    int duration = videoview.getDuration();
                    //更新进度
                    tv_duration.setText(Utils.stringToTime(duration));
                    tv_current_time.setText(Utils.stringToTime(currentPosition));
                    //更新进度条SeekBar
                    seekbar.setProgress(currentPosition);
                    //
                    if(!isDestroy){
                        handler.sendEmptyMessageDelayed(GROGRESS,1000);//每1s发送，形成死循环，所以要在视频结束或销毁时停止发送
                    }
                    break;
                }
                case HIDE_TOPBOTTOMLAYOUT:{//隐藏控制面板
                    //Log.i("aaron", Locale.CHINESE.toString());
                    hidelayout();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vidioplayer);
        //所有的View对象初始化
        init();
        setListener();


        //如此播放不起来，还要设置监听,播放完成，播放准备好，播放出错
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoview.start();
                //一开始时是播放状态
                if(!isPlaying){//如果在播放中
                    btn_player_pause.setBackgroundResource(R.drawable.player_play);

                }else{
                    btn_player_pause.setBackgroundResource(R.drawable.player_pause);
                }
                //将视频长度与SeekBar关联，就可以得到当前的currentPosition就是SeekBar的进度
                seekbar.setMax(videoview.getDuration());


                //发送信息更新进度条状态
                handler.sendEmptyMessage(GROGRESS);

                //Toast.makeText(VideoPlayerActivity.this,"系统视频播放！",Toast.LENGTH_SHORT).show();
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();

                //Toast.makeText(VideoPlayerActivity.this,"视频播放完成！",Toast.LENGTH_LONG).show();
                //finish();
            }
        });


        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //Toast.makeText(VideoPlayerActivity.this,"视频播放出错！",Toast.LENGTH_LONG).show();
                //播放出错时尝试用万能播放器
                startVitamioPlayer();
                //Toast.makeText(VideoPlayerActivity.this,"视频播放出错！",Toast.LENGTH_LONG).show();
                //返回false系统会出现出错对话框
                return true;
            }
        });


        videoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoPlayerActivity.this,"点击了",Toast.LENGTH_SHORT).show();
                //videoview.pause();
            }
        });



        //设置VideoView的控制面板
        //videoview.setMediaController(new MediaController(this));改为自定义


    }

    /***
     * 是否显示底部控制面板
     * true--显示--false--隐藏
     */
    private boolean isShowLayout = false;
    public void init(){
        btn_player_pre = (Button) findViewById(R.id.btn_player_pre);
        btn_player_pause = (Button) findViewById(R.id.btn_player_pause);
        btn_player_next = (Button) findViewById(R.id.btn_player_next);
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tv_duration = (TextView) findViewById(R.id.tv_duration);

        control_player = (LinearLayout) findViewById(R.id.control_player);

        //获取意图传过来的参数
        Intent intent = getIntent();
        //获取播放地址
        uri = intent.getData();
        //videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("listSize");
        position = intent.getIntExtra("position",0);
        pathList = intent.getStringArrayListExtra("pathList");

        handler.sendEmptyMessageDelayed(HIDE_TOPBOTTOMLAYOUT,5000);

    }

    /**
     * 设置监听器
     */
    public  void setListener(){
        //获取播放器
        videoview = (VideoView) findViewById(R.id.videoview);
        videoview.setVideoURI(uri);

        btn_player_pre.setOnClickListener(this);
        btn_player_pause.setOnClickListener(this);
        btn_player_next.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条进度改变的时候调用
             * progress是指定播放的进度
             * fromUser是否来自用户，当用户操作进度条的时候，fromUser=true
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){//响应用户滑动，如果不设置系统自动更新进度也会进入此方法
                    videoview.seekTo(progress);//跳转到滑动的位置
                }
            }

            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessage(GROGRESS);//停止拖动做一次更新状态
            }
        });


        //实例化手势
//        detector = new GestureDetector(new GestureDetector.OnGestureListener() {
//
//            //单击
//            @Override
//            public boolean onDown(MotionEvent e) {
//                Toast.makeText(VideoPlayerActivity.this,"点击了",Toast.LENGTH_SHORT).show();
//                if (isShowLayout) {
//                    removeHideTopBottomLayoutMessage();
//                    hidelayout();
//                } else {
//                    sendDelayedHideTopBottomLayout();
//                    showLayout();
//                }
//                return true;
//            }
//
//            //长按执行的顺序---1.onDown-->2.onShowPress-->onLongPress
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//
//            //单击后执行onDown接着执行的方法
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return false;
//            }
//
//            //滑动
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                return false;
//            }
//
//            //长按
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_player_pre:{
                playPre();
               break;
            }
            case R.id.btn_player_pause:{
                if(isPlaying){//如果在播放中,暂停
                    videoview.pause();
                    //currentPosition = videoview.getCurrentPosition();
                    btn_player_pause.setBackgroundResource(R.drawable.player_play);//暂停时显示播放按钮
                    removeHideTopBottomLayoutMessage();//暂停时去掉隐藏的消息，不隐藏控制面板
                }else{
                    //videoview.resume();
                    //videoview.seekTo(currentPosition);
                    videoview.start();
                    btn_player_pause.setBackgroundResource(R.drawable.player_pause);
                    handler.sendEmptyMessageDelayed(HIDE_TOPBOTTOMLAYOUT, 5000);//播放状态，5s隐藏控制面板
                }
                isPlaying = !isPlaying;
                break;
            }
            case R.id.btn_player_next:{
                playNext();
                break;
            }
        }
    }

    /**
     * 播放下一个
     */
    public void playNext(){
        if(position<pathList.size()-1){
            position ++;
        }else{
            position = 0;//重从开始播放
        }
        uri = Uri.parse(pathList.get(position));
        videoview.setVideoURI(uri);//如果uri不变循环播放当前文件
        videoview.start();
        //当前文件循环播放
//                videoview.setVideoURI(uri);//如果uri不变循环播放当前文件
//                videoview.start();
    }

    /**
     * 播放前一个
     */
    public void playPre(){
        if(position<=0){
            Toast.makeText(this,"当前已经是第一个文件",Toast.LENGTH_SHORT).show();
        }else{
            position --;uri = Uri.parse(pathList.get(position));
            videoview.setVideoURI(uri);//如果uri不变循环播放当前文件
            videoview.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy =true;
    }


    /***
     * 发送延迟隐藏
     */
    private void sendDelayedHideTopBottomLayout() {
        handler.sendEmptyMessageDelayed(HIDE_TOPBOTTOMLAYOUT, 5000);
    }

    /***
     * 移除延迟隐藏的Handler消息
     */
    private void removeHideTopBottomLayoutMessage() {
        handler.removeMessages(HIDE_TOPBOTTOMLAYOUT);
    }

    /***
     * 显示的方法
     */
    private void showLayout() {
        isShowLayout = true;
        control_player.setVisibility(View.VISIBLE);

    }

    /***
     * 隐藏的方法
     */
    private void hidelayout() {
        isShowLayout = false;
        control_player.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isShowLayout){//目前隐藏状态
            //Toast.makeText(VideoPlayerActivity.this,"点击了",Toast.LENGTH_SHORT).show();
           // removeHideTopBottomLayoutMessage();
            showLayout();
            handler.sendEmptyMessageDelayed(HIDE_TOPBOTTOMLAYOUT, 5000);
        }
        return true;
    }

    /**
     * 启动万能播放器
     */
    public void startVitamioPlayer(){
        Intent intent = new Intent(this,VitamioVideoPlayerActivity.class);
        intent.setData(uri);
        intent.putStringArrayListExtra("pathList",pathList);//按顺序传地址数据集
        //intent.putExtra("listSize",videoItems);//记录传过去，用来做下一视频和上一视频
        intent.putExtra("position",position);//当前播放的视频的位置
        startActivity(intent);
        finish();//关闭当前Activity
    }
}
