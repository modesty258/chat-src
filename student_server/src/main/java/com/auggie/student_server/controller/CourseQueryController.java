package com.auggie.student_server.controller;


import com.auggie.student_server.entity.CourseQuery;
import com.auggie.student_server.service.CourseQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course/query")
@CrossOrigin  // 处理跨域
public class CourseQueryController {

    @Autowired
    private CourseQueryService courseQueryService;

    @PostMapping("/list")
    public List<Map<String, Object>> queryCourseList(@RequestBody CourseQuery queryParams) {
        try {
            return courseQueryService.queryCourseList(queryParams);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}