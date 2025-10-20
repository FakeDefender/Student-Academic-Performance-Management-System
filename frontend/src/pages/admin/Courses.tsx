import { Card, Table, Button, Space, Modal, Form, Input, InputNumber, Switch, message } from 'antd'
import { useEffect, useState } from 'react'
import { listCourses, createCourse, updateCourse, deleteCourse } from '@/api/courses'

export default function AdminCourses() {
  const [data, setData] = useState<any[]>([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ pageNo: 1, pageSize: 10, total: 0 })
  const [keyword, setKeyword] = useState<string | undefined>()

  const columns = [
    { title: 'ID', dataIndex: 'id' },
    { title: '课程代码', dataIndex: 'courseCode' },
    { title: '课程名称', dataIndex: 'courseName' },
    { title: '学分', dataIndex: 'credits' },
    { title: '部门', dataIndex: 'department' },
    { title: '启用', dataIndex: 'isActive', render: (v: boolean) => v ? '是' : '否' },
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
      const res = await listCourses({ keyword, pageNo: pagination.pageNo, pageSize: pagination.pageSize })
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
      await deleteCourse(id)
      message.success('已删除')
      fetchData()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '删除失败')
    }
  }

  return (
    <Card title="管理员 - 课程管理" extra={<Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>新增课程</Button>}>
      <Table rowKey="id" columns={columns as any} dataSource={data} loading={loading}
             pagination={{ current: pagination.pageNo, pageSize: pagination.pageSize, total: pagination.total, onChange: (p, s) => setPagination({ pageNo: p, pageSize: s, total: pagination.total }) }} />

      <Modal title="课程信息" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={async (values: any) => {
          values.isActive = !!values.isActive
          try {
            if (values.id) await updateCourse(values.id, values); else await createCourse(values)
            message.success('保存成功')
            setOpen(false)
            fetchData()
          } catch (e: any) {
            message.error(e?.response?.data?.message || '保存失败')
          }
        }}>
          <Form.Item name="id" hidden><Input /></Form.Item>
          <Form.Item label="课程代码" name="courseCode" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="课程名称" name="courseName" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="学分" name="credits" rules={[{ required: true }]}><InputNumber min={1} /></Form.Item>
          <Form.Item label="部门" name="department"><Input /></Form.Item>
          <Form.Item label="是否启用" name="isActive" valuePropName="checked"><Switch /></Form.Item>
          <Form.Item label="描述" name="description"><Input.TextArea rows={3} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}


