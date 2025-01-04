package com.auggie.student_server.entity;

import lombok.Data;

@Data
public class CourseQuery {
    private String cid;        // 课程编号
    private String cname;      // 课程名称
    private String tname;      // 教师姓名
    private String term;
}