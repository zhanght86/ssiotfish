package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class TaskCenterModel implements Serializable {
    public int _id;
    public int _userid;
    public String _tousers;
    public String _contenttext;
    public Timestamp _createtime;
    public Timestamp _starttime;
    public Timestamp _endtime;
    public String _img;
    public boolean _needimg;
    public boolean _needlocation;
    public int _state;//新建1 进行中2 完成3
}