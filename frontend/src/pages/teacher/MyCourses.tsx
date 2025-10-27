import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, message, Tag, Alert, Modal, Form, Select, Typography, Row, Col, Statistic } from 'antd'
import { BookOutlined, InfoCircleOutlined, ClockCircleOutlined, UserOutlined, TrophyOutlined, TeamOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { getMyCourses } from '@/api/courses'
import { getTimePeriods } from '@/api/timePeriods'
import { createCourseTimePeriod, deleteCourseTimePeriod, getCourseTimePeriodsByCourse } from '@/api/courseTimePeriods'
import { hasRole } from '@/store/auth'

const { Title, Text } = Typography

interface Course {
  id: number
  courseCode: string
  courseName: string
  department: string
  credits: number
  isActive: boolean
}

interface TimePeriod {
  id: number
  name: string
  startDate: string
  endDate: string
  isActive: boolean
}

export default function MyCourses() {
  const [courses, setCourses] = useState<Course[]>([])
  const [timePeriods, setTimePeriods] = useState<TimePeriod[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null)
  const [form] = Form.useForm()
  const navigate = useNavigate()
  const isTeacher = hasRole(['TEACHER'])

  const loadCourses = async () => {
    setLoading(true)
    try {
      const res = await getMyCourses()
      setCourses(Array.isArray(res.data) ? res.data : [])
    } catch (error) {
      message.error('加载课程失败')
    } finally {
      setLoading(false)
    }
  }

  const loadTimePeriods = async () => {
    try {
      const res = await getTimePeriods()
      setTimePeriods(Array.isArray(res.data) ? res.data : [])
    } catch (error) {
      message.error('加载时间段失败')
    }
  }

  const handleSetTimePeriod = (course: Course) => {
    setSelectedCourse(course)
    setModalVisible(true)
  }

  const handleCreateTimePeriod = async (values: any) => {
    if (!selectedCourse) return
    
    try {
      await createCourseTimePeriod({
        courseId: selectedCourse.id,
        timePeriodId: values.timePeriodId
      })
      message.success('时间段设置成功')
      setModalVisible(false)
      form.resetFields()
    } catch (error: any) {
      message.error(error.response?.data?.message || '设置失败')
    }
  }

  useEffect(() => {
    loadCourses()
    loadTimePeriods()
  }, [])

  const columns = [
    {
      title: '课程代码',
      dataIndex: 'courseCode',
      key: 'courseCode',
    },
    {
      title: '课程名称',
      dataIndex: 'courseName',
      key: 'courseName',
    },
    {
      title: '院系',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '学分',
      dataIndex: 'credits',
      key: 'credits',
    },
    {
      title: '状态',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive: boolean) => (
        <Tag color={isActive ? 'green' : 'red'}>
          {isActive ? '激活' : '未激活'}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (text: any, record: Course) => (
        <Space>
          <Button 
            type="link" 
            icon={<UserOutlined />}
            onClick={() => navigate(`/teacher/courses/${record.id}/students`)}
          >
            查看学生
          </Button>
          <Button 
            type="link" 
            icon={<ClockCircleOutlined />}
            onClick={() => handleSetTimePeriod(record)}
          >
            设置时间段
          </Button>
          <Button 
            type="link" 
            icon={<InfoCircleOutlined />}
            onClick={() => message.info(`课程详情：${record.courseName}`)}
          >
            查看详情
          </Button>
        </Space>
      )
    }
  ]

  if (!isTeacher) {
    return (
      <div style={{ padding: '24px' }}>
        <Alert
          message="权限不足"
          description="您没有权限访问此页面"
          type="error"
          showIcon
        />
      </div>
    )
  }

  return (
    <div style={{ 
      padding: '24px', 
      background: '#f8f9fa',
      minHeight: '100vh'
    }}>
      {/* 页面头部 */}
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0, color: '#495057', fontWeight: 600 }}>
          <BookOutlined style={{ marginRight: 12, color: '#6c757d' }} />
          我的课程
        </Title>
        <Text type="secondary" style={{ fontSize: 16, color: '#6c757d' }}>
          管理您教授的课程，设置时间段，查看学生信息
        </Text>
      </div>


      {/* 课程列表 */}
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
          <Title level={4} style={{ margin: 0, color: '#495057' }}>
            <BookOutlined style={{ marginRight: 8, color: '#6c757d' }} />
            课程列表
          </Title>
        </div>
        
        <div style={{ padding: '24px' }}>
          <Alert
            message="课程管理"
            description="这里显示您教授的所有课程。您可以为每个课程设置不同的成绩查询时间段，查看学生信息。"
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
            dataSource={Array.isArray(courses) ? courses : []}
            rowKey="id"
            loading={loading}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 门课程`,
              style: { marginTop: 16 }
            }}
            style={{
              borderRadius: 8
            }}
          />
        </div>
      </Card>

      <Modal
        title={
          <Space>
            <ClockCircleOutlined style={{ color: '#6c757d' }} />
            <span>为课程 {selectedCourse?.courseName} 设置时间段</span>
          </Space>
        }
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false)
          form.resetFields()
        }}
        onOk={() => form.submit()}
        width={600}
        style={{ top: 20 }}
        styles={{
          header: {
            background: '#ffffff',
            color: '#495057',
            borderRadius: '8px 8px 0 0',
            borderBottom: '1px solid #e9ecef'
          }
        }}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateTimePeriod}
          style={{ marginTop: 16 }}
        >
          <Form.Item
            name="timePeriodId"
            label={<Text strong>选择时间段</Text>}
            rules={[{ required: true, message: '请选择时间段' }]}
          >
            <Select
              placeholder="请选择时间段"
              showSearch
              optionFilterProp="children"
              size="large"
              style={{ borderRadius: 8 }}
              filterOption={(input, option) =>
                String(option?.children || '').toLowerCase().includes(input.toLowerCase())
              }
            >
              {timePeriods.map(timePeriod => (
                <Select.Option key={timePeriod.id} value={timePeriod.id}>
                  <Space>
                    <span>{timePeriod.name}</span>
                    <Tag color={timePeriod.isActive ? 'green' : 'red'}>
                      {timePeriod.isActive ? '激活' : '未激活'}
                    </Tag>
                  </Space>
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
