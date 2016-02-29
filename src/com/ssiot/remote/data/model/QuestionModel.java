package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class QuestionModel implements Serializable{
    public int _id;
    public int _userId;
    public String _title;
    public String _contentText;
    public Timestamp _createTime;
    public String _picUrls;
    public String _addr;
    public float _longitude;
    public float _latitude;
    public int _replyCount;
}