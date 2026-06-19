import React, { useState, useEffect } from 'react';
import {
  Table, Tag, Button, Space, Card, Row, Col, Statistic, Modal, Form, Input,
  message, Empty, Upload, List, Image
} from 'antd';
import {
  EyeOutlined, PlayCircleOutlined, CheckCircleOutlined, UploadOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { eventApi } from '@/api/event';
import { photoApi } from '@/api/photo';
import { runwayApi } from '@/api';
import type { FodEvent, Runway, FodPhoto } from '@/types';
import {
  EventStatusMap, RiskLevelMap, EventStatusEnum, RoleEnum, PhotoTypeEnum,
} from '@/constants';
import { useUser } from '@/context/UserContext';
import type { UploadFile } from 'antd/es/upload/interface';

const { TextArea } = Input;
const { Dragger } = Upload;

const MaintenanceHandle: React.FC = () => {
  const navigate = useNavigate();
  const { userRole, userId, userName } = useUser();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<FodEvent[]>([]);
  const [runways, setRunways] = useState<Runway[]>([]);
  const [handleVisible, setHandleVisible] = useState(false);
  const [currentEvent, setCurrentEvent] = useState<FodEvent | null>(null);
  const [handleForm] = Form.useForm();
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [statistics, setStatistics] = useState<Record<string, number>>({});
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [photos, setPhotos] = useState<FodPhoto[]>([]);

  useEffect(() => {
    if (userRole !== RoleEnum.MAINTENANCE_TEAM) {
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
        (e) => e.status === EventStatusEnum.NOT_AFFECT ||
               e.status === EventStatusEnum.AFFECT ||
               e.status === EventStatusEnum.HANDLING ||
               e.status === EventStatusEnum.PENDING_CLOSE
      );
      setData(filtered);
    } finally {
      setLoading(false);
    }
  };

  const handleStartHandle = async (record: FodEvent) => {
    try {
      await eventApi.startHandle(record.id, userId, userName);
      message.success('已开始处理');
      loadData();
      loadStatistics();
    } catch (e) {
      // error handled
    }
  };

  const handleCompleteHandle = (record: FodEvent) => {
    setCurrentEvent(record);
    setFileList([]);
    handleForm.resetFields();
    setHandleVisible(true);
  };

  const handleSubmitComplete = async (values: any) => {
    if (!currentEvent) return;
    try {
      await eventApi.completeHandle({
        eventId: currentEvent.id,
        handleResult: values.handleResult,
        handlerId: userId,
        handlerName: userName,
      });

      if (fileList.length > 0) {
        const files = fileList.map((f) => f.originFileObj as File).filter(Boolean);
        if (files.length > 0) {
          await photoApi.upload(
            currentEvent.id,
            files,
            PhotoTypeEnum.COMPLETED_PHOTO,
            userId,
            userName,
            '处理完成后照片'
          );
        }
      }

      message.success('处理完成');
      setHandleVisible(false);
      handleForm.resetFields();
      setFileList([]);
      loadData();
      loadStatistics();
    } catch (e) {
      // error handled
    }
  };

  const loadPhotos = async (eventId: number) => {
    const res = await photoApi.getByEventId(eventId);
    setPhotos(res.data || []);
  };

  const getRunwayName = (code?: string) => {
    const runway = runways.find((r) => r.runwayCode === code);
    return runway?.runwayName || code;
  };

  const statCards = [
    { title: '待处理', value: (statistics.notAffect || 0) + (statistics.affect || 0), color: '#faad14' },
    { title: '处理中', value: statistics.handling || 0, color: '#1890ff' },
    { title: '待关闭', value: statistics.pendingClose || 0, color: '#722ed1' },
    { title: '已关闭', value: statistics.closed || 0, color: '#52c41a' },
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
      title: '是否影响起降',
      dataIndex: 'affectTakeoff',
      width: 120,
      render: (val: number) => (
        <Tag color={val === 1 ? 'red' : 'green'}>{val === 1 ? '是' : '否'}</Tag>
      ),
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
      width: 200,
      fixed: 'right' as const,
      render: (_: any, record: FodEvent) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => navigate(`/event/${record.id}`)}>
            <EyeOutlined /> 详情
          </Button>
          {(record.status === EventStatusEnum.NOT_AFFECT || record.status === EventStatusEnum.AFFECT) && (
            <Button
              type="primary"
              size="small"
              onClick={() => handleStartHandle(record)}
            >
              <PlayCircleOutlined /> 开始处理
            </Button>
          )}
          {record.status === EventStatusEnum.HANDLING && (
            <Button
              type="primary"
              size="small"
              onClick={() => handleCompleteHandle(record)}
            >
              <CheckCircleOutlined /> 完成处理
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const uploadProps = {
    fileList,
    onChange: ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
      setFileList(newFileList);
    },
    beforeUpload: () => false,
    multiple: true,
    accept: 'image/*',
  };

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

      <Card title="维修处理 - 待处理事件">
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
        title="完成处理"
        open={handleVisible}
        onCancel={() => setHandleVisible(false)}
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
        <Form form={handleForm} layout="vertical" onFinish={handleSubmitComplete}>
          <Form.Item
            name="handleResult"
            label="处理结果"
            rules={[{ required: true, message: '请输入处理结果' }]}
          >
            <TextArea rows={4} placeholder="请详细描述处理结果" />
          </Form.Item>

          <Form.Item label="上传处理后照片（可选）">
            <Dragger {...uploadProps}>
              <p className="ant-upload-drag-icon">
                <UploadOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
              <p className="ant-upload-hint">支持 jpg、png、gif 等图片格式，可多选</p>
            </Dragger>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                确认完成
              </Button>
              <Button onClick={() => setHandleVisible(false)}>取消</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

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

export default MaintenanceHandle;
