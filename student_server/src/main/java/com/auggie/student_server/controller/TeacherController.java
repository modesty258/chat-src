package com.auggie.student_server.controller;

import com.auggie.student_server.entity.Result;
import com.auggie.student_server.entity.Student;
import com.auggie.student_server.entity.Teacher;
import com.auggie.student_server.service.TeacherService;
import com.auggie.student_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.auggie.student_server.security.JwtUtil;
/**
 * @Auther: auggie
 * @Date: 2022/2/9 11:02
 * @Description: TeacherController
 * @Version 1.0.0
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private JwtUtil jwtUtil;


    // 添加这个
    @Autowired
    private UserService userService;
    @PostMapping("/addTeacher")
    public boolean addTeacher(@RequestBody Teacher teacher) {
        return teacherService.save(teacher);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Teacher teacher) {
        System.out.println("正在验证教师登陆 " + teacher);

        try {
            // 1. 参数验证
            if (teacher == null || teacher.getTid() == null || teacher.getPassword() == null) {
                return Result.error("用户名和密码不能为空");
            }

            // 2. 查找教师
            Teacher t = teacherService.findById(teacher.getTid());
            if (t == null || !t.getPassword().equals(teacher.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            // 3. 使用 UserService 生成 token
            String token = userService.generateUserToken(
                    t.getTid().toString(),
                    t.getTname().equals("admin") ? "admin" : "teacher"
            );

            // 4. 构建返回数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            t.setPassword(null);  // 移除敏感信息
            resultMap.put("teacher", t);

            return Result.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }



    @GetMapping("/findById/{tid}")
    public Teacher findById(@PathVariable("tid") Integer tid) {
        System.out.println("正在查询学生信息 By id " + tid);
        return teacherService.findById(tid);
    }

    @PostMapping("/findBySearch")
    public List<Teacher> findBySearch(@RequestBody Map<String, String> map) {
        return teacherService.findBySearch(map);
    }

    @GetMapping("/deleteById/{tid}")
    public boolean deleteById(@PathVariable("tid") int tid) {
        System.out.println("正在删除学生 tid：" + tid);
        return teacherService.deleteById(tid);
    }

    @PostMapping("/updateTeacher")
    public boolean updateTeacher(@RequestBody Teacher teacher) {
        System.out.println("更新 " + teacher);
        return teacherService.updateById(teacher);
    }
}
