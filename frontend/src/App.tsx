import { Layout, Menu } from 'antd'
import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import LoginPage from './pages/Login'
import StudentGrades from './pages/StudentGrades'
import ImportGrades from './pages/ImportGrades'
import AdminStudents from './pages/admin/Students'
import AdminTeachers from './pages/admin/Teachers'
import AdminCourses from './pages/admin/Courses'
import TimePeriods from './pages/admin/TimePeriods'
import { isAuthed, hasRole, clearToken } from './store/auth'
import React from 'react'

const { Header, Content } = Layout

export default function App() {
  const [authVersion, setAuthVersion] = React.useState(0)
  React.useEffect(() => {
    const handler = () => setAuthVersion(v => v + 1)
    window.addEventListener('auth-changed', handler)
    return () => window.removeEventListener('auth-changed', handler)
  }, [])
  const authed = isAuthed()
  const admin = hasRole(['ADMIN'])
  const teacher = hasRole(['ADMIN','TEACHER'])
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header>
        <Menu theme="dark" mode="horizontal" selectable={false} items={[
          !authed ? { key: 'login', label: <Link to="/login">登录</Link> } : { key: 'logout', label: <a onClick={() => { clearToken(); location.href='/login' }}>退出</a> },
          authed ? { key: 'student', label: <Link to="/student/grades">学生-成绩查询</Link> } : null,
          teacher ? { key: 'import', label: <Link to="/teacher/import">教师-批量导入</Link> } : null,
          admin ? { key: 'admin-stu', label: <Link to="/admin/students">管理员-学生</Link> } : null,
          admin ? { key: 'admin-tea', label: <Link to="/admin/teachers">管理员-教师</Link> } : null,
          admin ? { key: 'admin-cou', label: <Link to="/admin/courses">管理员-课程</Link> } : null,
          admin ? { key: 'tp', label: <Link to="/admin/time-periods">时间段管理</Link> } : null,
        ].filter(Boolean) as any} />
      </Header>
      <Content style={{ padding: 24 }}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/student/grades" element={authed ? <StudentGrades /> : <LoginPage />} />
          <Route path="/teacher/import" element={hasRole(['ADMIN','TEACHER']) ? <ImportGrades /> : <LoginPage />} />
          <Route path="/admin/students" element={admin ? <AdminStudents /> : <LoginPage />} />
          <Route path="/admin/teachers" element={admin ? <AdminTeachers /> : <LoginPage />} />
          <Route path="/admin/courses" element={admin ? <AdminCourses /> : <LoginPage />} />
          <Route path="/admin/time-periods" element={admin ? <TimePeriods /> : <LoginPage />} />
        </Routes>
      </Content>
    </Layout>
  )
}


