package com.auggie.student_server.entity;

public class SCTUpdateRequest {
    private Integer sid;
    private Integer cid;
    private Integer tid;
    private String term;
    private String grade; // 改为 String 类型

    // Getter 和 Setter 方法


    // Getter 和 Setter 方法
    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}