<template>
  <div>
    <el-form>
      <el-form-item label="选择学期">
        <el-select v-model="term" placeholder="请选择学期" style="width: 200px">
          <el-option
              v-for="(item, index) in termList"
              :key="index"
              :label="item"
              :value="item">
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <el-card>
      <el-table
          :data="tableData"
          border
          style="width: 100%"
          v-loading="loading">
        <el-table-column
            fixed
            prop="cid"
            label="课号"
            width="150">
        </el-table-column>
        <el-table-column
            prop="cname"
            label="课程名称"
            width="200">
        </el-table-column>
        <el-table-column
            prop="tid"
            label="教师号"
            width="150">
        </el-table-column>
        <el-table-column
            prop="tname"
            label="教师名称"
            width="150">
        </el-table-column>
        <el-table-column
            prop="ccredit"
            label="学分"
            width="100">
        </el-table-column>
        <el-table-column
            label="成绩"
            width="150">
          <template slot-scope="scope">
            <div v-html="decodeGrade(scope.row.grade)"></div>
          </template>
        </el-table-column>
      </el-table>

      <div class="statistics-info" style="margin-top: 20px">
        <el-alert
            v-if="tableData.length > 0"
            :title="`平均成绩：${avg.toFixed(2)}`"
            type="success"
            :closable="false">
        </el-alert>
        <el-alert
            v-else
            title="暂无成绩数据"
            type="info"
            :closable="false">
        </el-alert>
      </div>

      <div class="pagination-container" style="margin-top: 20px; text-align: center">
        <el-pagination
            background
            layout="prev, pager, next, total"
            :total="total"
            :page-size="pageSize"
            :current-page.sync="currentPage"
            @current-change="handlePageChange">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'StudentGrades',
  data() {
    return {
      tableData: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      tmpList: [],
      avg: 0,
      term: '',
      termList: [],
      loading: false
    };
  },

  created() {
    this.term = sessionStorage.getItem('currentTerm') || '';
    this.loadTerms();
  },

  watch: {
    term: {
      handler(newTerm) {
        if (newTerm) {
          this.currentPage = 1;
          this.loadGrades(newTerm);
        }
      },
      immediate: true
    }
  },

  methods: {
    // 加载学期列表
    async loadTerms() {
      try {
        this.loading = true;
        const response = await axios.get('http://localhost:10086/SCT/findAllTerm');
        this.termList = response.data;
        if (!this.term && this.termList.length > 0) {
          this.term = this.termList[0];
        }
      } catch (error) {
        this.$message.error('加载学期列表失败: ' + error.message);
        console.error('Failed to load terms:', error);
      } finally {
        this.loading = false;
      }
    },

    // 加载成绩数据
    async loadGrades(term) {
      try {
        this.loading = true;
        const sid = sessionStorage.getItem('sid');
        if (!sid) {
          this.$message.error('未找到学生信息，请重新登录');
          return;
        }

        const response = await axios.get(`http://localhost:10086/SCT/findBySid/${sid}/${term}`);
        this.tmpList = response.data;
        this.total = this.tmpList.length;
        this.updateTableData(this.currentPage);
        this.calculateAverage();
      } catch (error) {
        this.$message.error('加载成绩失败: ' + error.message);
        console.error('Failed to load grades:', error);
      } finally {
        this.loading = false;
      }
    },

    // 更新表格数据
    updateTableData(page) {
      const start = (page - 1) * this.pageSize;
      const end = page * this.pageSize;
      this.tableData = this.tmpList.slice(start, end);
    },

    // 计算平均分
    calculateAverage() {
      let totalScore = 0;
      let totalCredits = 0;

      this.tmpList.forEach(item => {
        // 提取纯数字成绩
        const numericGrade = this.extractNumericGrade(item.grade);
        if (!isNaN(numericGrade)) {
          totalCredits += item.ccredit;
          totalScore += item.ccredit * numericGrade;
        }
      });

      this.avg = totalCredits ? (totalScore / totalCredits) : 0;
    },

    // 处理页面改变
    handlePageChange(page) {
      this.currentPage = page;
      this.updateTableData(page);
    },

    // 解码成绩中的HTML内容
    decodeGrade(grade) {
      try {
        return decodeURIComponent(grade);
      } catch (e) {
        return grade;
      }
    },

    // 提取成绩中的数字
    extractNumericGrade(grade) {
      if (!grade) return NaN;

      // 移除HTML标签，只保留文本内容
      const plainText = grade.replace(/<[^>]*>/g, '');

      // 提取数字
      const match = plainText.match(/\d+/);
      return match ? parseFloat(match[0]) : NaN;
    }
  }
};
</script>

<style scoped>
.statistics-info {
  margin: 20px 0;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

/* 添加一些响应式样式 */
@media screen and (max-width: 768px) {
  .el-select {
    width: 100% !important;
  }
}
</style>