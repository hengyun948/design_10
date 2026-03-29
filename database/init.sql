-- 智慧物业服务平台 数据库初始化脚本
-- 数据库版本: MySQL 8.0

CREATE DATABASE IF NOT EXISTS property_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password      VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    real_name     VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    phone         VARCHAR(20) COMMENT '手机号',
    email         VARCHAR(100) COMMENT '邮箱',
    role_code     VARCHAR(30)  NOT NULL COMMENT '角色编码',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    avatar        VARCHAR(255) COMMENT '头像路径',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '更新时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0正常 1删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(30)  NOT NULL UNIQUE COMMENT '角色编码',
    remark      VARCHAR(200) COMMENT '备注',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 业主信息表
CREATE TABLE IF NOT EXISTS owner_info (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL UNIQUE COMMENT '关联用户ID',
    gender      TINYINT COMMENT '性别 0女 1男',
    id_card     VARCHAR(20) COMMENT '身份证号',
    address     VARCHAR(255) COMMENT '联系地址',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主信息表';

-- 房屋信息表
CREATE TABLE IF NOT EXISTS house_info (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    building_no  VARCHAR(20)  NOT NULL COMMENT '楼栋号',
    unit_no      VARCHAR(20)  NOT NULL COMMENT '单元号',
    room_no      VARCHAR(20)  NOT NULL COMMENT '房间号',
    area         DECIMAL(8,2) COMMENT '建筑面积(㎡)',
    house_type   VARCHAR(20) COMMENT '户型 如:3室2厅',
    house_status TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0待售 1已售 2已租',
    remark       VARCHAR(500) COMMENT '备注',
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_house (building_no, unit_no, room_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋信息表';

-- 业主房屋绑定关系表
CREATE TABLE IF NOT EXISTS owner_house_rel (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id    BIGINT       NOT NULL COMMENT '业主ID(owner_info.id)',
    house_id    BIGINT       NOT NULL COMMENT '房屋ID',
    bind_type   TINYINT      NOT NULL DEFAULT 1 COMMENT '绑定类型 1业主 2租户',
    is_primary  TINYINT      NOT NULL DEFAULT 1 COMMENT '是否主房屋',
    start_date  DATE COMMENT '开始日期',
    end_date    DATE COMMENT '结束日期',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主房屋绑定关系表';

-- 费用账单表
CREATE TABLE IF NOT EXISTS bill_info (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_no         VARCHAR(30)   NOT NULL UNIQUE COMMENT '账单编号',
    house_id        BIGINT        NOT NULL COMMENT '房屋ID',
    owner_id        BIGINT        NOT NULL COMMENT '业主ID',
    bill_type       VARCHAR(30)   NOT NULL COMMENT '费用类型 PROPERTY物业费 WATER水费 ELECTRIC电费 GAS燃气费 PARKING停车费',
    billing_period  VARCHAR(20)   NOT NULL COMMENT '账期 如:2024-01',
    amount_due      DECIMAL(10,2) NOT NULL COMMENT '应缴金额',
    amount_paid     DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已缴金额',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态 0未缴 1已缴 2逾期',
    due_date        DATE COMMENT '缴费截止日期',
    remark          VARCHAR(500) COMMENT '备注',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '更新时间',
    deleted         TINYINT       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用账单表';

-- 缴费记录表
CREATE TABLE IF NOT EXISTS payment_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id     BIGINT        NOT NULL COMMENT '账单ID',
    pay_no      VARCHAR(30)   NOT NULL UNIQUE COMMENT '支付流水号',
    pay_amount  DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_type    VARCHAR(20)   NOT NULL DEFAULT 'MOCK' COMMENT '支付方式',
    pay_status  TINYINT       NOT NULL DEFAULT 1 COMMENT '支付状态 1成功',
    pay_time    DATETIME      NOT NULL COMMENT '支付时间',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- 报修工单表
CREATE TABLE IF NOT EXISTS repair_order (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no           VARCHAR(30)  NOT NULL UNIQUE COMMENT '工单编号',
    owner_id           BIGINT       NOT NULL COMMENT '报修业主ID',
    house_id           BIGINT       NOT NULL COMMENT '报修房屋ID',
    title              VARCHAR(100) NOT NULL COMMENT '报修标题',
    content            TEXT         NOT NULL COMMENT '问题描述',
    repair_type        VARCHAR(30) COMMENT '报修类型 WATER水管 ELECTRIC电路 DOOR门窗 OTHER其他',
    priority           TINYINT      NOT NULL DEFAULT 2 COMMENT '优先级 1紧急 2普通 3低',
    status             TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0待受理 1已受理 2已分派 3处理中 4已完成 5已取消',
    assigned_worker_id BIGINT COMMENT '指派维修人员ID',
    accept_time        DATETIME COMMENT '受理时间',
    assign_time        DATETIME COMMENT '派单时间',
    finish_time        DATETIME COMMENT '完成时间',
    image_urls         VARCHAR(1000) COMMENT '图片路径,逗号分隔',
    create_time        DATETIME COMMENT '创建时间',
    update_time        DATETIME COMMENT '更新时间',
    deleted            TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修工单表';

-- 工单处理记录表
CREATE TABLE IF NOT EXISTS repair_process_record (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    repair_order_id BIGINT      NOT NULL COMMENT '工单ID',
    operator_id    BIGINT       NOT NULL COMMENT '操作人ID',
    action_type    VARCHAR(30)  NOT NULL COMMENT '操作类型 SUBMIT提交 ACCEPT受理 ASSIGN派单 RECEIVE接单 UPDATE进度 FINISH完成 CANCEL取消',
    action_content TEXT COMMENT '操作内容',
    process_status TINYINT COMMENT '操作后工单状态',
    record_time    DATETIME     NOT NULL COMMENT '记录时间',
    create_time    DATETIME COMMENT '创建时间',
    deleted        TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单处理记录表';

-- 公告表
CREATE TABLE IF NOT EXISTS notice_info (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(200) NOT NULL COMMENT '公告标题',
    content        TEXT         NOT NULL COMMENT '公告内容',
    notice_type    VARCHAR(30) COMMENT '类型 NOTICE通知 ACTIVITY活动 URGENCY紧急',
    publish_status TINYINT      NOT NULL DEFAULT 0 COMMENT '发布状态 0草稿 1已发布',
    publisher_id   BIGINT COMMENT '发布人ID',
    publish_time   DATETIME COMMENT '发布时间',
    create_time    DATETIME COMMENT '创建时间',
    update_time    DATETIME COMMENT '更新时间',
    deleted        TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 停车位表
CREATE TABLE IF NOT EXISTS parking_space (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_no    VARCHAR(20)  NOT NULL UNIQUE COMMENT '车位编号',
    space_type  VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '类型 NORMAL普通 CHARGE充电桩',
    location    VARCHAR(100) COMMENT '位置描述',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0空闲 1已占用 2已锁定',
    owner_id    BIGINT COMMENT '绑定业主ID(月租)',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车位表';

-- 停车记录表
CREATE TABLE IF NOT EXISTS parking_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_id    BIGINT        NOT NULL COMMENT '车位ID',
    car_no      VARCHAR(20)   NOT NULL COMMENT '车牌号',
    owner_id    BIGINT COMMENT '业主ID(可为空)',
    start_time  DATETIME      NOT NULL COMMENT '进入时间',
    end_time    DATETIME COMMENT '离开时间',
    fee_amount  DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '费用',
    pay_status  TINYINT       NOT NULL DEFAULT 0 COMMENT '支付状态 0未缴 1已缴',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted     TINYINT       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车记录表';

-- 投诉建议表
CREATE TABLE IF NOT EXISTS complaint_suggestion (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id       BIGINT       NOT NULL COMMENT '业主ID',
    title          VARCHAR(200) NOT NULL COMMENT '标题',
    content        TEXT         NOT NULL COMMENT '内容',
    category       VARCHAR(20)  NOT NULL DEFAULT 'COMPLAINT' COMMENT '类型 COMPLAINT投诉 SUGGESTION建议',
    status         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0待处理 1处理中 2已处理',
    reply_content  TEXT COMMENT '回复内容',
    reply_by       BIGINT COMMENT '回复人ID',
    reply_time     DATETIME COMMENT '回复时间',
    create_time    DATETIME COMMENT '创建时间',
    update_time    DATETIME COMMENT '更新时间',
    deleted        TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议表';

-- 活动信息表
CREATE TABLE IF NOT EXISTS activity_info (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200) NOT NULL COMMENT '活动标题',
    content          TEXT COMMENT '活动内容',
    activity_type    VARCHAR(30) COMMENT '类型 ACTIVITY活动 CARE关怀',
    activity_time    DATETIME COMMENT '活动时间',
    location         VARCHAR(200) COMMENT '活动地点',
    max_participants INT COMMENT '最大参与人数 null不限',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1报名中 2已结束 0已取消',
    publisher_id     BIGINT COMMENT '发布人ID',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '更新时间',
    deleted          TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动信息表';

-- 活动报名表
CREATE TABLE IF NOT EXISTS activity_signup (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id  BIGINT      NOT NULL COMMENT '活动ID',
    owner_id     BIGINT      NOT NULL COMMENT '报名业主ID',
    signup_time  DATETIME    NOT NULL COMMENT '报名时间',
    status       TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1已报名 0已取消',
    remark       VARCHAR(500) COMMENT '备注',
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '更新时间',
    deleted      TINYINT     NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 基础设施表
CREATE TABLE IF NOT EXISTS facility_info (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    facility_name         VARCHAR(100) NOT NULL COMMENT '设施名称',
    facility_type         VARCHAR(50) COMMENT '设施类型 ELEVATOR电梯 FIRE消防 WATER水务 POWER供电 OTHER其他',
    location              VARCHAR(200) COMMENT '位置',
    status                TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0故障 2维护中',
    last_maintenance_time DATETIME COMMENT '最近维保时间',
    remark                VARCHAR(500) COMMENT '备注',
    create_time           DATETIME COMMENT '创建时间',
    update_time           DATETIME COMMENT '更新时间',
    deleted               TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础设施表';

-- 巡检记录表
CREATE TABLE IF NOT EXISTS inspection_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    facility_id     BIGINT      NOT NULL COMMENT '设施ID',
    inspector_id    BIGINT      NOT NULL COMMENT '巡检人员ID',
    inspection_time DATETIME    NOT NULL COMMENT '巡检时间',
    result          VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '巡检结果 NORMAL正常 ABNORMAL异常',
    status          TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1已完成',
    remark          VARCHAR(500) COMMENT '备注说明',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '更新时间',
    deleted         TINYINT     NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检记录表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_name    VARCHAR(50) COMMENT '模块名称',
    operation_type VARCHAR(30) COMMENT '操作类型',
    request_uri    VARCHAR(255) COMMENT '请求路径',
    request_method VARCHAR(10) COMMENT '请求方式',
    operator_id    BIGINT COMMENT '操作人ID',
    operator_name  VARCHAR(50) COMMENT '操作人名称',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    ip             VARCHAR(50) COMMENT '操作IP',
    execute_time   BIGINT COMMENT '执行时长(ms)',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1成功 0失败',
    error_msg      TEXT COMMENT '错误信息',
    create_time    DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
