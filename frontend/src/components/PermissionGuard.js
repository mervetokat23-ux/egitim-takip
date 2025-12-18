import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * PermissionGuard Component
 * 
 * Wraps routes to check if user has required permissions.
 * Redirects to /unauthorized if permission is denied.
 * 
 * Usage:
 * <PermissionGuard module="education" action="view">
 *   <EgitimList />
 * </PermissionGuard>
 */
function PermissionGuard({ module, action, children, fallback = null }) {
  const { hasPermission, loading, isAuthenticated } = useAuth();

  // Show loading state while checking authentication
  if (loading) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Yükleniyor...</div>;
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check permission
  if (!hasPermission(module, action)) {
    // If fallback is provided, render it instead of redirecting
    if (fallback) {
      return fallback;
    }
    // Otherwise redirect to unauthorized page
    return <Navigate to="/unauthorized" replace />;
  }

  // User has permission, render children
  return <>{children}</>;
}

export default PermissionGuard;


