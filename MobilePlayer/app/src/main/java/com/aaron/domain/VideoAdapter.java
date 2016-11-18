package com.aaron.domain;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.mobileplayer.R;
import com.aaron.util.Utils;

import java.util.ArrayList;

/**
 * Created by hsl1106 on 2016/11/14.
 */

public class VideoAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<VideoItem> videoItems;


    public VideoAdapter(Context context, ArrayList<VideoItem> videoItems){
        this.videoItems = videoItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
       if(convertView !=null){
            //view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }else{
            //将布局文件实例化成view对象
           convertView = View.inflate(context, R.layout.video_item,null);
            holder = new ViewHolder();
            //查找View的Id很耗时，将查找的到的对象放进一个容器类中
            //holder.imgVideo = (ImageView) view.findViewById(R.id.iv_video);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.duration = (TextView) convertView.findViewById(R.id.tv_videolist_duration);
            holder.size = (TextView) convertView.findViewById(R.id.tv_size);

           convertView.setTag(holder);
        }


        VideoItem videoItem = videoItems.get(position);
        holder.name.setText(videoItem.getName());
        holder.duration.setText(Utils.stringToTime((int)videoItem.getDuration()));
        holder.size.setText(Formatter.formatFileSize(context,videoItem.getSize()));

       /* TextView textView = new TextView(context);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setText(videoItems.get(position).toString());*/
        return convertView;
    }


    public class ViewHolder{//内部类
        ImageView imgVideo;
        TextView name;
        TextView duration;
        TextView size;
    }

}
