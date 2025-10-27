import { useState, useEffect } from 'react'
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  message, 
  Space,
  Alert,
  Row,
  Col,
  Typography
} from 'antd'
import { UserOutlined, EditOutlined, SaveOutlined, CloseOutlined } from '@ant-design/icons'
import { getStudentProfile, updateStudentProfile } from '@/api/students'

const { Title, Text } = Typography

export default function StudentProfile() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [editing, setEditing] = useState(false)
  const [profile, setProfile] = useState<any>(null)

  const fetchProfile = async () => {
    setLoading(true)
    try {
      const res = await getStudentProfile()
      setProfile(res.data)
      form.setFieldsValue(res.data)
    } catch (e: any) {
      message.error(e?.response?.data?.message || '获取个人信息失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProfile()
  }, [])

  const handleEdit = () => {
    setEditing(true)
  }

  const handleCancel = () => {
    setEditing(false)
    form.setFieldsValue(profile) // 恢复原始数据
  }

  const handleSave = async (values: any) => {
    try {
      await updateStudentProfile(values)
      message.success('个人信息更新成功')
      setEditing(false)
      fetchProfile() // 重新获取最新数据
    } catch (e: any) {
      message.error(e?.response?.data?.message || '更新失败')
    }
  }

  return (
    <div style={{ 
      padding: '24px', 
      background: '#f8f9fa',
      minHeight: '100vh'
    }}>
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
          <Title level={3} style={{ margin: 0, color: '#495057' }}>
            <UserOutlined style={{ marginRight: 8, color: '#6c757d' }} />
            个人信息
          </Title>
          {!editing ? (
            <Button 
              type="primary" 
              icon={<EditOutlined />} 
              onClick={handleEdit}
              style={{ 
                borderRadius: 8,
                background: '#495057',
                borderColor: '#495057',
                fontWeight: 500
              }}
            >
              编辑
            </Button>
          ) : (
            <Space>
              <Button 
                onClick={handleCancel} 
                icon={<CloseOutlined />}
                style={{ 
                  borderRadius: 8,
                  background: '#f8f9fa',
                  border: '1px solid #e9ecef',
                  color: '#495057'
                }}
              >
                取消
              </Button>
              <Button 
                type="primary" 
                icon={<SaveOutlined />} 
                onClick={() => form.submit()}
                style={{ 
                  borderRadius: 8,
                  background: '#495057',
                  borderColor: '#495057',
                  fontWeight: 500
                }}
              >
                保存
              </Button>
            </Space>
          )}
        </div>
        
        <div style={{ padding: '24px' }}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <Text type="secondary">加载中...</Text>
            </div>
          ) : (
            <>
              <Alert
                message="个人信息说明"
                description="您可以查看和修改个人信息，但学号不可修改。请确保信息准确无误。"
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

              <Form
                form={form}
                layout="vertical"
                onFinish={handleSave}
                disabled={!editing}
              >
                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item
                      name="studentId"
                      label={<Text strong style={{ color: '#495057' }}>学号</Text>}
                      rules={[{ required: true, message: '学号不能为空' }]}
                    >
                      <Input 
                        placeholder="学号" 
                        disabled 
                        suffix={<Text type="secondary">不可修改</Text>}
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="name"
                      label={<Text strong style={{ color: '#495057' }}>姓名</Text>}
                      rules={[{ required: true, message: '请输入姓名' }]}
                    >
                      <Input 
                        placeholder="请输入姓名" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                </Row>

                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item
                      name="email"
                      label={<Text strong style={{ color: '#495057' }}>邮箱</Text>}
                      rules={[
                        { required: true, message: '请输入邮箱' },
                        { type: 'email', message: '请输入有效的邮箱地址' }
                      ]}
                    >
                      <Input 
                        placeholder="请输入邮箱" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="phone"
                      label={<Text strong style={{ color: '#495057' }}>电话</Text>}
                      rules={[
                        { required: true, message: '请输入电话' },
                        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码' }
                      ]}
                    >
                      <Input 
                        placeholder="请输入手机号码" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                </Row>

                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item
                      name="major"
                      label={<Text strong style={{ color: '#495057' }}>专业</Text>}
                      rules={[{ required: true, message: '请输入专业' }]}
                    >
                      <Input 
                        placeholder="请输入专业" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="className"
                      label={<Text strong style={{ color: '#495057' }}>班级</Text>}
                      rules={[{ required: true, message: '请输入班级' }]}
                    >
                      <Input 
                        placeholder="请输入班级" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                </Row>

                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item
                      name="grade"
                      label={<Text strong style={{ color: '#495057' }}>年级</Text>}
                      rules={[{ required: true, message: '请输入年级' }]}
                    >
                      <Input 
                        placeholder="请输入年级" 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="status"
                      label={<Text strong style={{ color: '#495057' }}>状态</Text>}
                    >
                      <Input 
                        placeholder="学生状态" 
                        disabled 
                        style={{ borderRadius: 8 }}
                      />
                    </Form.Item>
                  </Col>
                </Row>

                <Form.Item
                  name="address"
                  label={<Text strong style={{ color: '#495057' }}>地址</Text>}
                >
                  <Input.TextArea 
                    placeholder="请输入详细地址" 
                    rows={3}
                    style={{ borderRadius: 8 }}
                  />
                </Form.Item>

                <Form.Item
                  name="remarks"
                  label={<Text strong style={{ color: '#495057' }}>备注</Text>}
                >
                  <Input.TextArea 
                    placeholder="其他备注信息" 
                    rows={2}
                    style={{ borderRadius: 8 }}
                  />
                </Form.Item>
              </Form>
            </>
          )}
        </div>
      </Card>
    </div>
  )
}
