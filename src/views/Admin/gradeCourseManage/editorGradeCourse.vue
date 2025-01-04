<template>
  <div>
    <el-card>
      <el-form style="width: 60%" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="课程名称" prop="cname">
          <el-input v-model="ruleForm.cname" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="教师名" prop="tname">
          <el-input v-model="ruleForm.tname" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="学生名" prop="sname">
          <el-input v-model="ruleForm.sname" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="分数" prop="grade">
          <el-input type="textarea" v-model="ruleForm.grade" :rows="4" placeholder="请输入成绩（可以包含 HTML）"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
          <el-button @click="test">test</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    var checkGrade = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('成绩不能为空'));
      }
      // 这里可以根据需要添加更多的 HTML 验证
      callback();
    };
    return {
      ruleForm: {
        cid: null,
        cname: null,
        grade: '',
        sid: null,
        sname: null,
        tid: null,
        tname: null,
      },
      rules: {
        grade: [
          {required: true, message: '请输入学分', trigger: 'change'},
          {validator: checkGrade, trigger: 'blur'}
        ],
      }
    };
  },
  created() {
    const that = this;
    this.ruleForm.cid = this.$route.query.cid;
    this.ruleForm.tid = this.$route.query.tid;
    this.ruleForm.sid = this.$route.query.sid;
    this.ruleForm.term = this.$route.query.term;

    axios.get(`http://localhost:10086/SCT/findById/${this.ruleForm.sid}/${this.ruleForm.cid}/${this.ruleForm.tid}/${this.ruleForm.term}`)
        .then(function (resp) {
          that.ruleForm = resp.data;
        })
        .catch(function (error) {
          console.error('获取数据失败:', error);
        });
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const that = this;
          const { sid, cid, tid, term, grade } = that.ruleForm;

          // 构建请求体
          const requestBody = {
            sid: sid,
            cid: cid,
            tid: tid,
            term: term,
            grade: grade
          };

          console.log('请求体:', requestBody); // 打印请求体

          // 发送 POST 请求
          axios.post('http://localhost:10086/SCT/updateById', requestBody)
              .then(function (resp) {
                console.log('响应:', resp); // 打印响应
                if (resp.data === true) {
                  that.$message({
                    showClose: true,
                    message: '编辑成功',
                    type: 'success'
                  });
                } else {
                  that.$message.error('编辑失败，请检查数据库');
                }

                if (sessionStorage.getItem('type') === 'admin') {
                  that.$router.push("/queryGradeCourse");
                } else {
                  that.$router.push("/teacherQueryGradeCourseManage");
                }
              })
              .catch(function (error) {
                console.error('提交失败:', error);
                that.$message.error('提交失败，请检查网络或服务器');
              });
        } else {
          console.log('表单验证失败');
          return false;
        }
      });
    }
  }
}
</script>

<style>
/* 如果需要，可以在此处添加样式 */
</style>