import React, { useState, useEffect } from 'react';
import {
  Table, Tag, Button, Space, Card, Row, Col, Statistic, Modal, Form, Input,
  Radio, message, Select, Empty
} from 'antd';
import { EyeOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { eventApi } from '@/api/event';
import { runwayApi } from '@/api';
import type { FodEvent, Runway } from '@/types';
import {
  EventStatusMap, RiskLevelMap, EventStatusEnum, RiskLevelEnum, RoleEnum,
} from '@/constants';
import { useUser } from '@/context/UserContext';

const { TextArea } = Input;
const { Option } = Select;

const TowerEvaluate: React.FC = () => {
  const navigate = useNavigate();
  const { userRole, userId, userName } = useUser();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<FodEvent[]>([]);
  const [runways, setRunways] = useState<Runway[]>([]);
  const [evaluateVisible, setEvaluateVisible] = useState(false);
  const [currentEvent, setCurrentEvent] = useState<FodEvent | null>(null);
  const [form] = Form.useForm();
  const [statistics, setStatistics] = useState<Record<string, number>>({});

  useEffect(() => {
    if (userRole !== RoleEnum.TOWER_CONTROLLER) {
      message.warning('当前角色无权限访问此页面');
      navigate('/event/list');
      return;
    }
    loadRunways();
    loadStatistics();
  }, [userRole]);

  useEffect(() => {
    loadData();
  }, []);

  const loadRunways = async () => {
    const res = await runwayApi.getAll();
    setRunways(res.data || []);
  };

  const loadStatistics = async () => {
    const res = await eventApi.getStatistics();
    setStatistics(res.data || {});
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const params = {
        pageNum: 1,
        pageSize: 100,
      };
      const res = await eventApi.getPage(params);
      const allEvents = res.data?.records || [];
      const filtered = allEvents.filter(
        (e) => e.status === EventStatusEnum.REPORTED ||
               e.status === EventStatusEnum.EVALUATING ||
               e.status === EventStatusEnum.AFFECT ||
               e.status === EventStatusEnum.NOT_AFFECT ||
               e.status === EventStatusEnum.PENDING_CLOSE
      );
      setData(filtered);
    } finally {
      setLoading(false);
    }
  };

  const handleEvaluate = (record: FodEvent) => {
    setCurrentEvent(record);
    form.setFieldsValue({
      riskLevel: record.riskLevel,
      affectTakeoff: record.affectTakeoff,
      evaluateOpinion: '',
    });
    setEvaluateVisible(true);
  };

  const handleSubmitEvaluate = async (values: any) => {
    if (!currentEvent) return;
    try {
      await eventApi.evaluate({
        eventId: currentEvent.id,
        riskLevel: values.riskLevel,
        affectTakeoff: values.affectTakeoff,
        evaluateOpinion: values.evaluateOpinion,
        evaluatorId: userId,
        evaluatorName: userName,
      });
      message.success('评估完成');
      setEvaluateVisible(false);
      form.resetFields();
      loadData();
      loadStatistics();
    } catch (e) {
      // error handled
    }
  };

  const getRunwayName = (code?: string) => {
    const runway = runways.find((r) => r.runwayCode === code);
    return runway?.runwayName || code;
  };

  const statCards = [
    { title: '待评估', value: statistics.reported || 0, color: '#faad14' },
    { title: '影响起降', value: statistics.affect || 0, color: '#f5222d' },
    { title: '不影响起降', value: statistics.notAffect || 0, color: '#52c41a' },
    { title: '待关闭', value: statistics.pendingClose || 0, color: '#722ed1' },
  ];

  const columns = [
    {
      title: '事件编号',
      dataIndex: 'eventNo',
      width: 160,
      render: (val: string) => <code style={{ color: '#1890ff' }}>{val}</code>,
    },
    {
      title: '跑道',
      dataIndex: 'runwayCode',
      width: 100,
      render: (val: string) => getRunwayName(val),
    },
    {
      title: '位置',
      dataIndex: 'location',
      ellipsis: true,
    },
    {
      title: '异物类型',
      dataIndex: 'fodType',
      width: 100,
    },
    {
      title: '当前风险',
      dataIndex: 'riskLevel',
      width: 100,
      render: (val: number) => {
        if (!val) return <Tag color="default">未评估</Tag>;
        const info = RiskLevelMap[val];
        return <Tag color={info.color}>{info.label}</Tag>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (val: number) => {
        const info = EventStatusMap[val];
        return <Tag color={info.color}>{info.label}</Tag>;
      },
    },
    {
      title: '照片',
      dataIndex: 'photoCount',
      width: 80,
      render: (val: number) => (val > 0 ? `${val}张` : '无'),
    },
    {
      title: '上报时间',
      dataIndex: 'reportTime',
      width: 180,
      render: (val: string) => (val ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right' as const,
      render: (_: any, record: FodEvent) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => navigate(`/event/${record.id}`)}>
            <EyeOutlined /> 详情
          </Button>
          {(record.status === EventStatusEnum.REPORTED || record.status === EventStatusEnum.EVALUATING) && (
            <Button
              type="primary"
              size="small"
              onClick={() => handleEvaluate(record)}
            >
              <CheckCircleOutlined /> 评估
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          {statCards.map((item, index) => (
            <Col span={6} key={index}>
              <Statistic title={item.title} value={item.value} valueStyle={{ color: item.color }} />
            </Col>
          ))}
        </Row>
      </Card>

      <Card title="塔台评估 - 待处理事件">
        {data.length === 0 ? (
          <Empty description="暂无待处理事件" />
        ) : (
          <Table
            rowKey="id"
            loading={loading}
            dataSource={data}
            columns={columns}
            pagination={{
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条`,
            }}
            rowClassName={(record) => (record.isTop === 1 ? 'top-event' : '')}
          />
        )}
      </Card>

      <Modal
        title="风险评估"
        open={evaluateVisible}
        onCancel={() => setEvaluateVisible(false)}
        footer={null}
        width={600}
      >
        {currentEvent && (
          <div style={{ marginBottom: 16, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
            <div>事件编号：{currentEvent.eventNo}</div>
            <div>跑道：{getRunwayName(currentEvent.runwayCode)}</div>
            <div>位置：{currentEvent.location}</div>
          </div>
        )}
        <Form form={form} layout="vertical" onFinish={handleSubmitEvaluate}>
          <Form.Item
            name="riskLevel"
            label="风险等级"
            rules={[{ required: true, message: '请选择风险等级' }]}
          >
            <Radio.Group>
              <Radio value={RiskLevelEnum.LOW} style={{ color: RiskLevelMap[1].color }}>
                低风险
              </Radio>
              <Radio value={RiskLevelEnum.MEDIUM} style={{ color: RiskLevelMap[2].color }}>
                中风险
              </Radio>
              <Radio value={RiskLevelEnum.HIGH} style={{ color: RiskLevelMap[3].color }}>
                高风险
              </Radio>
              <Radio value={RiskLevelEnum.EXTREME} style={{ color: RiskLevelMap[4].color }}>
                极高风险
              </Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            name="affectTakeoff"
            label="是否影响起降"
            rules={[{ required: true, message: '请选择是否影响起降' }]}
          >
            <Radio.Group>
              <Radio value={1} style={{ color: '#f5222d' }}>
                影响起降（自动置顶并将跑道切换为受限状态）
              </Radio>
              <Radio value={0} style={{ color: '#52c41a' }}>
                不影响起降
              </Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item name="evaluateOpinion" label="评估意见">
            <TextArea rows={3} placeholder="请输入评估意见" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                确认评估
              </Button>
              <Button onClick={() => setEvaluateVisible(false)}>取消</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default TowerEvaluate;
