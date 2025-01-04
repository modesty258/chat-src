package com.auggie.student_server.controller;

import com.auggie.student_server.entity.Result;
import com.auggie.student_server.entity.Student;
import com.auggie.student_server.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.auggie.student_server.security.JwtUtil;
import com.auggie.student_server.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: auggie
 * @Date: 2022/2/8 17:37
 * @Description: StudentController
 * @Version 1.0.0
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @PostMapping("/addStudent")
    public boolean addStudent(@RequestBody Student student) {
        System.out.println("正在保存学生对象" + student);
        return studentService.save(student);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Student student) {
        System.out.println("正在验证学生登陆 " + student);

        try {
            // 1. 参数验证
            if (student == null || student.getSid() == null || student.getPassword() == null) {
                return Result.error("用户名和密码不能为空");
            }

            // 2. 查找学生
            Student s = studentService.findById(student.getSid());
            if (s == null || !s.getPassword().equals(student.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            // 3. 使用 UserService 生成 token
            String token = userService.generateUserToken(
                    s.getSid().toString(),
                    "student"
            );

            // 4. 构建返回数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            s.setPassword(null);  // 移除敏感信息
            resultMap.put("student", s);

            return Result.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }


    @PostMapping("/findBySearch")
    public List<Student> findBySearch(@RequestBody Student student) {
        Integer fuzzy = (student.getPassword() == null) ? 0 : 1;
        return studentService.findBySearch(student.getSid(), student.getSname(), fuzzy);
    }

    @GetMapping("/findById/{sid}")
    public Student findById(@PathVariable("sid") Integer sid) {
        System.out.println("正在查询学生信息 By id " + sid);
        return studentService.findById(sid);
    }

    @GetMapping("/findByPage/{page}/{size}")
    public List<Student> findByPage(@PathVariable("page") int page, @PathVariable("size") int size) {
        System.out.println("查询学生列表分页 " + page + " " + size);
        return studentService.findByPage(page, size);
    }

    @GetMapping("/getLength")
    public Integer getLength() {
        return studentService.getLength();
    }

    @GetMapping("/deleteById/{sid}")
    public boolean deleteById(@PathVariable("sid") int sid) {
        System.out.println("正在删除学生 sid：" + sid);
        return studentService.deleteById(sid);
    }

    @PostMapping("/updateStudent")
    public boolean updateStudent(@RequestBody Student student) {
        System.out.println("更新 " + student);
        return studentService.updateById(student);
    }
}
