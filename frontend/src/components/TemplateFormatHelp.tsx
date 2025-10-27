import { Card, Table, Tag } from 'antd'

export default function TemplateFormatHelp() {
  const columns = [
    {
      title: '列名',
      dataIndex: 'column',
      key: 'column',
    },
    {
      title: '数据类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => <Tag color="blue">{type}</Tag>
    },
    {
      title: '必填',
      dataIndex: 'required',
      key: 'required',
      render: (required: boolean) => (
        <Tag color={required ? 'red' : 'green'}>
          {required ? '是' : '否'}
        </Tag>
      )
    },
    {
      title: '说明',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '示例',
      dataIndex: 'example',
      key: 'example',
    }
  ]

  const data = [
    {
      key: '1',
      column: '学生学号',
      type: '文本',
      required: true,
      description: '学生的学号（如S0001）',
      example: 'S0001'
    },
    {
      key: '2',
      column: '课程代码',
      type: '文本',
      required: true,
      description: '课程代码（如CS101）',
      example: 'CS101'
    },
    {
      key: '3',
      column: '分数',
      type: '数字',
      required: false,
      description: '学生成绩，范围0-100',
      example: '85.5'
    },
    {
      key: '4',
      column: '考试类型',
      type: '文本',
      required: false,
      description: '考试类型，默认为FINAL',
      example: 'FINAL'
    },
    {
      key: '5',
      column: '学期',
      type: '文本',
      required: false,
      description: '学期信息，如为空则使用默认值',
      example: '秋季'
    },
    {
      key: '6',
      column: '学年',
      type: '文本',
      required: false,
      description: '学年信息，如为空则使用默认值',
      example: '2024-2025'
    },
    {
      key: '7',
      column: '教师ID',
      type: '文本',
      required: false,
      description: '教师编号，如为空则使用默认值',
      example: 'T0001'
    },
    {
      key: '8',
      column: '备注',
      type: '文本',
      required: false,
      description: '成绩相关备注信息',
      example: '平时表现良好'
    }
  ]

  return (
    <Card title="模板格式说明" size="small">
      <Table 
        columns={columns} 
        dataSource={data} 
        pagination={false}
        size="small"
        scroll={{ x: 800 }}
      />
    </Card>
  )
}
