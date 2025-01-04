package com.auggie.student_server.controller;

import com.auggie.student_server.entity.CourseTeacherInfo;
import com.auggie.student_server.entity.SCTInfo;
import com.auggie.student_server.entity.SCTUpdateRequest;
import com.auggie.student_server.entity.StudentCourseTeacher;
import com.auggie.student_server.service.SCTService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: auggie
 * @Date: 2022/2/10 20:15
 * @Description: SCTcontroller
 * @Version 1.0.0
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/SCT")
public class SCTcontroller {
    @Autowired
    private SCTService sctService;

    @PostMapping("/save")
    public String save(@RequestBody StudentCourseTeacher studentCourseTeacher) {
        if (sctService.isSCTExist(studentCourseTeacher)) {
            return "禁止重复选课";
        }
        System.out.println("正在保存 sct 记录：" + studentCourseTeacher);
        return sctService.save(studentCourseTeacher) ? "选课成功" : "选课失败，联系管理员";
    }

    @GetMapping("/findBySid/{sid}/{term}")
    public List<CourseTeacherInfo> findBySid(@PathVariable Integer sid, @PathVariable String term) {
        return sctService.findBySid(sid, term);
    }

    @GetMapping("/findAllTerm")
    public List<String> findAllTerm() {
        return sctService.findAllTerm();
    }

    @PostMapping("/deleteBySCT")
    public boolean deleteBySCT(@RequestBody StudentCourseTeacher studentCourseTeacher) {
        System.out.println("正在删除 sct 记录：" + studentCourseTeacher);
        return sctService.deleteBySCT(studentCourseTeacher);
    }

    @PostMapping("/findBySearch")
    public List<SCTInfo> findBySearch(@RequestBody Map<String, String> map) {
        return sctService.findBySearch(map);
    }

    @GetMapping("/findById/{sid}/{cid}/{tid}/{term}")
    public SCTInfo findById(@PathVariable Integer sid,
                            @PathVariable Integer cid,
                            @PathVariable Integer tid,
                            @PathVariable String term) {
        return sctService.findByIdWithTerm(sid, cid, tid, term);
    }

    @PostMapping("/updateById")
    public boolean updateById(@RequestBody SCTUpdateRequest updateRequest) {
        // 这里的 updateRequest.getGrade() 现在是一个字符串
        String grade = updateRequest.getGrade();
        // 处理逻辑，例如存储到数据库
        return sctService.updateById(
                updateRequest.getSid(),
                updateRequest.getCid(),
                updateRequest.getTid(),
                updateRequest.getTerm(),
                grade // 直接使用字符串
        );
    }

    @GetMapping("/deleteById/{sid}/{cid}/{tid}/{term}")
    public boolean deleteById(@PathVariable Integer sid,
                              @PathVariable Integer cid,
                              @PathVariable Integer tid,
                              @PathVariable String term) {
        return sctService.deleteById(sid, cid, tid, term);
    }



}
