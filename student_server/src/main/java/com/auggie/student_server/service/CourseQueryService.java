package com.auggie.student_server.service;

import com.auggie.student_server.entity.CourseQuery;
import com.auggie.student_server.mapper.CourseQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseQueryService {

    @Autowired
    private CourseQueryMapper courseQueryMapper;

    public List<Map<String, Object>> queryCourseList(CourseQuery queryParams) {
        try {
            return courseQueryMapper.selectCourseList(queryParams);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}