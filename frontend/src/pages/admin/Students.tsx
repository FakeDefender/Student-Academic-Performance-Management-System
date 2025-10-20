import { Card, Table, Button, Space, Modal, Form, Input, message, Select } from 'antd'
import { useEffect, useState } from 'react'
import { listStudents, createStudent, updateStudent, deleteStudent } from '@/api/students'

export default function AdminStudents() {
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const [data, setData] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ pageNo: 1, pageSize: 10, total: 0 })
  const [keyword, setKeyword] = useState<string | undefined>()

  const columns = [
    { title: 'ID', dataIndex: 'id' },
    { title: '学号', dataIndex: 'studentId' },
    { title: '姓名', dataIndex: 'name' },
    { title: '班级', dataIndex: 'className' },
    { title: '专业', dataIndex: 'major' },
    { title: '状态', dataIndex: 'status' },
    {
      title: '操作',
      render: (_: any, record: any) => (
        <Space>
          <Button type="link" onClick={() => { setOpen(true); form.setFieldsValue(record) }}>编辑</Button>
          <Button type="link" danger onClick={async () => { await onDelete(record.id) }}>删除</Button>
        </Space>
      )
    }
  ]

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await listStudents({ keyword, pageNo: pagination.pageNo, pageSize: pagination.pageSize })
      setData(res.data.records || [])
      setPagination(prev => ({ ...prev, total: res.data.total || 0 }))
    } catch (e: any) {
      message.error(e?.response?.data?.message || '加载失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchData() }, [pagination.pageNo, pagination.pageSize, keyword])

  const onDelete = async (id: number) => {
    try {
      await deleteStudent(id)
      message.success('已删除')
      fetchData()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '删除失败')
    }
  }

  return (
    <Card title="管理员 - 学生管理" extra={<Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>新增学生</Button>}>
      <Space style={{ marginBottom: 12 }}>
        <Input placeholder="关键词（姓名/学号）" allowClear onChange={e => setKeyword(e.target.value || undefined)} style={{ width: 240 }} />
        <Button onClick={() => setPagination(p => ({ ...p, pageNo: 1 }))}>查询</Button>
      </Space>
      <Table rowKey="id" columns={columns as any} dataSource={data} loading={loading}
             pagination={{ current: pagination.pageNo, pageSize: pagination.pageSize, total: pagination.total, onChange: (p, s) => setPagination({ pageNo: p, pageSize: s, total: pagination.total }) }} />

      <Modal title="学生信息" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={async (values: any) => {
          try {
            if (values.id) await updateStudent(values.id, values); else await createStudent(values)
            message.success('保存成功')
            setOpen(false)
            fetchData()
          } catch (e: any) {
            message.error(e?.response?.data?.message || '保存失败')
          }
        }}>
          <Form.Item name="id" hidden><Input /></Form.Item>
          <Form.Item label="学号" name="studentId" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="姓名" name="name" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="班级" name="className"><Input /></Form.Item>
          <Form.Item label="专业" name="major"><Input /></Form.Item>
          <Form.Item label="状态" name="status"><Select allowClear options={[{ value: 'ACTIVE', label: 'ACTIVE' }, { value: 'GRADUATED', label: 'GRADUATED' }, { value: 'SUSPENDED', label: 'SUSPENDED' }]} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}


