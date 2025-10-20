import { Card, Table, Button, Space, Modal, Form, Input, DatePicker, Switch, message } from 'antd'
import { useEffect, useState } from 'react'
import { listActiveTimePeriods, createTimePeriod, updateTimePeriod, deleteTimePeriod } from '@/api/timePeriods'
import dayjs from 'dayjs'

export default function TimePeriods() {
  const [data, setData] = useState<any[]>([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const columns = [
    { title: 'ID', dataIndex: 'id' },
    { title: '名称', dataIndex: 'name' },
    { title: '开始时间', dataIndex: 'startDate' },
    { title: '结束时间', dataIndex: 'endDate' },
    { title: '启用', dataIndex: 'isActive', render: (v: boolean) => (v ? '是' : '否') },
    {
      title: '操作',
      render: (_: any, record: any) => (
        <Space>
          <Button type="link" onClick={() => { setOpen(true); form.setFieldsValue({ ...record, startDate: dayjs(record.startDate), endDate: dayjs(record.endDate) }) }}>编辑</Button>
          <Button type="link" danger onClick={async () => { await onDelete(record.id) }}>删除</Button>
        </Space>
      )
    }
  ]
  const fetchData = async () => {
    try {
      const res = await listActiveTimePeriods()
      setData(res.data || [])
    } catch (e: any) {
      message.error(e?.response?.data?.message || '加载失败')
    }
  }

  useEffect(() => { fetchData() }, [])

  const onDelete = async (id: number) => {
    try {
      await deleteTimePeriod(id)
      message.success('已删除')
      fetchData()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '删除失败')
    }
  }

  return (
    <Card title="管理员 - 时间段管理" extra={<Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>新增时间段</Button>}>
      <Table rowKey="id" columns={columns as any} dataSource={data} />
      <Modal title="时间段" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={async (values: any) => {
          const payload = { ...values, startDate: values.startDate?.toISOString(), endDate: values.endDate?.toISOString() }
          try {
            if (values.id) await updateTimePeriod(values.id, payload); else await createTimePeriod(payload)
            message.success('保存成功')
            setOpen(false)
            fetchData()
          } catch (e: any) {
            message.error(e?.response?.data?.message || '保存失败')
          }
        }}>
          <Form.Item name="id" hidden><Input /></Form.Item>
          <Form.Item label="名称" name="name" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="开始时间" name="startDate" rules={[{ required: true }]}><DatePicker showTime /></Form.Item>
          <Form.Item label="结束时间" name="endDate" rules={[{ required: true }]}><DatePicker showTime /></Form.Item>
          <Form.Item label="启用" name="isActive" valuePropName="checked"><Switch /></Form.Item>
          <Form.Item label="描述" name="description"><Input.TextArea rows={3} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}


