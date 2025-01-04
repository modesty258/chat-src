package com.auggie.student_server.entity;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(400);
        result.setMsg(msg);
        return result;
    }

    public static Result unauthorized(String msg) {
        Result result = new Result();
        result.setCode(401);
        result.setMsg(msg);
        return result;
    }
}