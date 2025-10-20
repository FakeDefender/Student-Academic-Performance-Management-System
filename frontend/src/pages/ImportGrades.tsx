import { useState } from 'react'
import { Button, Card, Form, Input, InputNumber, Upload, message } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { importGrades } from '@/api/grades'

export default function ImportGrades() {
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)

  const onFinish = async (values: any) => {
    if (!file) { message.warning('请先选择文件'); return }
    const fd = new FormData()
    fd.append('file', file)
    fd.append('semester', values.semester)
    fd.append('academicYear', values.academicYear)
    fd.append('teacherId', String(values.teacherId))
    setLoading(true)
    try {
      const res = await importGrades(fd)
      message.success(`导入成功：${res.data || 0} 条`)
    } catch (e: any) {
      message.error(e?.response?.data?.message || '导入失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="教师 - 批量导入成绩">
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item name="semester" label="学期" rules={[{ required: true }]}>
          <Input placeholder="如：春季/秋季" />
        </Form.Item>
        <Form.Item name="academicYear" label="学年" rules={[{ required: true }]}>
          <Input placeholder="如：2024-2025" />
        </Form.Item>
        <Form.Item name="teacherId" label="教师ID" rules={[{ required: true }]}>
          <InputNumber min={1} />
        </Form.Item>
        <Form.Item label="Excel文件(.xlsx)">
          <Upload beforeUpload={(f) => { setFile(f as File); return false }} maxCount={1} accept=".xlsx,.xls">
            <Button icon={<UploadOutlined />}>选择文件</Button>
          </Upload>
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={loading}>开始导入</Button>
      </Form>
    </Card>
  )
}


