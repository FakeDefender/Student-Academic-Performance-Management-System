import { useState } from 'react'
import { Button, Card, Form, InputNumber, Select, Table, message } from 'antd'
import { getStudentGrades } from '@/api/grades'

export default function StudentGrades() {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<any[]>([])

  const columns = [
    { title: '课程ID', dataIndex: 'courseId' },
    { title: '学期', dataIndex: 'semester' },
    { title: '学年', dataIndex: 'academicYear' },
    { title: '分数', dataIndex: 'score' },
    { title: '等级', dataIndex: 'gradeLetter' },
    { title: '通过', dataIndex: 'isPassed', render: (v: boolean) => (v ? '是' : '否') },
  ]

  const onSearch = async (values: any) => {
    if (!values.studentId) {
      message.warning('请输入学生ID')
      return
    }
    setLoading(true)
    try {
      const res = await getStudentGrades(values.studentId, { semester: values.semester, academicYear: values.academicYear })
      setData(res.data || [])
    } catch (e: any) {
      const code = e?.response?.data?.code
      if (code === 'TIME_WINDOW_CLOSED') {
        message.error('当前不在成绩查询开放时间段内')
      } else {
        message.error(e?.response?.data?.message || '查询失败')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="学生成绩查询">
      <Form layout="inline" onFinish={onSearch}>
        <Form.Item name="studentId" label="学生ID">
          <InputNumber min={1} />
        </Form.Item>
        <Form.Item name="semester" label="学期">
          <Select allowClear style={{ width: 120 }} options={[{ value: '春季', label: '春季' }, { value: '秋季', label: '秋季' }]} />
        </Form.Item>
        <Form.Item name="academicYear" label="学年">
          <Select allowClear style={{ width: 120 }} options={[{ value: '2024-2025', label: '2024-2025' }, { value: '2025-2026', label: '2025-2026' }]} />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>查询</Button>
        </Form.Item>
      </Form>

      <Table style={{ marginTop: 16 }} rowKey="id" columns={columns as any} dataSource={data} loading={loading} pagination={false} />
    </Card>
  )
}


