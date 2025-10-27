import { useState, useEffect } from 'react'
import { Button, Card, Form, Input, Select, Table, message, Alert, Typography, Row, Col, Space } from 'antd'
import { SearchOutlined, BookOutlined, UserOutlined, CalendarOutlined } from '@ant-design/icons'
import { getStudentGradesByNumber, getStudentGradesByTeacher } from '@/api/grades'
import { getStudentProfile } from '@/api/students'
import { hasRole } from '@/store/auth'

const { Title, Text } = Typography

export default function StudentGrades() {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<any[]>([])
  const [studentId, setStudentId] = useState<string>('')
  const [form] = Form.useForm()

  const columns = [
    { title: '课程', dataIndex: 'courseName' },
    { title: '课程代码', dataIndex: 'courseCode' },
    { title: '学期', dataIndex: 'semester' },
    { title: '学年', dataIndex: 'academicYear' },
    { title: '分数', dataIndex: 'score' },
    { title: '等级', dataIndex: 'gradeLetter' },
    { title: '通过', dataIndex: 'isPassed', render: (v: boolean) => (v ? '是' : '否') },
  ]

  const loadStudentProfile = async () => {
    try {
      const res = await getStudentProfile()
      const studentNumber = res.data?.studentId
      if (studentNumber) {
        setStudentId(studentNumber)
        form.setFieldsValue({ studentNumber })
      }
    } catch (error) {
      console.error('获取学生信息失败:', error)
    }
  }

  const onSearch = async (values: any) => {
    const searchStudentNumber = values.studentNumber || studentId
    if (!searchStudentNumber) {
      message.warning('学生学号不能为空')
      return
    }
    setLoading(true)
    try {
      let res
      if (hasRole(['TEACHER']) && !hasRole(['ADMIN'])) {
        // 教师使用带权限控制的API
        res = await getStudentGradesByTeacher(searchStudentNumber, { semester: values.semester, academicYear: values.academicYear })
      } else {
        // 管理员和学生使用普通API
        res = await getStudentGradesByNumber(searchStudentNumber, { semester: values.semester, academicYear: values.academicYear })
      }
      setData(res.data || [])
    } catch (e: any) {
      const code = e?.response?.data?.code
      if (code === 'TIME_WINDOW_CLOSED') {
        message.error('当前不在成绩查询开放时间段内')
      } else if (code === 'PERMISSION_DENIED') {
        message.error('您没有权限查看该学生的成绩')
      } else {
        message.error(e?.response?.data?.message || '查询失败')
      }
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    // 只有学生角色才自动加载学号
    if (hasRole(['STUDENT']) && !hasRole(['ADMIN', 'TEACHER'])) {
      loadStudentProfile()
    }
  }, [])

  const isTeacher = hasRole(['TEACHER']) && !hasRole(['ADMIN'])
  const isStudent = hasRole(['STUDENT']) && !hasRole(['ADMIN', 'TEACHER'])
  
  return (
    <div style={{ padding: '24px', background: '#f8f9fa', minHeight: '100vh' }}>
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
          borderBottom: '1px solid #e9ecef'
        }}>
          <Title level={3} style={{ margin: 0, color: '#495057' }}>
            <BookOutlined style={{ marginRight: 8, color: '#6c757d' }} />
            {isTeacher ? "教师 - 学生成绩查询" : "学生成绩查询"}
          </Title>
        </div>
        
        <div style={{ padding: '24px' }}>
          {isTeacher && (
            <Alert
              message="权限说明"
              description="您只能查询自己课程的学生成绩。如果提示权限不足，说明该学生不在您的课程中。"
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
          )}
          {isStudent && (
            <Alert
              message="成绩查询说明"
              description="您正在查询自己的成绩。学号已自动填充，不可修改。"
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
          )}

          <Card 
            style={{ 
              marginBottom: 24,
              borderRadius: 12,
              background: '#f8f9fa',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: '20px' }}
          >
            <Form form={form} layout="vertical" onFinish={onSearch}>
              <Row gutter={[16, 16]}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item 
                    name="studentNumber" 
                    label={<Text strong style={{ color: '#495057' }}>学生学号</Text>}
                  >
                    <Input 
                      placeholder={isStudent ? "学号已自动填充" : "请输入学生学号，如：S0001"} 
                      disabled={isStudent}
                      prefix={<UserOutlined style={{ color: '#6c757d' }} />}
                      style={{ borderRadius: 8 }}
                      suffix={isStudent ? <Text type="secondary">不可修改</Text> : ""}
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item 
                    name="semester" 
                    label={<Text strong style={{ color: '#495057' }}>学期</Text>}
                  >
                    <Select 
                      allowClear 
                      style={{ borderRadius: 8 }}
                      placeholder="选择学期"
                      options={[{ value: '春季', label: '春季' }, { value: '秋季', label: '秋季' }]} 
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item 
                    name="academicYear" 
                    label={<Text strong style={{ color: '#495057' }}>学年</Text>}
                  >
                    <Select 
                      allowClear 
                      style={{ borderRadius: 8 }}
                      placeholder="选择学年"
                      options={[{ value: '2024-2025', label: '2024-2025' }, { value: '2025-2026', label: '2025-2026' }]} 
                    />
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item style={{ marginBottom: 0 }}>
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  loading={loading}
                  icon={<SearchOutlined />}
                  size="large"
                  style={{ 
                    borderRadius: 8,
                    background: '#495057',
                    borderColor: '#495057',
                    fontWeight: 500
                  }}
                >
                  查询成绩
                </Button>
              </Form.Item>
            </Form>
          </Card>

          <Card 
            style={{ 
              borderRadius: 12,
              background: '#ffffff',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: 0 }}
          >
            <div style={{ 
              padding: '16px 20px', 
              borderBottom: '1px solid #e9ecef',
              background: '#f8f9fa',
              borderRadius: '12px 12px 0 0'
            }}>
              <Space>
                <CalendarOutlined style={{ color: '#6c757d' }} />
                <Text strong style={{ color: '#495057' }}>成绩列表</Text>
              </Space>
            </div>
            <div style={{ padding: '20px' }}>
              <Table 
                rowKey="id" 
                columns={columns as any} 
                dataSource={data} 
                loading={loading} 
                pagination={false}
                style={{ borderRadius: 8 }}
              />
            </div>
          </Card>
        </div>
      </Card>
    </div>
  )
}


