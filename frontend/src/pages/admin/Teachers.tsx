import { Card, Table, Button, Space, Modal, Form, Input, message } from 'antd'
import { useEffect, useState } from 'react'
import { listTeachers, createTeacher, updateTeacher, deleteTeacher } from '@/api/teachers'

export default function AdminTeachers() {
  const [data, setData] = useState<any[]>([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ pageNo: 1, pageSize: 10, total: 0 })
  const [keyword, setKeyword] = useState<string | undefined>()

  const columns = [
    { title: '工号', dataIndex: 'teacherId' },
    { title: '姓名', dataIndex: 'name' },
    { title: '部门', dataIndex: 'department' },
    { title: '职称', dataIndex: 'title' },
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
      const res = await listTeachers({ keyword, pageNo: pagination.pageNo, pageSize: pagination.pageSize })
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
      await deleteTeacher(id)
      message.success('已删除')
      fetchData()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '删除失败')
    }
  }

  return (
    <Card title="管理员 - 教师管理" extra={<Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>新增教师</Button>}>
      <Table rowKey="id" columns={columns as any} dataSource={data} loading={loading}
             pagination={{ current: pagination.pageNo, pageSize: pagination.pageSize, total: pagination.total, onChange: (p, s) => setPagination({ pageNo: p, pageSize: s, total: pagination.total }) }} />

      <Modal title="教师信息" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={async (values: any) => {
          try {
            if (values.id) await updateTeacher(values.id, values); else await createTeacher(values)
            message.success('保存成功')
            setOpen(false)
            fetchData()
          } catch (e: any) {
            message.error(e?.response?.data?.message || '保存失败')
          }
        }}>
          <Form.Item name="id" hidden><Input /></Form.Item>
          <Form.Item label="工号" name="teacherId" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="姓名" name="name" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="部门" name="department"><Input /></Form.Item>
          <Form.Item label="职称" name="title"><Input /></Form.Item>
          <Form.Item label="电话" name="phone"><Input /></Form.Item>
          <Form.Item label="办公室" name="office"><Input /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}


