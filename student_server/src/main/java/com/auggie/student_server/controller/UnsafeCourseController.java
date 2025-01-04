package com.auggie.student_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/unsafe")
@CrossOrigin
public class UnsafeCourseController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/query")
    public List<Map<String, Object>> unsafeQuery(@RequestBody Map<String, String> params) {
        List<Map<String, Object>> result = new ArrayList<>();

        String tname = params.get("tname");
        System.out.println("提取的tname: " + tname);

        // 修改SQL语句结构，使其更容易注入
        String sql = "SELECT DISTINCT c.cid, c.cname, ct.tid, t.tname, ct.term " +
                "FROM c c, ct ct, t t " +
                "WHERE c.cid = ct.cid " +
                "AND ct.tid = t.tid " +
                "AND t.tname = '" + tname + "'";  // 改用 = 而不是 LIKE

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("执行的SQL: " + sql);

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
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("执行的SQL: " + sql);
        }

        return result;
    }
}