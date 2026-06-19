import React, { useState, useEffect } from 'react';
import {
  Table, Tag, Button, Input, Select, Space, Card, Row, Col, Statistic, Popconfirm, Modal, Image, message
} from 'antd';
import { PlusOutlined, SearchOutlined, EyeOutlined, VerticalAlignTopOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { eventApi } from '@/api/event';
import { runwayApi } from '@/api';
import type { FodEvent, Runway } from '@/types';
import {
  EventStatusMap, RiskLevelMap, RoleEnum } from '@/constants';
import { useUser } from '@/context/UserContext';

const { Search } = Input;
const { Option } = Select;

const EventList: React.FC = () => {
  const navigate = useNavigate();
  const { userRole } = useUser();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<FodEvent[]>([]);
  const [runways, setRunways] = useState<Runway[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [filters, setFilters] = useState({
    keyword: '',
    status: undefined as number | undefined,
    runwayId: undefined as number | undefined,
    riskLevel: undefined as number | undefined,
    isTop: undefined as number | undefined,
  });
  const [statistics, setStatistics] = useState<Record<string, number>>({});
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState<string | null>(null);

  useEffect(() => {
    loadRunways();
    loadStatistics();
  }, []);

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize, filters]);

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
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
        ...filters,
      };
      const res = await eventApi.getPage(params);
      setData(res.data?.records || []);
      setPagination((prev) => ({ ...prev, total: res.data?.total || 0 }));
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (value: string) => {
    setFilters((prev) => ({ ...prev, keyword: value }));
    setPagination((prev) => ({ ...prev, current: 1 }));
  };

  const handleFilterChange = (key: string, value: any) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    setPagination((prev) => ({ ...prev, current: 1 }));
  };

  const handleCancel = async (id: number) => {
    try {
      await eventApi.cancel(id);
      message.success('取消成功');
      loadData();
      loadStatistics();
    } catch (e) {
      // error already handled by interceptor
    }
  };

  const getRunwayName = (code?: string) => {
    const runway = runways.find((r) => r.runwayCode === code);
    return runway?.runwayName || code;
  };

  const columns = [
    {
      title: '置顶',
      dataIndex: 'isTop',
      width: 60,
      render: (val: number) =>
        val === 1 ? <VerticalAlignTopOutlined style={{ color: '#f5222d', fontSize: '18px' }} /> : null,
    },
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
      title: '风险等级',
      dataIndex: 'riskLevel',
      width: 100,
      render: (val: number) => {
        if (!val) return '-';
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
      title: '上报人',
      dataIndex: 'reporterName',
      width: 100,
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
          {userRole === RoleEnum.FIELD_INSPECTOR &&
            record.status <= 3 && (
              <Popconfirm
                title="确认取消该事件？"
                onConfirm={() => handleCancel(record.id)}
                okText="确认"
                cancelText="取消"
              >
                <Button type="link" size="small" danger>
                  取消
                </Button>
              </Popconfirm>
            )}
        </Space>
      ),
    },
  ];

  const statCards = [
    { title: '总数', value: statistics.total || 0, color: '#1890ff' },
    { title: '影响起降', value: statistics.affect || 0, color: '#f5222d' },
    { title: '处理中', value: statistics.handling || 0, color: '#faad14' },
    { title: '待关闭', value: statistics.pendingClose || 0, color: '#722ed1' },
    { title: '已关闭', value: statistics.closed || 0, color: '#52c41a' },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          {statCards.map((item, index) => (
            <Col span={4.8} key={index}>
              <Statistic title={item.title} value={item.value} valueStyle={{ color: item.color }} />
            </Col>
          ))}
        </Row>
      </Card>

      <Card
        title="事件列表"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/event/report')}>
            上报异物
          </Button>
        }
      >
        <Space style={{ marginBottom: 16 }} wrap>
          <Search
            placeholder="搜索事件编号、位置、描述"
            allowClear
            style={{ width: 280 }}
            onSearch={handleSearch}
            enterButton={<SearchOutlined />}
          />
          <Select
            placeholder="状态筛选"
            allowClear
            style={{ width: 160 }}
            onChange={(val) => handleFilterChange('status', val)}
          >
            {Object.entries(EventStatusMap).map(([key, val]: [string, any]) => (
              <Option key={key} value={Number(key)}>
                {val.label}
              </Option>
            ))}
          </Select>
          <Select
            placeholder="跑道筛选"
            allowClear
            style={{ width: 160 }}
            onChange={(val) => handleFilterChange('runwayId', val)}
          >
            {runways.map((r) => (
              <Option key={r.id} value={r.id}>
                {r.runwayName}
              </Option>
            ))}
          </Select>
          <Select
            placeholder="风险等级"
            allowClear
            style={{ width: 140 }}
            onChange={(val) => handleFilterChange('riskLevel', val)}
          >
            {Object.entries(RiskLevelMap).map(([key, val]: [string, any]) => (
              <Option key={key} value={Number(key)}>
                {val.label}
              </Option>
            ))}
          </Select>
          <Select
            placeholder="是否置顶"
            allowClear
            style={{ width: 140 }}
            onChange={(val) => handleFilterChange('isTop', val)}
          >
            <Option value={1}>已置顶</Option>
            <Option value={0}>未置顶</Option>
          </Select>
          <Button
            onClick={() => {
              setFilters({
                keyword: '',
                status: undefined,
                runwayId: undefined,
                riskLevel: undefined,
                isTop: undefined,
              });
              setPagination((prev) => ({ ...prev, current: 1 }));
            }}
          >
            重置
          </Button>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          dataSource={data}
          columns={columns}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) =>
              setPagination({ current: page, pageSize, total: pagination.total }),
          }}
          rowClassName={(record) => (record.isTop === 1 ? 'top-event' : '')}
        />
      </Card>

      <Modal
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width={800}
      >
        {previewImage && (
          <Image
            width="100%"
            src={previewImage}
            style={{ maxHeight: '70vh', objectFit: 'contain' }}
          />
        )}
      </Modal>
    </div>
  );
};

export default EventList;
