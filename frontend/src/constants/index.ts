export const EventStatusEnum = {
  PENDING_REPORT: 1,
  REPORTED: 2,
  EVALUATING: 3,
  NOT_AFFECT: 4,
  AFFECT: 5,
  HANDLING: 6,
  PENDING_CLOSE: 7,
  CLOSED: 8,
  CANCELLED: 9,
} as const;

export const EventStatusMap: Record<number, { label: string; color: string }> = {
  1: { label: '待上报', color: 'default' },
  2: { label: '已上报待评估', color: 'processing' },
  3: { label: '评估中', color: 'processing' },
  4: { label: '不影响起降', color: 'success' },
  5: { label: '影响起降', color: 'error' },
  6: { label: '处理中', color: 'warning' },
  7: { label: '待关闭', color: 'processing' },
  8: { label: '已关闭', color: 'default' },
  9: { label: '已取消', color: 'default' },
};

export const RiskLevelEnum = {
  LOW: 1,
  MEDIUM: 2,
  HIGH: 3,
  EXTREME: 4,
} as const;

export const RiskLevelMap: Record<number, { label: string; color: string }> = {
  1: { label: '低', color: 'success' },
  2: { label: '中', color: 'warning' },
  3: { label: '高', color: 'orange' },
  4: { label: '极高', color: 'error' },
};

export const RoleEnum = {
  FIELD_INSPECTOR: 'FIELD_INSPECTOR',
  TOWER_CONTROLLER: 'TOWER_CONTROLLER',
  MAINTENANCE_TEAM: 'MAINTENANCE_TEAM',
} as const;

export const RoleMap: Record<string, string> = {
  FIELD_INSPECTOR: '场务巡查',
  TOWER_CONTROLLER: '塔台协调',
  MAINTENANCE_TEAM: '维修班组',
};

export const RunwayStatusEnum = {
  NORMAL: 1,
  FROZEN: 2,
  MAINTENANCE: 3,
} as const;

export const RunwayStatusMap: Record<number, { label: string; color: string }> = {
  1: { label: '正常', color: 'success' },
  2: { label: '冻结', color: 'error' },
  3: { label: '维修中', color: 'warning' },
};

export const PhotoTypeEnum = {
  REPORT_PHOTO: 1,
  HANDLING_PHOTO: 2,
  COMPLETED_PHOTO: 3,
} as const;

export const PhotoTypeMap: Record<number, string> = {
  1: '上报照片',
  2: '处理中照片',
  3: '处理后照片',
};

export const ClearanceOperationEnum = {
  FREEZE_RUNWAY: 1,
  UNFREEZE_RUNWAY: 2,
  ALLOW_CLEARANCE: 3,
  DENY_CLEARANCE: 4,
} as const;

export const ClearanceOperationMap: Record<number, string> = {
  1: '冻结跑道',
  2: '解除冻结',
  3: '允许放行',
  4: '禁止放行',
};

export const FodTypeOptions = [
  { label: '金属碎片', value: '金属碎片' },
  { label: '塑料制品', value: '塑料制品' },
  { label: '橡胶轮胎碎片', value: '橡胶轮胎碎片' },
  { label: '鸟类残骸', value: '鸟类残骸' },
  { label: '建筑材料', value: '建筑材料' },
  { label: '其他', value: '其他' },
];

export const FodSizeOptions = [
  { label: '小型 (<5cm)', value: '小型 (<5cm)' },
  { label: '中型 (5-15cm)', value: '中型 (5-15cm)' },
  { label: '大型 (>15cm)', value: '大型 (>15cm)' },
];
