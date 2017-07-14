package com.chinanetcenter.api.entity;

/**
 * Created by fuyz on 2015/7/29.
 */
public class Stream {

    private String avg_frame_rate; //平均帧率
    private String bit_rate;  //码率
    private String codec_long_name;  //编码器名全称
    private String codec_name;  //编码器名
    private String codec_tag;  //编码器标签
    private String codec_tag_string; //编码器标签名
    private String codec_time_base;  //编码器每帧时长
    private String codec_type;  //编码器类型
    private String display_aspect_ratio;  //显示长宽比
    private Disposition disposition;  //处理信息
    private String duration;  //文件总时间
    private String duration_ts;  //帧总时间
    private long has_b_frames;  //记录帧缓存大小
    private long height;  //高度
    private String index;  //流索引号
    private int level;  //级别
    private long nb_frames;  //帧数
    private String pix_fmt;  //像素个数
    private String profile;  //配置信息
    private String r_frame_rate;  //真实基础帧率
    private String sample_aspect_ratio;  //采样率
    private String start_pts;  //起始时间
    private String start_time;  //首帧时间
    private Tag tag;  //标签
    private long timeBase;
    private long width;

    public String getAvg_frame_rate() {
        return avg_frame_rate;
    }

    public void setAvg_frame_rate(String avg_frame_rate) {
        this.avg_frame_rate = avg_frame_rate;
    }

    public String getBit_rate() {
        return bit_rate;
    }

    public void setBit_rate(String bit_rate) {
        this.bit_rate = bit_rate;
    }

    public String getCodec_long_name() {
        return codec_long_name;
    }

    public void setCodec_long_name(String codec_long_name) {
        this.codec_long_name = codec_long_name;
    }

    public String getCodec_name() {
        return codec_name;
    }

    public void setCodec_name(String codec_name) {
        this.codec_name = codec_name;
    }

    public String getCodec_tag() {
        return codec_tag;
    }

    public void setCodec_tag(String codec_tag) {
        this.codec_tag = codec_tag;
    }

    public String getCodec_tag_string() {
        return codec_tag_string;
    }

    public void setCodec_tag_string(String codec_tag_string) {
        this.codec_tag_string = codec_tag_string;
    }

    public String getCodec_time_base() {
        return codec_time_base;
    }

    public void setCodec_time_base(String codec_time_base) {
        this.codec_time_base = codec_time_base;
    }

    public String getCodec_type() {
        return codec_type;
    }

    public void setCodec_type(String codec_type) {
        this.codec_type = codec_type;
    }

    public String getDisplay_aspect_ratio() {
        return display_aspect_ratio;
    }

    public void setDisplay_aspect_ratio(String display_aspect_ratio) {
        this.display_aspect_ratio = display_aspect_ratio;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration_ts() {
        return duration_ts;
    }

    public void setDuration_ts(String duration_ts) {
        this.duration_ts = duration_ts;
    }

    public long getHas_b_frames() {
        return has_b_frames;
    }

    public void setHas_b_frames(long has_b_frames) {
        this.has_b_frames = has_b_frames;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getNb_frames() {
        return nb_frames;
    }

    public void setNb_frames(long nb_frames) {
        this.nb_frames = nb_frames;
    }

    public String getPix_fmt() {
        return pix_fmt;
    }

    public void setPix_fmt(String pix_fmt) {
        this.pix_fmt = pix_fmt;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getR_frame_rate() {
        return r_frame_rate;
    }

    public void setR_frame_rate(String r_frame_rate) {
        this.r_frame_rate = r_frame_rate;
    }

    public String getSample_aspect_ratio() {
        return sample_aspect_ratio;
    }

    public void setSample_aspect_ratio(String sample_aspect_ratio) {
        this.sample_aspect_ratio = sample_aspect_ratio;
    }

    public String getStart_pts() {
        return start_pts;
    }

    public void setStart_pts(String start_pts) {
        this.start_pts = start_pts;
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

    public long getTimeBase() {
        return timeBase;
    }

    public void setTimeBase(long timeBase) {
        this.timeBase = timeBase;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    class Tag{
        public Tag(){

        }
        private String creation_time; //创建时间
        private String encoder;  //编码器
        private String handler_name;  //处理器名字
        private String language; //语言

        public String getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(String creation_time) {
            this.creation_time = creation_time;
        }

        public String getEncoder() {
            return encoder;
        }

        public void setEncoder(String encoder) {
            this.encoder = encoder;
        }

        public String getHandler_name() {
            return handler_name;
        }

        public void setHandler_name(String handler_name) {
            this.handler_name = handler_name;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    class Disposition{
        public Disposition(){

        }
        private int attached_pic;
        private int clean_effects;
        private int comment;
        private int defaultValue;
        private int dub;
        private int forced;
        private int hearing_impaired;
        private int karaoke;
        private int lyrics;
        private int original;
        private int visual_impaired;

        public int getAttached_pic() {
            return attached_pic;
        }

        public void setAttached_pic(int attached_pic) {
            this.attached_pic = attached_pic;
        }

        public int getClean_effects() {
            return clean_effects;
        }

        public void setClean_effects(int clean_effects) {
            this.clean_effects = clean_effects;
        }

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getDub() {
            return dub;
        }

        public void setDub(int dub) {
            this.dub = dub;
        }

        public int getForced() {
            return forced;
        }

        public void setForced(int forced) {
            this.forced = forced;
        }

        public int getHearing_impaired() {
            return hearing_impaired;
        }

        public void setHearing_impaired(int hearing_impaired) {
            this.hearing_impaired = hearing_impaired;
        }

        public int getKaraoke() {
            return karaoke;
        }

        public void setKaraoke(int karaoke) {
            this.karaoke = karaoke;
        }

        public int getLyrics() {
            return lyrics;
        }

        public void setLyrics(int lyrics) {
            this.lyrics = lyrics;
        }

        public int getOriginal() {
            return original;
        }

        public void setOriginal(int original) {
            this.original = original;
        }

        public int getVisual_impaired() {
            return visual_impaired;
        }

        public void setVisual_impaired(int visual_impaired) {
            this.visual_impaired = visual_impaired;
        }

        public int getDefault() {
            return defaultValue;
        }

        public void setDefault(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
