package com.auggie.student_server.mapper;

import com.auggie.student_server.entity.CourseQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseQueryMapper {
    @Select("SELECT DISTINCT c.cid, c.cname, ct.tid, t.tname, ct.term " +  // 添加term，使用DISTINCT避免重复
            "FROM c c " +
            "LEFT JOIN ct ct ON c.cid = ct.cid " +
            "LEFT JOIN t t ON ct.tid = t.tid " +
            "WHERE ct.term = #{term} " +  // 添加学期条件
            "AND (#{cid} IS NULL OR c.cid LIKE CONCAT('%', #{cid}, '%')) " +  // 修改LIKE语句
            "AND (#{cname} IS NULL OR c.cname LIKE CONCAT('%', #{cname}, '%')) " +
            "AND (#{tname} IS NULL OR t.tname LIKE CONCAT('%', #{tname}, '%'))")
    List<Map<String, Object>> selectCourseList(CourseQuery queryParams);
}