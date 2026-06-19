import React, { useState, useEffect } from 'react';
import { Table, Tag, Card, Row, Col, Statistic, Button, Space, Empty } from 'antd';
import { EyeOutlined, RocketOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { runwayApi, eventApi } from '@/api';
import type { Runway, FodEvent } from '@/types';
import { RunwayStatusMap, EventStatusEnum } from '@/constants';

const RunwayManage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [runways, setRunways] = useState<Runway[]>([]);
  const [events, setEvents] = useState<FodEvent[]>([]);
  const [statistics, setStatistics] = useState({
    total: 0,
    normal: 0,
    frozen: 0,
    maintenance: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [runwayRes, eventRes] = await Promise.all([
        runwayApi.getAll(),
        eventApi.getList(),
      ]);
      const runwayList = runwayRes.data || [];
      const eventList = eventRes.data || [];
      setRunways(runwayList);
      setEvents(eventList);

      const normalCount = runwayList.filter((r) => r.status === 1).length;
      const frozenCount = runwayList.filter((r) => r.status === 2).length;
      const maintenanceCount = runwayList.filter((r) => r.status === 3).length;

      setStatistics({
        total: runwayList.length,
        normal: normalCount,
        frozen: frozenCount,
        maintenance: maintenanceCount,
      });
    } finally {
      setLoading(false);
    }
  };

  const getEventCount = (runwayId: number) => {
    return events.filter((e) => e.runwayId === runwayId && e.status !== EventStatusEnum.CLOSED && e.status !== EventStatusEnum.CANCELLED).length;
  };

  const getActiveEventCount = (runwayId: number) => {
    return events.filter((e) => e.runwayId === runwayId && e.status === EventStatusEnum.AFFECT).length;
  };

  const statCards = [
    { title: '跑道总数', value: statistics.total, color: '#1890ff', icon: <RocketOutlined /> },
    { title: '正常', value: statistics.normal, color: '#52c41a' },
    { title: '冻结', value: statistics.frozen, color: '#f5222d' },
    { title: '维修中', value: statistics.maintenance, color: '#faad14' },
  ];

  const columns = [
    {
      title: '跑道编号',
      dataIndex: 'runwayCode',
      width: 120,
      render: (val: string) => <code style={{ color: '#1890ff', fontSize: 16 }}>{val}</code>,
    },
    {
      title: '跑道名称',
      dataIndex: 'runwayName',
      width: 180,
    },
    {
      title: '规格',
      dataIndex: 'id',
      width: 160,
      render: (_: any, record: Runway) =>
        record.length && record.width ? `${record.length}m × ${record.width}m` : '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (val: number) => {
        const info = RunwayStatusMap[val];
        return <Tag color={info.color}>{info.label}</Tag>;
      },
    },
    {
      title: '放行状态',
      dataIndex: 'isFrozen',
      width: 120,
      render: (val: number, record: Runway) => (
        <Space>
          <Tag color={val === 1 ? 'red' : 'green'}>{val === 1 ? '已冻结' : '正常放行'}</Tag>
          {record.freezeOperator && <span style={{ color: '#999' }}>{record.freezeOperator}</span>}
        </Space>
      ),
    },
    {
      title: '冻结原因',
      dataIndex: 'freezeReason',
      ellipsis: true,
    },
    {
      title: '冻结时间',
      dataIndex: 'freezeTime',
      width: 180,
    },
    {
      title: '活跃事件',
      dataIndex: 'id',
      width: 120,
      render: (val: number) => {
        const count = getEventCount(val);
        const affectCount = getActiveEventCount(val);
        return (
          <Space>
            {count > 0 && <Tag color="blue">{count} 件</Tag>}
            {affectCount > 0 && <Tag color="red">{affectCount} 影响起降</Tag>}
            {count === 0 && <span style={{ color: '#999' }}>无</span>}
          </Space>
        );
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: any, record: Runway) => (
        <Button type="link" size="small" onClick={() => navigate(`/event/list?runwayId=${record.id}`)}>
          <EyeOutlined /> 查看事件
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          {statCards.map((item, index) => (
            <Col span={6} key={index}>
              <Statistic
                title={item.title}
                value={item.value}
                valueStyle={{ color: item.color }}
                prefix={item.icon}
              />
            </Col>
          ))}
        </Row>
      </Card>

      <Card title="跑道状态">
        {runways.length === 0 ? (
          <Empty description="暂无跑道数据" />
        ) : (
          <Table
            rowKey="id"
            loading={loading}
            dataSource={runways}
            columns={columns}
            pagination={false}
            rowClassName={(record) => (record.isFrozen === 1 ? 'top-event' : '')}
          />
        )}
      </Card>
    </div>
  );
};

export default RunwayManage;
