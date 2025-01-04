<template>
  <div>
    <el-container>
      <el-header>
        <div style="text-align: center; font-size: 25px; font-weight: bolder">
          <i class="el-icon-s-home" style="margin-right: 25px"></i>
          选课管理系统
        </div>
      </el-header>
      <el-main>
        <el-card class="login-module">
          <div slot="header" class="clearfix">
            <span style="text-align: center; font-size: 20px; font-family: 'Microsoft YaHei'">
              <p><i class="el-icon-office-building" style="margin-right: 18px"></i>登陆</p>
            </span>
          </div>
          <div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
              <el-form-item label="用户 id" prop="id">
                <el-input v-model.number="ruleForm.id" prefix-icon="el-icon-lollipop"></el-input>
              </el-form-item>
              <el-form-item label="用户密码" prop="password">
                <el-input v-model="ruleForm.password" placeholder="请输入密码" show-password prefix-icon="el-icon-ice-cream-round"></el-input>
              </el-form-item>
              <el-form-item label="用户类型" prop="type">
                <el-radio-group v-model="ruleForm.type">
                  <el-radio label="student" value="student">学生</el-radio>
                  <el-radio label="teacher" value="teacher">老师</el-radio>
                  <el-radio label="admin" value="admin">admin</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm('ruleForm')">登陆</el-button>
                <el-button @click="resetForm('ruleForm')">重置</el-button>
                <el-button @click="test('ruleForm')">test</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Login',

  data() {
    return {
      loading: false,
      ruleForm: {
        id: null,
        password: null,
        type: 'student'
      },
      rules: {
        id: [
          { required: true, message: '请输入用户 id', trigger: 'blur' },
          { type: 'number', message: '请输入数字', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ]
      }
    };
  },

  created() {
    // 清除之前的登录信息
    sessionStorage.clear();
  },

  methods: {
    handleEnterKey() {
      if (!this.loading) {
        this.submitForm('ruleForm');
      }
    },

    async submitForm(formName) {
      try {
        const valid = await new Promise(resolve => {
          this.$refs[formName].validate(resolve);
        });

        if (!valid) {
          return;
        }

        this.loading = true;

        const loginUrl = this.ruleForm.type === 'student' ? '/student/login' : '/teacher/login';
        const loginData = {
          [this.ruleForm.type === 'student' ? 'sid' : 'tid']: this.ruleForm.id,
          password: this.ruleForm.password
        };

        const loginResp = await axios.post(`http://localhost:10086${loginUrl}`, loginData);

        if (loginResp.data.code === 200) {
          await this.handleLoginSuccess(loginResp.data.data);
        } else {
          throw new Error(loginResp.data.msg || '登录失败');
        }
      } catch (error) {
        console.error('登录错误:', error);
        this.$message.error(
            error.response?.data?.msg || error.message || '登录失败，请检查账号密码'
        );
      } finally {
        this.loading = false;
      }
    },

    async handleLoginSuccess(data) {
      const { token, student, teacher } = data;

      // 清除旧数据
      sessionStorage.clear();

      // 设置登录标记（与路由守卫匹配）
      sessionStorage.setItem('token', 'true');
      sessionStorage.setItem('type', this.ruleForm.type);

      // 根据用户类型处理
      if (this.ruleForm.type === 'student') {
        sessionStorage.setItem('name', student.sname);
        sessionStorage.setItem('sid', student.sid);
        this.$message.success(`登录成功，欢迎 ${student.sname}!`);
        await this.$router.push('/student');
      } else {
        sessionStorage.setItem('name', teacher.tname);
        sessionStorage.setItem('tid', teacher.tid);

        if (this.ruleForm.type === 'admin' && teacher.tname === 'admin') {
          this.$message.success(`登录成功，欢迎 ${teacher.tname}!`);
          await this.$router.push('/admin');
        } else if (this.ruleForm.type === 'teacher' && teacher.tname !== 'admin') {
          this.$message.success(`登录成功，欢迎 ${teacher.tname}!`);
          await this.$router.push('/teacher');
        } else {
          throw new Error('用户类型与账号不匹配');
        }
      }

      // 存储 JWT token
      sessionStorage.setItem('jwt', token);

      // 加载学期和选课状态
      this.loadTermAndSelectionStatus();
    },

    loadTermAndSelectionStatus() {
      // 获取当前学期
      axios.get('http://localhost:10086/info/getCurrentTerm')
          .then(function (resp) {
            sessionStorage.setItem("currentTerm", resp.data);
          })
          .catch(function (error) {
            console.warn('获取学期信息失败:', error);
          });

      // 获取选课状态
      axios.get('http://localhost:10086/info/getForbidCourseSelection')
          .then(function (resp) {
            sessionStorage.setItem("ForbidCourseSelection", resp.data);
          })
          .catch(function (error) {
            console.warn('获取选课状态失败:', error);
          });
    },

    resetForm(formName) {
      this.$refs[formName].resetFields();
    },

    test(formName) {
      console.log(this.ruleForm);
    }
  }
};
</script>

<style>
.login-module {
  margin-top: 60px;
  position: absolute;
  right: 500px;
  text-align: center;
  width: 30%;
}
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}
</style>