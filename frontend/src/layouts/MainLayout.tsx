import React, { useState } from 'react';
import { Layout, Menu, Dropdown, Avatar, Space, Tag } from 'antd';
import {
  SafetyOutlined,
  FileAddOutlined,
  ExclamationCircleOutlined,
  ToolOutlined,
  RocketOutlined,
  UserOutlined,
  DownOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useUser } from '@/context/UserContext';
import { RoleEnum, RoleMap } from '@/constants';
import type { UserRole } from '@/types';

const { Header, Sider, Content } = Layout;

const MainLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { userRole, setUserRole, userName } = useUser();
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    {
      key: '/event/list',
      icon: <ExclamationCircleOutlined />,
      label: '事件列表',
    },
    {
      key: '/event/report',
      icon: <FileAddOutlined />,
      label: '上报异物',
    },
    {
      key: '/tower/evaluate',
      icon: <SafetyOutlined />,
      label: '塔台评估',
    },
    {
      key: '/maintenance/handle',
      icon: <ToolOutlined />,
      label: '维修处理',
    },
    {
      key: '/runway/manage',
      icon: <RocketOutlined />,
      label: '跑道管理',
    },
  ];

  const roleMenuItems = [
    {
      key: RoleEnum.FIELD_INSPECTOR,
      label: RoleMap[RoleEnum.FIELD_INSPECTOR],
    },
    {
      key: RoleEnum.TOWER_CONTROLLER,
      label: RoleMap[RoleEnum.TOWER_CONTROLLER],
    },
    {
      key: RoleEnum.MAINTENANCE_TEAM,
      label: RoleMap[RoleEnum.MAINTENANCE_TEAM],
    },
  ];

  const handleRoleChange = ({ key }: { key: string }) => {
    setUserRole(key as UserRole);
  };

  const roleMenu = {
    items: roleMenuItems,
    onClick: handleRoleChange,
    selectedKeys: [userRole],
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          background: '#fff',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 1px 4px rgba(0,21,41,0.08)',
        }}
      >
        <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#1890ff' }}>
          🛫 机场跑道异物巡查系统
        </div>
        <Space size="large">
          <Dropdown menu={roleMenu}>
            <Space style={{ cursor: 'pointer' }}>
              <Tag color="blue">{RoleMap[userRole]}</Tag>
              <DownOutlined style={{ fontSize: '12px' }} />
            </Space>
          </Dropdown>
          <Space>
            <Avatar icon={<UserOutlined />} />
            <span>{userName}</span>
          </Space>
        </Space>
      </Header>
      <Layout>
        <Sider
          collapsible
          collapsed={collapsed}
          onCollapse={setCollapsed}
          width={220}
          style={{ background: '#001529' }}
        >
          <Menu
            theme="dark"
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
            style={{ height: '100%', borderRight: 0 }}
          />
        </Sider>
        <Layout style={{ padding: '24px' }}>
          <Content
            style={{
              background: '#fff',
              padding: 24,
              borderRadius: 8,
              minHeight: 'calc(100vh - 184px)',
            }}
          >
            <Outlet />
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
