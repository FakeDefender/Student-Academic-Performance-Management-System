import { Card, Table, Button, Space, Modal, Form, Input, DatePicker, Switch, message, Tabs, Select, Tag, Popconfirm } from 'antd'
import { useEffect, useState } from 'react'
import { listActiveTimePeriods, createTimePeriod, updateTimePeriod, deleteTimePeriod } from '@/api/timePeriods'
import { getCourses } from '@/api/courses'
import { 
  createCourseTimePeriod, 
  deleteCourseTimePeriod, 
  getCourseTimePeriodsByTimePeriod,
  CourseTimePeriod,
  CourseTimePeriodDetail 
} from '@/api/courseTimePeriods'
import dayjs from 'dayjs'

export default function TimePeriods() {
  const [data, setData] = useState<any[]>([])
  const [courses, setCourses] = useState<any[]>([])
  const [courseTimePeriods, setCourseTimePeriods] = useState<CourseTimePeriodDetail[]>([])
  const [selectedTimePeriod, setSelectedTimePeriod] = useState<number | null>(null)
  const [open, setOpen] = useState(false)
  const [courseModalOpen, setCourseModalOpen] = useState(false)
  const [form] = Form.useForm()
  const [courseForm] = Form.useForm()

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
          <Button type="link" onClick={() => { setSelectedTimePeriod(record.id); loadCourseTimePeriods(record.id) }}>关联课程</Button>
          <Button type="link" danger onClick={async () => { await onDelete(record.id) }}>删除</Button>
        </Space>
      )
    }
  ]

  const courseColumns = [
    { title: '课程代码', dataIndex: 'courseCode', key: 'courseCode' },
    { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
    {
      title: '操作',
      key: 'action',
      render: (text: any, record: CourseTimePeriodDetail) => (
        <Popconfirm
          title="确定要删除这个关联吗？"
          onConfirm={() => handleDeleteCourseTimePeriod(record.id!)}
          okText="确定"
          cancelText="取消"
        >
          <Button type="link" danger>删除关联</Button>
        </Popconfirm>
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

  const loadCourses = async () => {
    try {
      const res = await getCourses()
      setCourses(Array.isArray(res.data) ? res.data : [])
    } catch (e: any) {
      message.error('加载课程失败')
    }
  }

  const loadCourseTimePeriods = async (timePeriodId: number) => {
    try {
      const res = await getCourseTimePeriodsByTimePeriod(timePeriodId)
      setCourseTimePeriods(res.data || [])
    } catch (e: any) {
      message.error('加载课程关联失败')
    }
  }

  const handleCreateCourseTimePeriod = async (values: any) => {
    try {
      await createCourseTimePeriod({
        courseId: values.courseId,
        timePeriodId: selectedTimePeriod!
      })
      message.success('关联创建成功')
      setCourseModalOpen(false)
      courseForm.resetFields()
      loadCourseTimePeriods(selectedTimePeriod!)
    } catch (e: any) {
      message.error(e?.response?.data?.message || '创建关联失败')
    }
  }

  const handleDeleteCourseTimePeriod = async (id: number) => {
    try {
      await deleteCourseTimePeriod(id)
      message.success('删除成功')
      loadCourseTimePeriods(selectedTimePeriod!)
    } catch (e: any) {
      message.error('删除失败')
    }
  }

  useEffect(() => { 
    fetchData()
    loadCourses()
  }, [])

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
    <div>
      <Card title="时间段管理" extra={<Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>新增时间段</Button>}>
        <Table rowKey="id" columns={columns as any} dataSource={data} />
      </Card>

      {selectedTimePeriod && (
        <Card 
          title={`时间段课程关联 - ${data.find(tp => tp.id === selectedTimePeriod)?.name || ''}`}
          style={{ marginTop: 16 }}
          extra={
            <Button 
              type="primary" 
              onClick={() => setCourseModalOpen(true)}
            >
              关联课程
            </Button>
          }
        >
          <Table 
            rowKey="id" 
            columns={courseColumns} 
            dataSource={courseTimePeriods}
            pagination={false}
          />
        </Card>
      )}

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

      <Modal 
        title="关联课程" 
        open={courseModalOpen} 
        onCancel={() => {
          setCourseModalOpen(false)
          courseForm.resetFields()
        }} 
        onOk={() => courseForm.submit()}
      >
        <Form form={courseForm} layout="vertical" onFinish={handleCreateCourseTimePeriod}>
          <Form.Item
            name="courseId"
            label="选择课程"
            rules={[{ required: true, message: '请选择课程' }]}
          >
            <Select
              placeholder="请选择要关联的课程"
              showSearch
              optionFilterProp="children"
              filterOption={(input, option) =>
                String(option?.children || '').toLowerCase().includes(input.toLowerCase())
              }
            >
              {Array.isArray(courses) && courses.map(course => (
                <Select.Option key={course.id} value={course.id}>
                  {course.courseCode} - {course.courseName}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}


