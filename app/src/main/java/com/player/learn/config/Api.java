package com.player.learn.config;

public class Api {
    public static final String HOST = "http://192.168.0.102:6000";
    //查询所有大分类
    public static final String FINDALLBYCLASSIFYGROUP = HOST + "/service/learn/findAllByClassifyGroup";

    //根据大分类查询课程
    public static final String FINDALLBYCLASSIFY = HOST +"/service/learn/findAllByClassify";

    //根据类别查询课程
    public static final String FINDALLBYCATEGORY = HOST +"/service/learn/findAllByCategory";

    //根据大分类查询课程
    public static final String FINDALLBYCOURSENAME = HOST +"/service/learn/findAllByCourseName";

    //登录校验
    public static final String LOGIN = HOST +"/service/learn/login";

    //查询用户信息
    public static final String GETUSERDATA = HOST +"/service/learn/getUserData";

    //保存课程日志信息
    public static final String SAVECOURSELOG = HOST +"/service/learn/saveCourseLog";

    //保存观看记录
    public static final String SAVECHAPTERLOG = HOST +"/service/learn/saveChapterLog";
}
