import { Button, Card, Form, Input, message, Typography, Space } from 'antd'
import { UserOutlined, LockOutlined, LoginOutlined } from '@ant-design/icons'
import { login } from '@/api/auth'
import { useNavigate } from 'react-router-dom'

const { Title, Text } = Typography

export default function LoginPage() {
  const [form] = Form.useForm()
  const navigate = useNavigate()

  const onFinish = async (values: any) => {
    try {
      const { token } = await login(values.username, values.password)
      localStorage.setItem('token', token)
      window.dispatchEvent(new Event('auth-changed'))
      message.success('登录成功')
      navigate('/student/grades')
    } catch (e: any) {
      message.error(e?.response?.data?.message || '登录失败')
    }
  }

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center',
      minHeight: '100vh',
      background: '#f8f9fa',
      padding: '20px'
    }}>
      <Card 
        style={{ 
          width: 400, 
          borderRadius: 16,
          boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
          border: '1px solid #e9ecef',
          background: '#ffffff'
        }}
        bodyStyle={{ padding: '40px' }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Space direction="vertical" size={16}>
            <div style={{ 
              width: 64, 
              height: 64, 
              borderRadius: '50%', 
              background: '#f8f9fa',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto',
              border: '2px solid #e9ecef'
            }}>
              <LoginOutlined style={{ fontSize: 28, color: '#6c757d' }} />
            </div>
            <Title level={2} style={{ margin: 0, color: '#495057', fontWeight: 600 }}>
              学生成绩管理系统
            </Title>
            <Text type="secondary" style={{ fontSize: 16 }}>
              请登录您的账户
            </Text>
          </Space>
        </div>

        <Form form={form} onFinish={onFinish} layout="vertical" size="large">
          <Form.Item 
            name="username" 
            label={<Text strong style={{ color: '#495057' }}>用户名</Text>} 
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input 
              placeholder="请输入用户名" 
              prefix={<UserOutlined style={{ color: '#6c757d' }} />}
              style={{ borderRadius: 8 }}
            />
          </Form.Item>
          <Form.Item 
            name="password" 
            label={<Text strong style={{ color: '#495057' }}>密码</Text>} 
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password 
              placeholder="请输入密码" 
              prefix={<LockOutlined style={{ color: '#6c757d' }} />}
              style={{ borderRadius: 8 }}
            />
          </Form.Item>
          <Form.Item style={{ marginTop: 32 }}>
            <Button 
              type="primary" 
              htmlType="submit" 
              block 
              size="large"
              style={{ 
                borderRadius: 8,
                background: '#495057',
                borderColor: '#495057',
                height: 48,
                fontSize: 16,
                fontWeight: 500
              }}
            >
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}


