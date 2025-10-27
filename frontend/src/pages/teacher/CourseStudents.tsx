import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, message, Tag, Alert, Breadcrumb, Typography, Row, Col, Statistic } from 'antd'
import { ArrowLeftOutlined, UserOutlined, TeamOutlined, TrophyOutlined, BookOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { getCourseStudents } from '@/api/courses'

const { Title, Text } = Typography

interface StudentCourseInfo {
  studentId: number
  studentNumber: string
  studentName: string
  className: string
  major: string
  courseId: number
  courseCode: string
  courseName: string
  latestScore: number | null
  latestGradeLetter: string | null
  latestIsPassed: boolean | null
  latestSemester: string | null
  latestAcademicYear: string | null
}

export default function CourseStudents() {
  const [students, setStudents] = useState<StudentCourseInfo[]>([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { courseId } = useParams<{ courseId: string }>()

  const loadStudents = async () => {
    if (!courseId) return
    
    setLoading(true)
    try {
      const res = await getCourseStudents(parseInt(courseId))
      setStudents(Array.isArray(res.data) ? res.data : [])
    } catch (error: any) {
      message.error(error.response?.data?.message || '加载学生列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadStudents()
  }, [courseId])

  const columns = [
    {
      title: '学号',
      dataIndex: 'studentNumber',
      key: 'studentNumber',
      width: 120,
    },
    {
      title: '姓名',
      dataIndex: 'studentName',
      key: 'studentName',
      width: 100,
    },
    {
      title: '班级',
      dataIndex: 'className',
      key: 'className',
      width: 120,
    },
    {
      title: '专业',
      dataIndex: 'major',
      key: 'major',
      width: 150,
    },
    {
      title: '最新成绩',
      key: 'latestScore',
      width: 100,
      render: (text: any, record: StudentCourseInfo) => {
        if (record.latestScore === null) {
          return <Tag color="default">未录入</Tag>
        }
        return (
          <Space direction="vertical" size={0}>
            <span>{record.latestScore}</span>
            <Tag color={record.latestIsPassed ? 'green' : 'red'}>
              {record.latestGradeLetter}
            </Tag>
          </Space>
        )
      }
    },
    {
      title: '学期',
      dataIndex: 'latestSemester',
      key: 'latestSemester',
      width: 80,
      render: (semester: string) => semester || '-'
    },
    {
      title: '学年',
      dataIndex: 'latestAcademicYear',
      key: 'latestAcademicYear',
      width: 120,
      render: (year: string) => year || '-'
    }
  ]

  const courseInfo = students.length > 0 ? students[0] : null

  return (
    <div style={{ 
      padding: '24px', 
      background: '#f8f9fa',
      minHeight: '100vh'
    }}>
      {/* 面包屑导航 */}
      <Breadcrumb style={{ marginBottom: 24 }}>
        <Breadcrumb.Item>
          <Button 
            type="link" 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/teacher/courses')}
            style={{ padding: 0, fontSize: 16, color: '#6c757d' }}
          >
            我的课程
          </Button>
        </Breadcrumb.Item>
        <Breadcrumb.Item>
          <Text strong style={{ fontSize: 16, color: '#495057' }}>
            {courseInfo ? `${courseInfo.courseCode} - ${courseInfo.courseName}` : '课程学生'}
          </Text>
        </Breadcrumb.Item>
      </Breadcrumb>

      {/* 页面头部 */}
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0, color: '#495057', fontWeight: 600 }}>
          <UserOutlined style={{ marginRight: 12, color: '#6c757d' }} />
          {courseInfo ? `${courseInfo.courseCode} - ${courseInfo.courseName}` : '课程学生'}
        </Title>
        <Text type="secondary" style={{ fontSize: 16, color: '#6c757d' }}>
          查看该课程的所有学生及其成绩信息
        </Text>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={8}>
          <Card 
            style={{ 
              borderRadius: 12, 
              boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
              background: '#ffffff',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: '20px' }}
          >
            <Statistic
              title={<span style={{ color: '#6c757d' }}>总学生数</span>}
              value={students.length}
              prefix={<TeamOutlined style={{ color: '#6c757d' }} />}
              valueStyle={{ color: '#495057', fontSize: 28, fontWeight: 600 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card 
            style={{ 
              borderRadius: 12, 
              boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
              background: '#ffffff',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: '20px' }}
          >
            <Statistic
              title={<span style={{ color: '#6c757d' }}>已录入成绩</span>}
              value={students.filter(s => s.latestScore !== null).length}
              prefix={<TrophyOutlined style={{ color: '#6c757d' }} />}
              valueStyle={{ color: '#495057', fontSize: 28, fontWeight: 600 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card 
            style={{ 
              borderRadius: 12, 
              boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
              background: '#ffffff',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: '20px' }}
          >
            <Statistic
              title={<span style={{ color: '#6c757d' }}>通过率</span>}
              value={students.length > 0 ? Math.round((students.filter(s => s.latestIsPassed).length / students.length) * 100) : 0}
              suffix="%"
              prefix={<BookOutlined style={{ color: '#6c757d' }} />}
              valueStyle={{ color: '#495057', fontSize: 28, fontWeight: 600 }}
            />
          </Card>
        </Col>
      </Row>

      {/* 学生列表 */}
      <Card 
        style={{ 
          borderRadius: 16, 
          boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
          border: '1px solid #e9ecef',
          background: '#ffffff'
        }}
        bodyStyle={{ padding: 0 }}
      >
        <div style={{ 
          padding: '24px 24px 16px 24px', 
          background: '#ffffff',
          borderRadius: '16px 16px 0 0',
          borderBottom: '1px solid #e9ecef',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <Title level={4} style={{ margin: 0, color: '#495057' }}>
            <UserOutlined style={{ marginRight: 8, color: '#6c757d' }} />
            学生列表
          </Title>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/teacher/courses')}
            style={{
              background: '#f8f9fa',
              border: '1px solid #e9ecef',
              color: '#495057',
              borderRadius: 8
            }}
          >
            返回课程列表
          </Button>
        </div>
        
        <div style={{ padding: '24px' }}>
          <Alert
            message="学生信息"
            description="这里显示该课程的所有学生及其最新成绩信息。"
            type="info"
            showIcon
            style={{ 
              marginBottom: 24,
              borderRadius: 8,
              background: '#f8f9fa',
              border: '1px solid #e9ecef',
              color: '#495057'
            }}
          />

          <Table
            columns={columns}
            dataSource={students}
            rowKey="studentId"
            loading={loading}
            pagination={{
              pageSize: 20,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 名学生`,
              style: { marginTop: 16 }
            }}
            style={{
              borderRadius: 8
            }}
          />
        </div>
      </Card>
    </div>
  )
}
