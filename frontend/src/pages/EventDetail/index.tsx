import React, { useState, useEffect } from 'react';
import {
  Card, Descriptions, Tag, Button, Space, Image, Timeline, Upload, message, Modal,
  Form, Input, Select, Radio, List, Typography, Divider, Row, Col, Popconfirm, Alert, DatePicker, Badge
} from 'antd';
import {
  ArrowLeftOutlined, UploadOutlined, CheckCircleOutlined, CloseCircleOutlined,
  PlayCircleOutlined, LockOutlined, MergeCellsOutlined, FileSearchOutlined
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { eventApi } from '@/api/event';
import { photoApi } from '@/api/photo';
import { eventLogApi, runwayApi, clearanceApi } from '@/api';
import type { FodEvent, FodPhoto, FodEventLog, FodClearance, Runway, FodReview } from '@/types';
import {
  EventStatusMap, RiskLevelMap, PhotoTypeMap, RoleMap, RunwayStatusMap,
  EventStatusEnum, RiskLevelEnum, RoleEnum, PhotoTypeEnum, ClearanceOperationMap, ReviewTypeMap,
} from '@/constants';
import { useUser } from '@/context/UserContext';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { Dragger } = Upload;

const EventDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { userRole, userId, userName } = useUser();
  const [event, setEvent] = useState<FodEvent | null>(null);
  const [photos, setPhotos] = useState<FodPhoto[]>([]);
  const [logs, setLogs] = useState<FodEventLog[]>([]);
  const [clearances, setClearances] = useState<FodClearance[]>([]);
  const [runway, setRunway] = useState<Runway | null>(null);
  const [reviews, setReviews] = useState<FodReview[]>([]);
  const [mergedChildren, setMergedChildren] = useState<FodEvent[]>([]);
  const [mergedParent, setMergedParent] = useState<FodEvent | null>(null);
  const [loading, setLoading] = useState(false);
  const [evaluateVisible, setEvaluateVisible] = useState(false);
  const [handleVisible, setHandleVisible] = useState(false);
  const [closeVisible, setCloseVisible] = useState(false);
  const [clearanceVisible, setClearanceVisible] = useState(false);
  const [riskVisible, setRiskVisible] = useState(false);
  const [reviewVisible, setReviewVisible] = useState(false);
  const [form] = Form.useForm();
  const [handleForm] = Form.useForm();
  const [closeForm] = Form.useForm();
  const [clearanceForm] = Form.useForm();
  const [reviewForm] = Form.useForm();
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadData(Number(id));
    }
  }, [id]);

  const loadData = async (eventId: number) => {
    setLoading(true);
    try {
      const [eventRes, photoRes, logRes, clearanceRes] = await Promise.all([
        eventApi.getDetail(eventId),
        photoApi.getByEventId(eventId),
        eventLogApi.getByEventId(eventId),
        clearanceApi.getByEventId(eventId),
      ]);
      setEvent(eventRes.data || null);
      setPhotos(photoRes.data || []);
      setLogs(logRes.data || []);
      setClearances(clearanceRes.data || []);
      if (eventRes.data) {
        const [runwayRes, reviewRes] = await Promise.all([
          runwayApi.getById(eventRes.data.runwayId),
          eventApi.getReviews(eventRes.data.id),
        ]);
        setRunway(runwayRes.data || null);
        setReviews(reviewRes.data || []);

        if (eventRes.data.mergeCount > 0) {
          const childRes = await eventApi.getMergedChildren(eventRes.data.id);
          setMergedChildren(childRes.data || []);
        }
        if (eventRes.data.mergedParentId) {
          const parentRes = await eventApi.getMergedParent(eventRes.data.id);
          setMergedParent(parentRes.data || null);
        }
      }
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (fileList: File[]) => {
    if (!event) return;
    try {
      const res = await photoApi.upload(event.id, fileList, PhotoTypeEnum.REPORT_PHOTO, userId, userName);
      message.success('上传成功');
      setPhotos((prev) => [...prev, ...(res.data || [])]);
      loadData(event.id);
    } catch (e) {
      // error handled
    }
  };

  const handleEvaluate = async (values: any) => {
    if (!event) return;
    try {
      await eventApi.evaluate({
        eventId: event.id,
        riskLevel: values.riskLevel,
        affectTakeoff: values.affectTakeoff,
        evaluateOpinion: values.evaluateOpinion,
        evaluatorId: userId,
        evaluatorName: userName,
      });
      message.success('评估完成');
      setEvaluateVisible(false);
      form.resetFields();
      loadData(event.id);
    } catch (e) {}
  };

  const handleStartHandle = async () => {
    if (!event) return;
    try {
      await eventApi.startHandle(event.id, userId, userName);
      message.success('已开始处理');
      loadData(event.id);
    } catch (e) {}
  };

  const handleCompleteHandle = async (values: any) => {
    if (!event) return;
    try {
      await eventApi.completeHandle({
        eventId: event.id,
        handleResult: values.handleResult,
        handlerId: userId,
        handlerName: userName,
        estimatedRecoveryTime: values.estimatedRecoveryTime
          ? dayjs(values.estimatedRecoveryTime).format('YYYY-MM-DD HH:mm:ss')
          : undefined,
      });
      message.success('处理完成');
      setHandleVisible(false);
      handleForm.resetFields();
      loadData(event.id);
    } catch (e) {}
  };

  const handleClose = async (values: any) => {
    if (!event) return;
    if (event.hasPhoto !== 1) {
      message.error('缺少现场照片，不能关闭事件');
      return;
    }
    try {
      await eventApi.close({
        eventId: event.id,
        closeOpinion: values.closeOpinion,
        closerId: userId,
        closerName: userName,
      });
      message.success('事件已关闭');
      setCloseVisible(false);
      closeForm.resetFields();
      loadData(event.id);
    } catch (e) {}
  };

  const handleClearance = async (values: any) => {
    if (!event) return;
    try {
      await clearanceApi.operate({
        eventId: event.id,
        operationType: values.operationType,
        reason: values.reason,
        operatorId: userId,
        operatorName: userName,
        remark: values.remark,
      });
      message.success('操作成功');
      setClearanceVisible(false);
      clearanceForm.resetFields();
      loadData(event.id);
    } catch (e) {}
  };

  const handleRiskLevel = async (riskLevel: number) => {
    if (!event) return;
    try {
      await eventApi.updateRiskLevel(event.id, riskLevel, userId, userName);
      message.success('风险等级已更新');
      setRiskVisible(false);
      loadData(event.id);
    } catch (e) {}
  };

  const handleCancel = async () => {
    if (!event) return;
    try {
      await eventApi.cancel(event.id, userId, userName);
      message.success('已取消');
      loadData(event.id);
    } catch (e) {}
  };

  const handleAddReview = async (values: any) => {
    if (!event) return;
    try {
      await eventApi.addReview({
        eventId: event.id,
        reviewContent: values.reviewContent,
        reviewType: values.reviewType,
        reviewerId: userId,
        reviewerName: userName,
        attachmentUrls: values.attachmentUrls,
        remark: values.remark,
      });
      message.success('复盘记录已追加');
      setReviewVisible(false);
      reviewForm.resetFields();
      loadData(event.id);
    } catch (e) {}
  };

  const canEvaluate = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.TOWER_CONTROLLER &&
      (event.status === EventStatusEnum.REPORTED || event.status === EventStatusEnum.EVALUATING)
    );
  };

  const canHandle = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.MAINTENANCE_TEAM &&
      (event.status === EventStatusEnum.NOT_AFFECT || event.status === EventStatusEnum.AFFECT)
    );
  };

  const canCompleteHandle = () => {
    if (!event) return false;
    return userRole === RoleEnum.MAINTENANCE_TEAM && event.status === EventStatusEnum.HANDLING;
  };

  const canClose = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.TOWER_CONTROLLER &&
      event.status === EventStatusEnum.PENDING_CLOSE
    );
  };

  const canClearance = () => {
    return userRole === RoleEnum.TOWER_CONTROLLER;
  };

  const canChangeRisk = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.TOWER_CONTROLLER &&
      event.riskLevelLocked !== 1 &&
      (event.status === EventStatusEnum.REPORTED ||
       event.status === EventStatusEnum.EVALUATING ||
       event.status === EventStatusEnum.NOT_AFFECT ||
       event.status === EventStatusEnum.AFFECT)
    );
  };

  const canCancel = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.FIELD_INSPECTOR &&
      event.status <= EventStatusEnum.EVALUATING
    );
  };

  const canAddReview = () => {
    if (!event) return false;
    return (
      userRole === RoleEnum.TOWER_CONTROLLER &&
      event.riskLevelLocked === 1
    );
  };

  const renderStatusTag = (status: number) => {
    const info = EventStatusMap[status];
    return <Tag color={info.color}>{info.label}</Tag>;
  };

  const renderRiskTag = (level?: number) => {
    if (!level) return <Tag>-</Tag>;
    const info = RiskLevelMap[level];
    return <Tag color={info.color}>{info.label}风险</Tag>;
  };

  const renderRunwayStatus = () => {
    if (!runway) return null;
    const info = RunwayStatusMap[runway.status];
    if (!info) return null;
    if (runway.status === 1) return null;
    return <Tag color={info.color} style={{ marginLeft: 8 }}>{info.label}</Tag>;
  };

  if (!event) {
    return <div style={{ textAlign: 'center', padding: 50 }}>加载中...</div>;
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/event/list')}>
          返回列表
        </Button>
        {event.isTop === 1 && (
          <Tag color="red" icon={<LockOutlined />}>
            已置顶 - 影响起降
          </Tag>
        )}
        {event.riskLevelLocked === 1 && (
          <Tag color="orange" icon={<LockOutlined />}>
            风险等级已锁定
          </Tag>
        )}
        {event.mergeCount > 0 && (
          <Tag color="purple" icon={<MergeCellsOutlined />}>
            重点事件 · 已合并{event.mergeCount}条
          </Tag>
        )}
        {event.mergedParentId && (
          <Tag color="cyan" icon={<MergeCellsOutlined />}>
            已合并到父事件
          </Tag>
        )}
      </Space>

      <Row gutter={16}>
        <Col span={16}>
          <Card title="事件详情" loading={loading} style={{ marginBottom: 16 }}>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="事件编号">
                <code style={{ color: '#1890ff' }}>{event.eventNo}</code>
              </Descriptions.Item>
              <Descriptions.Item label="状态">{renderStatusTag(event.status)}</Descriptions.Item>
              <Descriptions.Item label="跑道">
                {runway?.runwayName || event.runwayCode}
                {renderRunwayStatus()}
              </Descriptions.Item>
              <Descriptions.Item label="风险等级">
                {renderRiskTag(event.riskLevel)}
                {canChangeRisk() && (
                  <Button type="link" size="small" onClick={() => setRiskVisible(true)}>
                    修改
                  </Button>
                )}
              </Descriptions.Item>
              <Descriptions.Item label="位置">{event.location}</Descriptions.Item>
              <Descriptions.Item label="坐标">{event.locationPoint || '-'}</Descriptions.Item>
              <Descriptions.Item label="异物类型">{event.fodType || '-'}</Descriptions.Item>
              <Descriptions.Item label="异物大小">{event.fodSize || '-'}</Descriptions.Item>
              <Descriptions.Item label="影响起降">
                {event.affectTakeoff === 1 ? '是' : event.affectTakeoff === 0 ? '否' : '未评估'}
              </Descriptions.Item>
              <Descriptions.Item label="描述">{event.description || '-'}</Descriptions.Item>
              <Descriptions.Item label="上报人">{event.reporterName || '-'}</Descriptions.Item>
              <Descriptions.Item label="上报时间">
                {event.reportTime ? dayjs(event.reportTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="评估人">{event.evaluatorName || '-'}</Descriptions.Item>
              <Descriptions.Item label="评估时间">
                {event.evaluateTime ? dayjs(event.evaluateTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="处理人">{event.handlerName || '-'}</Descriptions.Item>
              <Descriptions.Item label="处理时间">
                {event.handleStartTime
                  ? `${dayjs(event.handleStartTime).format('YYYY-MM-DD HH:mm')} ~ ${
                      event.handleEndTime ? dayjs(event.handleEndTime).format('YYYY-MM-DD HH:mm') : '进行中'
                    }`
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="处理结果">{event.handleResult || '-'}</Descriptions.Item>
              <Descriptions.Item label="预计恢复时间">
                {event.estimatedRecoveryTime
                  ? dayjs(event.estimatedRecoveryTime).format('YYYY-MM-DD HH:mm:ss')
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="关闭人">{event.closerName || '-'}</Descriptions.Item>
              <Descriptions.Item label="关闭时间">
                {event.closeTime ? dayjs(event.closeTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="照片数量" span={2}>
                {event.photoCount} 张 {event.hasPhoto === 0 && <Text type="danger">(缺少照片无法关闭)</Text>}
              </Descriptions.Item>
            </Descriptions>

            <Divider />

            <Title level={5}>操作按钮</Title>
            <Space wrap>
              {canEvaluate() && (
                <Button type="primary" onClick={() => setEvaluateVisible(true)}>
                  <CheckCircleOutlined /> 塔台评估
                </Button>
              )}
              {canHandle() && (
                <Button type="primary" onClick={handleStartHandle}>
                  <PlayCircleOutlined /> 开始处理
                </Button>
              )}
              {canCompleteHandle() && (
                <Button type="primary" onClick={() => setHandleVisible(true)}>
                  <CheckCircleOutlined /> 完成处理
                </Button>
              )}
              {canClose() && (
                <Button type="primary" danger={!event.hasPhoto} onClick={() => setCloseVisible(true)}>
                  {event.hasPhoto ? (
                    <>
                      <CheckCircleOutlined /> 关闭事件
                    </>
                  ) : (
                    <>
                      <CloseCircleOutlined /> 缺少照片无法关闭
                    </>
                  )}
                </Button>
              )}
              {canClearance() && (
                <Button onClick={() => setClearanceVisible(true)}>
                  <LockOutlined /> 放行操作
                </Button>
              )}
              {canAddReview() && (
                <Button onClick={() => setReviewVisible(true)}>
                  <FileSearchOutlined /> 追加复盘
                </Button>
              )}
              {canCancel() && (
                <Popconfirm title="确认取消该事件？" onConfirm={handleCancel}>
                  <Button danger>
                    <CloseCircleOutlined /> 取消事件
                  </Button>
                </Popconfirm>
              )}
            </Space>
          </Card>

          {event.mergeCount > 0 && (
            <Card
              title={<><MergeCellsOutlined /> 合并事件 ({event.mergeCount}条)</>}
              style={{ marginBottom: 16 }}
            >
              <List
                size="small"
                dataSource={mergedChildren}
                renderItem={(child) => (
                  <List.Item
                    extra={
                      <Button type="link" size="small" onClick={() => navigate(`/event/${child.id}`)}>
                        查看
                      </Button>
                    }
                  >
                    <List.Item.Meta
                      title={<code style={{ color: '#1890ff' }}>{child.eventNo}</code>}
                      description={`${child.fodType || '-'} · ${dayjs(child.reportTime).format('YYYY-MM-DD HH:mm')} · ${child.reporterName || '-'}`}
                    />
                  </List.Item>
                )}
              />
            </Card>
          )}

          {mergedParent && (
            <Card
              title={<><MergeCellsOutlined /> 所属父事件</>}
              style={{ marginBottom: 16 }}
            >
              <Space>
                <Text>本事件已合并到：</Text>
                <Button type="link" onClick={() => navigate(`/event/${mergedParent.id}`)}>
                  {mergedParent.eventNo}
                </Button>
                <Tag color="purple">合并{mergedParent.mergeCount}条</Tag>
              </Space>
            </Card>
          )}

          <Card title="现场照片" style={{ marginBottom: 16 }}>
            {photos.length === 0 ? (
              <div style={{ textAlign: 'center', padding: 24, color: '#999' }}>
                暂无照片，{userRole === RoleEnum.FIELD_INSPECTOR && event.status <= EventStatusEnum.HANDLING ? (
                  <span>请上传现场照片</span>
                ) : (
                  <span>未上传照片</span>
                )}
              </div>
            ) : (
              <div className="photo-grid">
                {photos.map((photo) => (
                  <div
                    key={photo.id}
                    className="photo-item"
                    onClick={() => {
                      setPreviewImage(photo.photoUrl);
                      setPreviewVisible(true);
                    }}
                  >
                    <Image src={photo.photoUrl} alt={photo.fileName} preview={false} />
                  </div>
                ))}
              </div>
            )}

            {userRole === RoleEnum.FIELD_INSPECTOR && event.status <= EventStatusEnum.HANDLING && (
              <div style={{ marginTop: 16 }}>
                <Dragger
                  multiple
                  showUploadList={false}
                  beforeUpload={(file) => {
                    handleUpload([file as File]);
                    return false;
                  }}
                  accept="image/*"
                >
                  <p className="ant-upload-drag-icon">
                    <UploadOutlined />
                  </p>
                  <p className="ant-upload-text">点击或拖拽上传现场照片</p>
                  <p className="ant-upload-hint">支持 jpg、png 等图片格式</p>
                </Dragger>
              </div>
            )}
          </Card>

          <Card title="复盘记录" style={{ marginBottom: 16 }}
            extra={canAddReview() && (
              <Button size="small" icon={<FileSearchOutlined />} onClick={() => setReviewVisible(true)}>
                追加复盘
              </Button>
            )}
          >
            {reviews.length === 0 ? (
              <div style={{ textAlign: 'center', padding: 24, color: '#999' }}>暂无复盘记录</div>
            ) : (
              <Timeline
                items={reviews.map((review) => {
                  const typeInfo = ReviewTypeMap[review.reviewType] || ReviewTypeMap[1];
                  return {
                    color: typeInfo.color,
                    children: (
                      <div>
                        <Space>
                          <Tag color={typeInfo.color}>{typeInfo.label}</Tag>
                          <Text strong>{review.reviewerName}</Text>
                          <Text type="secondary">
                            {dayjs(review.reviewTime).format('YYYY-MM-DD HH:mm:ss')}
                          </Text>
                        </Space>
                        <div style={{ marginTop: 8, whiteSpace: 'pre-wrap' }}>{review.reviewContent}</div>
                        {review.remark && (
                          <div style={{ marginTop: 4, color: '#999', fontSize: 12 }}>备注: {review.remark}</div>
                        )}
                      </div>
                    ),
                  };
                })}
              />
            )}
          </Card>

          <Card title="操作日志">
            {logs.length === 0 ? (
              <div style={{ textAlign: 'center', padding: 24, color: '#999' }}>暂无操作日志</div>
            ) : (
              <Timeline
                className="status-timeline"
                items={logs.map((log) => ({
                  color: log.afterStatus === EventStatusEnum.CLOSED ? 'green' :
                         log.afterStatus === EventStatusEnum.CANCELLED ? 'gray' : 'blue',
                  children: (
                    <div>
                      <Space>
                        <Text strong>
                          {log.operatorName} ({RoleMap[log.operatorRole || ''] || log.operatorRole})
                        </Text>
                        <Text type="secondary">
                          {dayjs(log.operateTime).format('YYYY-MM-DD HH:mm:ss')}
                        </Text>
                      </Space>
                      <div style={{ marginTop: 4 }}>{log.content}</div>
                      {log.beforeStatus && log.afterStatus && (
                        <div style={{ marginTop: 4 }}>
                          状态变更: {EventStatusMap[log.beforeStatus].label} →{' '}
                          {EventStatusMap[log.afterStatus].label}
                        </div>
                      )}
                    </div>
                  ),
                }))}
              />
            )}
          </Card>
        </Col>

        <Col span={8}>
          <Card title="放行记录" style={{ marginBottom: 16 }}>
            {clearances.length === 0 ? (
              <div style={{ textAlign: 'center', padding: 24, color: '#999' }}>暂无放行记录</div>
            ) : (
              <List
                size="small"
                dataSource={clearances}
                renderItem={(item) => {
                  const opLabel = ClearanceOperationMap[item.operationType] || `操作${item.operationType}`;
                  const isRestrict = item.operationType === 5 || item.operationType === 1 || item.operationType === 4;
                  return (
                    <List.Item>
                      <List.Item.Meta
                        title={
                          <Space>
                            <Tag color={isRestrict ? 'red' : 'green'}>
                              {opLabel}
                            </Tag>
                            <Text>{item.operatorName}</Text>
                          </Space>
                        }
                        description={
                          <div>
                            <div>{item.reason}</div>
                            {item.remark && <div style={{ color: '#999', fontSize: 12, fontStyle: 'italic' }}>{item.remark}</div>}
                            <div style={{ color: '#999', fontSize: 12 }}>
                              {dayjs(item.operateTime).format('YYYY-MM-DD HH:mm:ss')}
                            </div>
                          </div>
                        }
                      />
                    </List.Item>
                  );
                }}
              />
            )}
          </Card>

          <Card title="当前用户">
            <Space direction="vertical" style={{ width: '100%' }}>
              <div>
                <Text type="secondary">角色：</Text>
                <Tag color="blue">{RoleMap[userRole]}</Tag>
              </div>
              <div>
                <Text type="secondary">用户：</Text>
                <Text strong>{userName}</Text>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>

      <Modal
        title="塔台评估"
        open={evaluateVisible}
        onCancel={() => setEvaluateVisible(false)}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleEvaluate}>
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

      <Modal
        title="完成处理"
        open={handleVisible}
        onCancel={() => setHandleVisible(false)}
        footer={null}
        width={600}
      >
        <Form form={handleForm} layout="vertical" onFinish={handleCompleteHandle}>
          <Form.Item
            name="handleResult"
            label="处理结果"
            rules={[{ required: true, message: '请输入处理结果' }]}
          >
            <TextArea rows={4} placeholder="请详细描述处理结果" />
          </Form.Item>
          <Form.Item
            name="estimatedRecoveryTime"
            label="预计恢复时间"
          >
            <DatePicker
              showTime
              style={{ width: '100%' }}
              placeholder="请选择预计恢复时间"
            />
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
        title="关闭事件"
        open={closeVisible}
        onCancel={() => setCloseVisible(false)}
        footer={null}
      >
        <Form form={closeForm} layout="vertical" onFinish={handleClose}>
          {event.hasPhoto !== 1 && (
            <Alert
              type="error"
              message="缺少现场照片，不能关闭事件"
              showIcon
              style={{ marginBottom: 16 }}
            />
          )}
          <Form.Item name="closeOpinion" label="关闭意见">
            <TextArea rows={3} placeholder="请输入关闭意见" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" disabled={event.hasPhoto !== 1}>
                确认关闭
              </Button>
              <Button onClick={() => setCloseVisible(false)}>取消</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="放行操作"
        open={clearanceVisible}
        onCancel={() => setClearanceVisible(false)}
        footer={null}
      >
        <Form form={clearanceForm} layout="vertical" onFinish={handleClearance}>
          <Form.Item
            name="operationType"
            label="操作类型"
            rules={[{ required: true, message: '请选择操作类型' }]}
          >
            <Select placeholder="请选择操作类型">
              <Option value={1}>冻结跑道</Option>
              <Option value={2}>解除冻结</Option>
              <Option value={3}>允许放行</Option>
              <Option value={4}>禁止放行</Option>
              <Option value={5}>限制跑道</Option>
              <Option value={6}>解除限制</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="reason"
            label="操作原因"
            rules={[{ required: true, message: '请输入操作原因' }]}
          >
            <TextArea rows={3} placeholder="请输入操作原因" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <TextArea rows={2} placeholder="备注信息" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                确认操作
              </Button>
              <Button onClick={() => setClearanceVisible(false)}>取消</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="修改风险等级"
        open={riskVisible}
        onCancel={() => setRiskVisible(false)}
        footer={null}
      >
        <Radio.Group
          defaultValue={event.riskLevel}
          onChange={(e) => handleRiskLevel(e.target.value)}
        >
          <Radio value={RiskLevelEnum.LOW} style={{ color: RiskLevelMap[1].color, display: 'block' }}>
            低风险
          </Radio>
          <Radio value={RiskLevelEnum.MEDIUM} style={{ color: RiskLevelMap[2].color, display: 'block' }}>
            中风险
          </Radio>
          <Radio value={RiskLevelEnum.HIGH} style={{ color: RiskLevelMap[3].color, display: 'block' }}>
            高风险
          </Radio>
          <Radio value={RiskLevelEnum.EXTREME} style={{ color: RiskLevelMap[4].color, display: 'block' }}>
            极高风险
          </Radio>
        </Radio.Group>
      </Modal>

      <Modal
        title="追加复盘记录"
        open={reviewVisible}
        onCancel={() => setReviewVisible(false)}
        footer={null}
        width={600}
      >
        <Form form={reviewForm} layout="vertical" onFinish={handleAddReview}>
          <Form.Item
            name="reviewType"
            label="复盘类型"
            initialValue={1}
          >
            <Radio.Group>
              <Radio value={1}>一般复盘</Radio>
              <Radio value={2}>重点复盘</Radio>
              <Radio value={3}>整改措施</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            name="reviewContent"
            label="复盘内容"
            rules={[{ required: true, message: '请输入复盘内容' }]}
          >
            <TextArea rows={5} placeholder="请详细描述复盘内容" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <TextArea rows={2} placeholder="可选备注" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                确认追加
              </Button>
              <Button onClick={() => setReviewVisible(false)}>取消</Button>
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

export default EventDetail;
