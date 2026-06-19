import React, { useState, useEffect } from 'react';
import {
  Card, Form, Input, Select, Button, Space, Upload, message, Row, Col, Typography
} from 'antd';
import { ArrowLeftOutlined, UploadOutlined, PlusOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { UploadFile } from 'antd/es/upload/interface';
import { eventApi } from '@/api/event';
import { photoApi } from '@/api/photo';
import { runwayApi } from '@/api';
import type { Runway } from '@/types';
import { FodTypeOptions, FodSizeOptions, PhotoTypeEnum } from '@/constants';
import { useUser } from '@/context/UserContext';

const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { Dragger } = Upload;

const EventReport: React.FC = () => {
  const navigate = useNavigate();
  const { userId, userName } = useUser();
  const [form] = Form.useForm();
  const [runways, setRunways] = useState<Runway[]>([]);
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadRunways();
  }, []);

  const loadRunways = async () => {
    const res = await runwayApi.getAll();
    setRunways(res.data || []);
  };

  const handleSubmit = async (values: any) => {
    setSubmitting(true);
    try {
      const eventRes = await eventApi.report({
        ...values,
        reporterId: userId,
        reporterName: userName,
      });

      if (eventRes.data && fileList.length > 0) {
        const files = fileList.map((f) => f.originFileObj as File).filter(Boolean);
        if (files.length > 0) {
          await photoApi.upload(
            eventRes.data,
            files,
            PhotoTypeEnum.REPORT_PHOTO,
            userId,
            userName,
            '上报时上传的现场照片'
          );
        }
      }

      message.success('上报成功');
      navigate('/event/list');
    } catch (e) {
      // error handled
    } finally {
      setSubmitting(false);
    }
  };

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
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/event/list')}>
          返回列表
        </Button>
      </Space>

      <Card title="上报异物">
        <Title level={5}>事件信息</Title>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="runwayId"
                label="跑道"
                rules={[{ required: true, message: '请选择跑道' }]}
              >
                <Select placeholder="请选择跑道">
                  {runways.map((r) => (
                    <Option key={r.id} value={r.id}>
                      {r.runwayName}
                      {r.isFrozen === 1 && <span style={{ color: '#f5222d' }}>（已冻结）</span>}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="location"
                label="具体位置"
                rules={[{ required: true, message: '请输入具体位置' }]}
              >
                <Input placeholder="例如：01号跑道东端 1500米处" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="locationPoint" label="位置坐标">
                <Input placeholder="例如：116.5845, 40.0799" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="fodType" label="异物类型">
                <Select placeholder="请选择异物类型">
                  {FodTypeOptions.map((opt) => (
                    <Option key={opt.value} value={opt.value}>
                      {opt.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="fodSize" label="异物大小">
                <Select placeholder="请选择异物大小">
                  {FodSizeOptions.map((opt) => (
                    <Option key={opt.value} value={opt.value}>
                      {opt.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="description" label="详细描述">
            <TextArea rows={3} placeholder="请详细描述发现的异物情况" />
          </Form.Item>

          <Title level={5} style={{ marginTop: 24 }}>
            现场照片
          </Title>
          <Form.Item label="上传照片（建议上传多角度照片）">
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
              <Button type="primary" htmlType="submit" loading={submitting} icon={<PlusOutlined />}>
                提交上报
              </Button>
              <Button onClick={() => navigate('/event/list')}>取消</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default EventReport;
