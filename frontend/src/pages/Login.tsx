import { Button, Card, Form, Input, message } from 'antd'
import { login } from '@/api/auth'
import { useNavigate } from 'react-router-dom'

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
    <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
      <Card title="登录" style={{ width: 360 }}>
        <Form form={form} onFinish={onFinish} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true }]}>
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>登录</Button>
        </Form>
      </Card>
    </div>
  )
}


