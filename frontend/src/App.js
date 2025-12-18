import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PermissionGuard from './components/PermissionGuard';

// Main Pages
import EgitimList from './components/EgitimList';
import EgitimForm from './components/EgitimForm';
import EgitimDetail from './components/EgitimDetail';
import EgitmenList from './components/EgitmenList';
import EgitmenForm from './components/EgitmenForm';
import SorumluList from './components/SorumluList';
import SorumluForm from './components/SorumluForm';
import KategoriList from './components/KategoriList';
import KategoriForm from './components/KategoriForm';
import PaydasList from './components/PaydasList';
import PaydasForm from './components/PaydasForm';
import PaydasDetail from './components/PaydasDetail';
import ProjeList from './components/ProjeList';
import ProjeForm from './components/ProjeForm';
import ProjeDetail from './components/ProjeDetail';
import FaaliyetList from './components/FaaliyetList';
import FaaliyetForm from './components/FaaliyetForm';
import PaymentList from './components/PaymentList';
import PaymentForm from './components/PaymentForm';
import PaymentDetail from './components/PaymentDetail';

// Auth & Special Pages
import Login from './components/Login';
import Unauthorized from './components/Unauthorized';

// Log Management
import LogDashboard from './components/logs/LogDashboard';
import ApiLogs from './components/logs/ApiLogs';
import ActivityLogs from './components/logs/ActivityLogs';
import ErrorLogs from './components/logs/ErrorLogs';
import PerformanceLogs from './components/logs/PerformanceLogs';
import FrontendLogs from './components/logs/FrontendLogs';

// Admin Panel
import AdminDashboard from './components/admin/AdminDashboard';
import RoleList from './components/admin/RoleList';
import PermissionList from './components/admin/PermissionList';
import RolePermissionAssignment from './components/admin/RolePermissionAssignment';
import UserRoleAssignment from './components/admin/UserRoleAssignment';
import RoleForm from './components/admin/RoleForm';

import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/unauthorized" element={<Unauthorized />} />
            
            {/* Eğitim Rotaları */}
            <Route 
              path="/egitim" 
              element={
                <PermissionGuard module="education" action="view">
                  <EgitimList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/egitim/new" 
              element={
                <PermissionGuard module="education" action="create">
                  <EgitimForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/egitim/edit/:id" 
              element={
                <PermissionGuard module="education" action="update">
                  <EgitimForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/egitim/:id" 
              element={
                <PermissionGuard module="education" action="view">
                  <EgitimDetail />
                </PermissionGuard>
              } 
            />

            {/* Eğitmen Rotaları */}
            <Route 
              path="/egitmen" 
              element={
                <PermissionGuard module="trainer" action="view">
                  <EgitmenList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/egitmen/new" 
              element={
                <PermissionGuard module="trainer" action="create">
                  <EgitmenForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/egitmen/edit/:id" 
              element={
                <PermissionGuard module="trainer" action="update">
                  <EgitmenForm />
                </PermissionGuard>
              } 
            />

            {/* Sorumlu Rotaları */}
            <Route 
              path="/sorumlu" 
              element={
                <PermissionGuard module="responsible" action="view">
                  <SorumluList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/sorumlu/new" 
              element={
                <PermissionGuard module="responsible" action="create">
                  <SorumluForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/sorumlu/edit/:id" 
              element={
                <PermissionGuard module="responsible" action="update">
                  <SorumluForm />
                </PermissionGuard>
              } 
            />

            {/* Kategori Rotaları */}
            <Route 
              path="/kategori" 
              element={
                <PermissionGuard module="category" action="view">
                  <KategoriList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/kategori/new" 
              element={
                <PermissionGuard module="category" action="create">
                  <KategoriForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/kategori/edit/:id" 
              element={
                <PermissionGuard module="category" action="update">
                  <KategoriForm />
                </PermissionGuard>
              } 
            />

            {/* Paydaş Rotaları */}
            <Route 
              path="/paydas" 
              element={
                <PermissionGuard module="stakeholder" action="view">
                  <PaydasList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/paydas/new" 
              element={
                <PermissionGuard module="stakeholder" action="create">
                  <PaydasForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/paydas/edit/:id" 
              element={
                <PermissionGuard module="stakeholder" action="update">
                  <PaydasForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/paydas/:id" 
              element={
                <PermissionGuard module="stakeholder" action="view">
                  <PaydasDetail />
                </PermissionGuard>
              } 
            />

            {/* Proje Rotaları */}
            <Route 
              path="/proje" 
              element={
                <PermissionGuard module="project" action="view">
                  <ProjeList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/proje/new" 
              element={
                <PermissionGuard module="project" action="create">
                  <ProjeForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/proje/edit/:id" 
              element={
                <PermissionGuard module="project" action="update">
                  <ProjeForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/proje/:id" 
              element={
                <PermissionGuard module="project" action="view">
                  <ProjeDetail />
                </PermissionGuard>
              } 
            />

            {/* Faaliyet Rotaları */}
            <Route 
              path="/faaliyet" 
              element={
                <PermissionGuard module="activity" action="view">
                  <FaaliyetList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/faaliyet/new" 
              element={
                <PermissionGuard module="activity" action="create">
                  <FaaliyetForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/faaliyet/edit/:id" 
              element={
                <PermissionGuard module="activity" action="update">
                  <FaaliyetForm />
                </PermissionGuard>
              } 
            />

            {/* Ödeme Rotaları */}
            <Route 
              path="/payments" 
              element={
                <PermissionGuard module="payment" action="view">
                  <PaymentList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/payments/create" 
              element={
                <PermissionGuard module="payment" action="create">
                  <PaymentForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/payments/:id/edit" 
              element={
                <PermissionGuard module="payment" action="update">
                  <PaymentForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/payments/:id/view" 
              element={
                <PermissionGuard module="payment" action="view">
                  <PaymentDetail />
                </PermissionGuard>
              } 
            />

            {/* Log Yönetimi Rotaları */}
            <Route 
              path="/logs" 
              element={
                <PermissionGuard module="logs" action="view">
                  <LogDashboard />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/logs/api" 
              element={
                <PermissionGuard module="logs" action="view">
                  <ApiLogs />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/logs/activity" 
              element={
                <PermissionGuard module="logs" action="view">
                  <ActivityLogs />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/logs/errors" 
              element={
                <PermissionGuard module="logs" action="view">
                  <ErrorLogs />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/logs/performance" 
              element={
                <PermissionGuard module="logs" action="view">
                  <PerformanceLogs />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/logs/frontend" 
              element={
                <PermissionGuard module="logs" action="view">
                  <FrontendLogs />
                </PermissionGuard>
              } 
            />

            {/* Admin Panel Rotaları */}
            <Route 
              path="/admin" 
              element={
                <PermissionGuard module="roles" action="view">
                  <AdminDashboard />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/roles" 
              element={
                <PermissionGuard module="roles" action="view">
                  <RoleList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/roles/create" 
              element={
                <PermissionGuard module="roles" action="create">
                  <RoleForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/roles/:id/edit" 
              element={
                <PermissionGuard module="roles" action="update">
                  <RoleForm />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/permissions" 
              element={
                <PermissionGuard module="permissions" action="view">
                  <PermissionList />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/role-permissions" 
              element={
                <PermissionGuard module="roles" action="manage">
                  <RolePermissionAssignment />
                </PermissionGuard>
              } 
            />
            <Route 
              path="/admin/user-roles" 
              element={
                <PermissionGuard module="responsible" action="update">
                  <UserRoleAssignment />
                </PermissionGuard>
              } 
            />

            {/* Default Route */}
            <Route path="/" element={<Navigate to="/egitim" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
