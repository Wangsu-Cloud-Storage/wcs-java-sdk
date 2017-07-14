package com.chinanetcenter.api.entity;

/**
 * Created by fuyz on 2015/7/29.
 */
public class Format {
    private String bit_rate;  //码率
    private String duration;  //时长
    private String format_long_name;  //文件名全称
    private String format_name; //文件名
    private String nb_programs;  //程序集的数目
    private int nb_streams;  //流的数目
    private String probe_score; //格式探测得分
    private long size;  //文件大小
    private String start_time;  //首帧时间
    private Tag tag;  //标签信息

    public String getBit_rate() {
        return bit_rate;
    }

    public void setBit_rate(String bit_rate) {
        this.bit_rate = bit_rate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFormat_long_name() {
        return format_long_name;
    }

    public void setFormat_long_name(String format_long_name) {
        this.format_long_name = format_long_name;
    }

    public String getFormat_name() {
        return format_name;
    }

    public void setFormat_name(String format_name) {
        this.format_name = format_name;
    }

    public String getNb_programs() {
        return nb_programs;
    }

    public void setNb_programs(String nb_programs) {
        this.nb_programs = nb_programs;
    }

    public int getNb_streams() {
        return nb_streams;
    }

    public void setNb_streams(int nb_streams) {
        this.nb_streams = nb_streams;
    }

    public String getProbe_score() {
        return probe_score;
    }

    public void setProbe_score(String probe_score) {
        this.probe_score = probe_score;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    class Tag {
        public Tag(){

        }
        private String compatible_brands; //兼容性品牌
        private String creation_time;  //创建时间
        private String major_brand;  //主品牌
        private String minor_version; //次要版本

        public String getCompatible_brands() {
            return compatible_brands;
        }

        public void setCompatible_brands(String compatible_brands) {
            this.compatible_brands = compatible_brands;
        }

        public String getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(String creation_time) {
            this.creation_time = creation_time;
        }

        public String getMajor_brand() {
            return major_brand;
        }

        public void setMajor_brand(String major_brand) {
            this.major_brand = major_brand;
        }

        public String getMinor_version() {
            return minor_version;
        }

        public void setMinor_version(String minor_version) {
            this.minor_version = minor_version;
        }
    }
}
