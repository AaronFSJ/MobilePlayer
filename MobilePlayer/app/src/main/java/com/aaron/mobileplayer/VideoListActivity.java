package com.aaron.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aaron.domain.VideoAdapter;
import com.aaron.domain.VideoItem;

import java.util.ArrayList;

/**
 * Created by fengshj on 2016/11/14.
 */

public class VideoListActivity extends Activity {

    SeekBar s;
    /**
     * 数据列表
     */
    private ListView listView;

    private TextView tvNodata;

    ArrayList<VideoItem> videoItems;//视频数据集合


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(videoItems != null && videoItems.size()>0){
                tvNodata.setVisibility(View.GONE);
                //显示在ListView
                listView.setAdapter(new VideoAdapter(VideoListActivity.this,videoItems));
            }else{
                tvNodata.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        //初始化对象
        initView();
        //获取数据
        getData();
        //点击事件
        setOnListener();
    }

    public void setOnListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前的Item
                VideoItem videoItem = videoItems.get(position);
                //把手机里的所有播放器调起来
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse(videoItem.getData()),"video/*");
//                startActivity(intent);

                //启动自己的播放器
                Intent intent = new Intent(VideoListActivity.this,VideoPlayerActivity.class);
                //传播放地址
                intent.setData(Uri.parse(videoItem.getData()));
                ArrayList<String> pathList = new ArrayList<String>();
                for(VideoItem it:videoItems){
                    pathList.add(it.getData());
                }
                intent.putStringArrayListExtra("pathList",pathList);//按顺序传地址数据集
                //intent.putExtra("listSize",videoItems);//记录传过去，用来做下一视频和上一视频
                intent.putExtra("position",position);//当前播放的视频的位置
                startActivity(intent);
            }
        });
    }


    /**
     * 初始化数据
     */
    public void initView(){
        listView = (ListView) findViewById(R.id.lv_vediolist);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
    }

    public void getData(){
        //加载数据不能放在主线程，所以要创建子线程
        new Thread(){
            @Override
            public void run() {
                videoItems = new ArrayList<VideoItem>();
                //读取手机里的所有视频,sd卡下的目录
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                //显示的数据
                String[] projection={
                        MediaStore.Video.Media.DISPLAY_NAME,//名字
                        MediaStore.Video.Media.DURATION,//时长
                        MediaStore.Video.Media.SIZE,//大小
                        MediaStore.Video.Media.DATA//地址
                };
                //读取手里机的视频
                Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
                while(cursor.moveToNext()){
                    String name = cursor.getString(0);
                    Long duration = cursor.getLong(1);
                    Long size = cursor.getLong(2);
                    String data = cursor.getString(3);

                    VideoItem item = new VideoItem();
                    item.setName(name);
                    item.setDuration(duration );
                    item.setSize(size);
                    item.setData(data);
                    //将每一个item数据放到数据集合中
                    videoItems.add(item);
                }
                cursor.close();
                //子线程数据加载完毕后应该在UI线程中显示，要通知UI线程，就要用Handler
                handler.sendEmptyMessage(1);
            }
        }.start();
    }
}
