-- 机场跑道异物巡查系统数据库设计
-- 创建日期: 2026-06-19

-- 跑道表
CREATE TABLE IF NOT EXISTS `runway` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runway_code` VARCHAR(32) NOT NULL COMMENT '跑道编号',
  `runway_name` VARCHAR(128) NOT NULL COMMENT '跑道名称',
  `length` INT DEFAULT NULL COMMENT '跑道长度(米)',
  `width` INT DEFAULT NULL COMMENT '跑道宽度(米)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 2-冻结 3-维修中',
  `is_frozen` TINYINT NOT NULL DEFAULT 0 COMMENT '是否冻结放行: 0-否 1-是',
  `freeze_reason` VARCHAR(512) DEFAULT NULL COMMENT '冻结原因',
  `freeze_time` DATETIME DEFAULT NULL COMMENT '冻结时间',
  `freeze_operator` VARCHAR(64) DEFAULT NULL COMMENT '冻结操作人',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dr` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_runway_code` (`runway_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑道表';

-- 异物事件表
CREATE TABLE IF NOT EXISTS `fod_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_no` VARCHAR(32) NOT NULL COMMENT '事件编号',
  `runway_id` BIGINT NOT NULL COMMENT '跑道ID',
  `runway_code` VARCHAR(32) NOT NULL COMMENT '跑道编号',
  `location` VARCHAR(256) NOT NULL COMMENT '异物位置描述',
  `location_point` VARCHAR(64) DEFAULT NULL COMMENT '位置坐标(经纬度)',
  `fod_type` VARCHAR(64) DEFAULT NULL COMMENT '异物类型',
  `fod_size` VARCHAR(64) DEFAULT NULL COMMENT '异物大小',
  `description` VARCHAR(1024) DEFAULT NULL COMMENT '事件描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-待上报 2-已上报待评估 3-评估中 4-不影响起降 5-影响起降 6-处理中 7-待关闭 8-已关闭 9-已取消',
  `risk_level` TINYINT DEFAULT NULL COMMENT '风险等级: 1-低 2-中 3-高 4-极高',
  `risk_level_locked` TINYINT NOT NULL DEFAULT 0 COMMENT '风险等级是否锁定: 0-否 1-是(放行后锁定)',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否 1-是(影响起降自动置顶)',
  `affect_takeoff` TINYINT DEFAULT NULL COMMENT '是否影响起降: 0-否 1-是',
  `reporter_id` VARCHAR(64) DEFAULT NULL COMMENT '上报人ID',
  `reporter_name` VARCHAR(64) DEFAULT NULL COMMENT '上报人姓名',
  `report_time` DATETIME DEFAULT NULL COMMENT '上报时间',
  `evaluator_id` VARCHAR(64) DEFAULT NULL COMMENT '评估人ID',
  `evaluator_name` VARCHAR(64) DEFAULT NULL COMMENT '评估人姓名',
  `evaluate_time` DATETIME DEFAULT NULL COMMENT '评估时间',
  `evaluate_opinion` VARCHAR(1024) DEFAULT NULL COMMENT '评估意见',
  `handler_id` VARCHAR(64) DEFAULT NULL COMMENT '处理人ID',
  `handler_name` VARCHAR(64) DEFAULT NULL COMMENT '处理人姓名',
  `handle_start_time` DATETIME DEFAULT NULL COMMENT '处理开始时间',
  `handle_end_time` DATETIME DEFAULT NULL COMMENT '处理完成时间',
  `handle_result` VARCHAR(1024) DEFAULT NULL COMMENT '处理结果',
  `closer_id` VARCHAR(64) DEFAULT NULL COMMENT '关闭人ID',
  `closer_name` VARCHAR(64) DEFAULT NULL COMMENT '关闭人姓名',
  `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `close_opinion` VARCHAR(1024) DEFAULT NULL COMMENT '关闭意见',
  `has_photo` TINYINT NOT NULL DEFAULT 0 COMMENT '是否有现场照片: 0-否 1-是',
  `photo_count` INT NOT NULL DEFAULT 0 COMMENT '照片数量',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dr` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_no` (`event_no`),
  KEY `idx_runway_id` (`runway_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异物事件表';

-- 事件照片表
CREATE TABLE IF NOT EXISTS `fod_photo` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id` BIGINT NOT NULL COMMENT '事件ID',
  `event_no` VARCHAR(32) NOT NULL COMMENT '事件编号',
  `photo_no` VARCHAR(64) NOT NULL COMMENT '照片编号',
  `photo_url` VARCHAR(512) NOT NULL COMMENT '照片访问地址',
  `file_name` VARCHAR(256) DEFAULT NULL COMMENT '原始文件名',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '文件类型',
  `photo_type` TINYINT NOT NULL DEFAULT 1 COMMENT '照片类型: 1-上报照片 2-处理中照片 3-处理后照片',
  `uploader_id` VARCHAR(64) DEFAULT NULL COMMENT '上传人ID',
  `uploader_name` VARCHAR(64) DEFAULT NULL COMMENT '上传人姓名',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '照片描述',
  `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dr` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_photo_no` (`photo_no`),
  KEY `idx_event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件照片表';

-- 放行记录表
CREATE TABLE IF NOT EXISTS `fod_clearance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id` BIGINT NOT NULL COMMENT '事件ID',
  `event_no` VARCHAR(32) NOT NULL COMMENT '事件编号',
  `runway_id` BIGINT NOT NULL COMMENT '跑道ID',
  `runway_code` VARCHAR(32) NOT NULL COMMENT '跑道编号',
  `clearance_no` VARCHAR(32) NOT NULL COMMENT '放行记录编号',
  `operation_type` TINYINT NOT NULL COMMENT '操作类型: 1-冻结跑道 2-解除冻结 3-允许放行 4-禁止放行',
  `operator_id` VARCHAR(64) NOT NULL COMMENT '操作人ID(塔台)',
  `operator_name` VARCHAR(64) NOT NULL COMMENT '操作人姓名',
  `operate_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `reason` VARCHAR(1024) DEFAULT NULL COMMENT '操作原因',
  `before_status` TINYINT DEFAULT NULL COMMENT '操作前跑道状态',
  `after_status` TINYINT DEFAULT NULL COMMENT '操作后跑道状态',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dr` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clearance_no` (`clearance_no`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_runway_id` (`runway_id`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='放行记录表';

-- 事件操作日志表
CREATE TABLE IF NOT EXISTS `fod_event_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id` BIGINT NOT NULL COMMENT '事件ID',
  `event_no` VARCHAR(32) NOT NULL COMMENT '事件编号',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型: report-上报 evaluate-评估 handle-处理 close-关闭 cancel-取消 update-修改',
  `operator_id` VARCHAR(64) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
  `operator_role` VARCHAR(32) DEFAULT NULL COMMENT '操作人角色',
  `operate_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `before_status` TINYINT DEFAULT NULL COMMENT '操作前状态',
  `after_status` TINYINT DEFAULT NULL COMMENT '操作后状态',
  `before_risk_level` TINYINT DEFAULT NULL COMMENT '操作前风险等级',
  `after_risk_level` TINYINT DEFAULT NULL COMMENT '操作后风险等级',
  `content` VARCHAR(1024) DEFAULT NULL COMMENT '操作内容',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件操作日志表';

-- 初始化跑道数据
INSERT INTO `runway` (`runway_code`, `runway_name`, `length`, `width`, `status`, `description`) VALUES
('RWY01', '01号跑道', 3600, 45, 1, '主跑道，西向东起降'),
('RWY19', '19号跑道', 3600, 45, 1, '主跑道，东向西起降'),
('RWY07', '07号跑道', 2800, 45, 1, '备用跑道，西南向东北起降'),
('RWY25', '25号跑道', 2800, 45, 1, '备用跑道，东北向西南起降');
