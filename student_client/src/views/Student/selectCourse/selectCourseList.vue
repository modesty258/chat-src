<template>
  <div>
    <!-- 查询表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="课程编号">
        <el-input v-model="searchForm.cid" placeholder="输入课程编号"></el-input>
      </el-form-item>
      <el-form-item label="课程名称">
        <el-input v-model="searchForm.cname" placeholder="输入课程名称"></el-input>
      </el-form-item>
      <el-form-item label="教师姓名">
        <el-input v-model="searchForm.tname" placeholder="输入教师姓名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="doSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格部分 -->
    <el-table
        :data="tableData"
        border
        show-header
        stripe
        style="width: 100%">
      <el-table-column fixed prop="cid" label="课号" width="150"></el-table-column>
      <el-table-column prop="cname" label="课程号" width="150"></el-table-column>
      <el-table-column prop="tid" label="教师号" width="150"></el-table-column>
      <el-table-column prop="tname" label="教师名称" width="150"></el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-popconfirm
              confirm-button-text='选择'
              cancel-button-text='取消'
              icon="el-icon-info"
              title="确定选择该教师开设的课程？"
              @confirm="select(scope.row)"
          >
            <el-button slot="reference" type="text" size="small">选择</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="changePage"
    >
    </el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        cid: '',
        cname: '',
        tname: '',
        term: sessionStorage.getItem('currentTerm')
      },
      tableData: null,
      pageSize: 10,
      total: null,
      tmpList: null,
      type: sessionStorage.getItem('type')
    }
  },

  props: {
    ruleForm: Object
  },

  methods: {
    doSearch() {
      const that = this
      console.log('开始查询，参数：', this.searchForm)

      axios.post("http://localhost:10086/unsafe/query", this.searchForm)
          .then(function(resp) {
            console.log('查询成功：', resp.data)
            that.tmpList = resp.data
            that.total = resp.data.length
            let start = 0, end = that.pageSize
            let length = that.tmpList.length
            let ans = (end < length) ? end : length
            that.tableData = that.tmpList.slice(start, ans)
          })
          .catch(function(error) {
            console.error('查询失败，详细错误：', error)
            console.error('请求状态：', error.response ? error.response.status : '无响应')
            that.$message({
              showClose: true,
              message: '查询失败：' + (error.response ? error.response.status : '网络连接错误'),
              type: 'error'
            })
          })
    },

    resetSearch() {
      this.searchForm.cid = ''
      this.searchForm.cname = ''
      this.searchForm.tname = ''
      this.loadOriginalData()
    },

    loadOriginalData() {
      const that = this
      that.tmpList = null
      that.total = null
      that.tableData = null
      axios.post("http://localhost:10086/courseTeacher/findCourseTeacherInfo", that.ruleForm)
          .then(function(resp) {
            that.tmpList = resp.data
            that.total = resp.data.length
            let start = 0, end = that.pageSize
            let length = that.tmpList.length
            let ans = (end < length) ? end : length
            that.tableData = that.tmpList.slice(start, ans)
          })
    },

    select(row) {
      console.log(row)
      const cid = row.cid
      const tid = row.tid
      const sid = sessionStorage.getItem('sid')
      const term = sessionStorage.getItem('currentTerm')
      const sct = {
        cid: cid,
        tid: tid,
        sid: sid,
        term: term
      }
      const that = this
      axios.post('http://localhost:10086/SCT/save', sct).then(function(resp) {
        if (resp.data === '选课成功') {
          that.$message({
            showClose: true,
            message: '选课成功',
            type: 'success'
          });
        } else {
          that.$message({
            showClose: true,
            message: resp.data,
            type: 'error'
          });
        }
      })
    },

    changePage(page) {
      page = page - 1
      const that = this
      let start = page * that.pageSize, end = that.pageSize * (page + 1)
      let length = that.tmpList.length
      let ans = (end < length) ? end : length
      that.tableData = that.tmpList.slice(start, ans)
    }
  },

  watch: {
    ruleForm: {
      handler(newRuleForm, oldRuleForm) {
        this.loadOriginalData()
      },
      deep: true,
      immediate: true
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>