import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import EventList from './pages/EventList';
import EventDetail from './pages/EventDetail';
import EventReport from './pages/EventReport';
import TowerEvaluate from './pages/TowerEvaluate';
import MaintenanceHandle from './pages/MaintenanceHandle';
import RunwayManage from './pages/RunwayManage';

const App: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/event/list" replace />} />
        <Route path="event/list" element={<EventList />} />
        <Route path="event/:id" element={<EventDetail />} />
        <Route path="event/report" element={<EventReport />} />
        <Route path="tower/evaluate" element={<TowerEvaluate />} />
        <Route path="maintenance/handle" element={<MaintenanceHandle />} />
        <Route path="runway/manage" element={<RunwayManage />} />
      </Route>
    </Routes>
  );
};

export default App;
