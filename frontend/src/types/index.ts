export interface Result<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageQuery {
  pageNum: number;
  pageSize: number;
  orderBy?: string;
  orderDirection?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
  pages: number;
}

export interface Runway {
  id: number;
  runwayCode: string;
  runwayName: string;
  length?: number;
  width?: number;
  status: number;
  isFrozen: number;
  freezeReason?: string;
  freezeTime?: string;
  freezeOperator?: string;
  description?: string;
  createTime: string;
  updateTime: string;
}

export interface FodEvent {
  id: number;
  eventNo: string;
  runwayId: number;
  runwayCode: string;
  location: string;
  locationPoint?: string;
  fodType?: string;
  fodSize?: string;
  description?: string;
  status: number;
  riskLevel?: number;
  riskLevelLocked: number;
  isTop: number;
  affectTakeoff?: number;
  reporterId?: string;
  reporterName?: string;
  reportTime?: string;
  evaluatorId?: string;
  evaluatorName?: string;
  evaluateTime?: string;
  evaluateOpinion?: string;
  handlerId?: string;
  handlerName?: string;
  handleStartTime?: string;
  handleEndTime?: string;
  handleResult?: string;
  closerId?: string;
  closerName?: string;
  closeTime?: string;
  closeOpinion?: string;
  hasPhoto: number;
  photoCount: number;
  remark?: string;
  createTime: string;
  updateTime: string;
}

export interface FodPhoto {
  id: number;
  eventId: number;
  eventNo: string;
  photoNo: string;
  photoUrl: string;
  fileName?: string;
  fileSize?: number;
  fileType?: string;
  photoType: number;
  uploaderId?: string;
  uploaderName?: string;
  uploadTime: string;
  description?: string;
  remark?: string;
}

export interface FodClearance {
  id: number;
  eventId: number;
  eventNo: string;
  runwayId: number;
  runwayCode: string;
  clearanceNo: string;
  operationType: number;
  operatorId: string;
  operatorName: string;
  operateTime: string;
  reason?: string;
  beforeStatus?: number;
  afterStatus?: number;
  remark?: string;
}

export interface FodEventLog {
  id: number;
  eventId: number;
  eventNo: string;
  operationType: string;
  operatorId?: string;
  operatorName?: string;
  operatorRole?: string;
  operateTime: string;
  beforeStatus?: number;
  afterStatus?: number;
  beforeRiskLevel?: number;
  afterRiskLevel?: number;
  content?: string;
  remark?: string;
}

export interface EventReportDTO {
  runwayId: number;
  location: string;
  locationPoint?: string;
  fodType?: string;
  fodSize?: string;
  description?: string;
  reporterId?: string;
  reporterName?: string;
}

export interface EventEvaluateDTO {
  eventId: number;
  riskLevel: number;
  affectTakeoff: number;
  evaluateOpinion?: string;
  evaluatorId?: string;
  evaluatorName?: string;
}

export interface EventHandleDTO {
  eventId: number;
  handleResult: string;
  handlerId?: string;
  handlerName?: string;
}

export interface EventCloseDTO {
  eventId: number;
  closeOpinion?: string;
  closerId?: string;
  closerName?: string;
}

export interface ClearanceOperationDTO {
  eventId: number;
  operationType: number;
  reason: string;
  operatorId?: string;
  operatorName?: string;
  remark?: string;
}

export type UserRole = 'FIELD_INSPECTOR' | 'TOWER_CONTROLLER' | 'MAINTENANCE_TEAM';
