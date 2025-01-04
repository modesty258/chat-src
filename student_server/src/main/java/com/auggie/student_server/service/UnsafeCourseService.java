package com.auggie.student_server.service;  // 根据你的包名修改

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class UnsafeCourseService {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> unsafeQuery(String cname, String tname, String term) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 修改SQL拼接方式
        String sql = "SELECT DISTINCT c.cid, c.cname, ct.tid, t.tname, ct.term " +
                "FROM c c " +
                "LEFT JOIN ct ct ON c.cid = ct.cid " +
                "LEFT JOIN t t ON ct.tid = t.tid " +
                "WHERE 1=1 ";  // 使用1=1作为基础条件

        // 分别添加条件
        if (term != null) {
            sql += "AND ct.term = '" + term + "' ";
        }

        if (cname != null) {
            sql += "AND c.cname LIKE '%" + cname + "%' ";
        }

        if (tname != null) {
            sql += "AND t.tname LIKE '%" + tname + "%' ";
        }

        // 打印SQL语句以便调试
        System.out.println("Executing SQL: " + sql);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("cid", rs.getString("cid"));
                row.put("cname", rs.getString("cname"));
                row.put("tid", rs.getString("tid"));
                row.put("tname", rs.getString("tname"));
                row.put("term", rs.getString("term"));
                result.add(row);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("Attempted SQL: " + sql);
        }

        return result;
    }
}