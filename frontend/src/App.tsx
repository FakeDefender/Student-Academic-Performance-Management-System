import { Layout, Menu } from 'antd'
import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import LoginPage from './pages/Login'
import StudentGrades from './pages/StudentGrades'
import ImportGrades from './pages/ImportGrades'
import AdminStudents from './pages/admin/Students'
import AdminTeachers from './pages/admin/Teachers'
import AdminCourses from './pages/admin/Courses'
import TimePeriods from './pages/admin/TimePeriods'
import MyCourses from './pages/teacher/MyCourses'
import CourseStudents from './pages/teacher/CourseStudents'
import StudentProfile from './pages/student/Profile'
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
  const teacherOnly = hasRole(['TEACHER']) && !hasRole(['ADMIN'])
  const student = hasRole(['STUDENT'])
  return (
    <Layout style={{ 
      minHeight: '100vh',
      background: '#f8f9fa'
    }}>
      <Header style={{ 
        background: '#ffffff',
        boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
        padding: '0 24px',
        borderBottom: '1px solid #e9ecef'
      }}>
        <Menu 
          theme="light" 
          mode="horizontal" 
          selectable={false} 
          style={{ 
            background: 'transparent',
            border: 'none',
            fontSize: '16px',
            fontWeight: 500
          }}
          items={[
            !authed ? { key: 'login', label: <Link to="/login" style={{ color: '#495057', fontWeight: 600 }}>登录</Link> } : { key: 'logout', label: <a onClick={() => { clearToken(); location.href='/login' }} style={{ color: '#495057', fontWeight: 600 }}>退出</a> },
            authed ? { key: 'student', label: <Link to="/student/grades" style={{ color: '#6c757d', fontWeight: 500 }}>学生-成绩查询</Link> } : null,
            student ? { key: 'profile', label: <Link to="/student/profile" style={{ color: '#6c757d', fontWeight: 500 }}>个人信息</Link> } : null,
            teacher ? { key: 'import', label: <Link to="/teacher/import" style={{ color: '#6c757d', fontWeight: 500 }}>教师-批量导入</Link> } : null,
            teacherOnly ? { key: 'my-courses', label: <Link to="/teacher/courses" style={{ color: '#6c757d', fontWeight: 500 }}>我的课程</Link> } : null,
            admin ? { key: 'admin-stu', label: <Link to="/admin/students" style={{ color: '#6c757d', fontWeight: 500 }}>管理员-学生</Link> } : null,
            admin ? { key: 'admin-tea', label: <Link to="/admin/teachers" style={{ color: '#6c757d', fontWeight: 500 }}>管理员-教师</Link> } : null,
            admin ? { key: 'admin-cou', label: <Link to="/admin/courses" style={{ color: '#6c757d', fontWeight: 500 }}>管理员-课程</Link> } : null,
            teacher ? { key: 'tp', label: <Link to="/admin/time-periods" style={{ color: '#6c757d', fontWeight: 500 }}>时间段管理</Link> } : null,
          ].filter(Boolean) as any} 
        />
      </Header>
      <Content style={{ 
        padding: 0,
        background: '#f8f9fa'
      }}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/student/grades" element={authed ? <StudentGrades /> : <LoginPage />} />
          <Route path="/student/profile" element={hasRole(['STUDENT']) ? <StudentProfile /> : <LoginPage />} />
          <Route path="/teacher/import" element={hasRole(['ADMIN','TEACHER']) ? <ImportGrades /> : <LoginPage />} />
          <Route path="/teacher/courses" element={hasRole(['TEACHER']) ? <MyCourses /> : <LoginPage />} />
          <Route path="/teacher/courses/:courseId/students" element={hasRole(['TEACHER']) ? <CourseStudents /> : <LoginPage />} />
          <Route path="/admin/students" element={admin ? <AdminStudents /> : <LoginPage />} />
          <Route path="/admin/teachers" element={admin ? <AdminTeachers /> : <LoginPage />} />
          <Route path="/admin/courses" element={admin ? <AdminCourses /> : <LoginPage />} />
          <Route path="/admin/time-periods" element={hasRole(['ADMIN','TEACHER']) ? <TimePeriods /> : <LoginPage />} />
        </Routes>
      </Content>
    </Layout>
  )
}


