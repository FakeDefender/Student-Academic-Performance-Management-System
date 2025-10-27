import { useState } from 'react'
import { Button, Card, Form, Input, InputNumber, Upload, message, Space, Divider, Alert, Collapse, Typography, Row, Col } from 'antd'
import { UploadOutlined, DownloadOutlined, QuestionCircleOutlined, FileExcelOutlined, FileTextOutlined } from '@ant-design/icons'
import { importGrades, downloadTemplate } from '@/api/grades'
import TemplateFormatHelp from '@/components/TemplateFormatHelp'
import { hasRole } from '@/store/auth'

const { Title, Text } = Typography

export default function ImportGrades() {
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)

  const onFinish = async () => {
    if (!file) { message.warning('请先选择文件'); return }
    const fd = new FormData()
    fd.append('file', file)
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

  const handleDownloadTemplate = async (type: 'excel' | 'csv') => {
    try {
      const blob = await downloadTemplate(type)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `成绩导入模板.${type === 'excel' ? 'xlsx' : 'csv'}`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      message.success('模板下载成功')
    } catch (e: any) {
      message.error('模板下载失败')
    }
  }

  const isTeacher = hasRole(['TEACHER']) && !hasRole(['ADMIN'])
  
  return (
    <div style={{ padding: '24px', background: '#f8f9fa', minHeight: '100vh' }}>
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
          borderBottom: '1px solid #e9ecef'
        }}>
          <Title level={3} style={{ margin: 0, color: '#495057' }}>
            <UploadOutlined style={{ marginRight: 8, color: '#6c757d' }} />
            教师 - 批量导入成绩
          </Title>
        </div>
        
        <div style={{ padding: '24px' }}>
          <Alert
            message="导入说明"
            description="请先下载模板文件，按照模板格式填写成绩数据（包含学号、课程代码、分数、学期、学年、教师工号等信息），然后上传文件进行批量导入。支持Excel(.xlsx)和CSV(.csv)格式。"
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
          {isTeacher && (
            <Alert
              message="权限说明"
              description="您只能导入自己课程的学生的成绩。如果导入失败，请检查课程代码是否为您教授的课程。"
              type="warning"
              showIcon
              style={{ 
                marginBottom: 24,
                borderRadius: 8,
                background: '#fff3cd',
                border: '1px solid #ffeaa7',
                color: '#856404'
              }}
            />
          )}

          <Row gutter={[24, 24]}>
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <DownloadOutlined style={{ color: '#6c757d' }} />
                    <Text strong style={{ color: '#495057' }}>下载模板</Text>
                  </Space>
                }
                style={{ 
                  borderRadius: 12,
                  background: '#f8f9fa',
                  border: '1px solid #e9ecef'
                }}
                bodyStyle={{ padding: '20px' }}
              >
                <Space direction="vertical" size={16} style={{ width: '100%' }}>
                  <Text type="secondary">选择模板格式下载：</Text>
                  <Space wrap>
                    <Button 
                      icon={<FileExcelOutlined />}
                      onClick={() => handleDownloadTemplate('excel')}
                      style={{ 
                        borderRadius: 8,
                        background: '#ffffff',
                        border: '1px solid #e9ecef',
                        color: '#495057'
                      }}
                    >
                      Excel 模板
                    </Button>
                    <Button 
                      icon={<FileTextOutlined />}
                      onClick={() => handleDownloadTemplate('csv')}
                      style={{ 
                        borderRadius: 8,
                        background: '#ffffff',
                        border: '1px solid #e9ecef',
                        color: '#495057'
                      }}
                    >
                      CSV 模板
                    </Button>
                  </Space>
                </Space>
              </Card>
            </Col>
            
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <UploadOutlined style={{ color: '#6c757d' }} />
                    <Text strong style={{ color: '#495057' }}>上传文件</Text>
                  </Space>
                }
                style={{ 
                  borderRadius: 12,
                  background: '#f8f9fa',
                  border: '1px solid #e9ecef'
                }}
                bodyStyle={{ padding: '20px' }}
              >
                <Space direction="vertical" size={16} style={{ width: '100%' }}>
                  <Text type="secondary">选择要导入的成绩文件：</Text>
                  <Upload 
                    beforeUpload={(f) => { setFile(f as File); return false }} 
                    maxCount={1} 
                    accept=".xlsx,.xls,.csv"
                    fileList={file ? [{
                      uid: '1',
                      name: file.name,
                      status: 'done',
                    }] : []}
                    onRemove={() => setFile(null)}
                    style={{ width: '100%' }}
                  >
                    <Button 
                      icon={<UploadOutlined />}
                      style={{ 
                        width: '100%',
                        borderRadius: 8,
                        background: '#ffffff',
                        border: '1px solid #e9ecef',
                        color: '#495057',
                        height: 40
                      }}
                    >
                      选择文件
                    </Button>
                  </Upload>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    支持格式：Excel(.xlsx, .xls) 和 CSV(.csv)
                  </Text>
                </Space>
              </Card>
            </Col>
          </Row>

          <div style={{ marginTop: 24, textAlign: 'center' }}>
            <Button 
              type="primary" 
              onClick={onFinish}
              loading={loading}
              size="large"
              style={{ 
                borderRadius: 8,
                background: '#495057',
                borderColor: '#495057',
                fontWeight: 500,
                paddingLeft: 32,
                paddingRight: 32
              }}
            >
              开始导入
            </Button>
          </div>

          <Divider style={{ margin: '32px 0' }} />

          <Card 
            title={
              <Space>
                <QuestionCircleOutlined style={{ color: '#6c757d' }} />
                <Text strong style={{ color: '#495057' }}>模板格式说明</Text>
              </Space>
            }
            style={{ 
              borderRadius: 12,
              background: '#ffffff',
              border: '1px solid #e9ecef'
            }}
            bodyStyle={{ padding: '20px' }}
          >
            <TemplateFormatHelp />
          </Card>
        </div>
      </Card>
    </div>
  )
}


