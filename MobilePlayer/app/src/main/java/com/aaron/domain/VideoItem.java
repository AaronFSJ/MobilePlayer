package com.aaron.domain;

/**
 * Created by hsl1106 on 2016/11/14.
 */

public class VideoItem {

    /**
     * 视频名称
     */
    private String name;
    /**
     * 时长
     */
    private long duration;
    /**
     * 大小
     */
    private long size;
    /**
     * 视频地址
     */
    private String data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}
