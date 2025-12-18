import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Load user from localStorage on mount
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    
    if (storedUser && token) {
      try {
        const parsedUser = JSON.parse(storedUser);
        setUser(parsedUser);
      } catch (error) {
        console.error('Failed to parse stored user:', error);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      const userData = response.data;
      
      // Store token and user data
      localStorage.setItem('token', userData.token);
      localStorage.setItem('user', JSON.stringify(userData));
      
      setUser(userData);
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  /**
   * Check if user has a specific permission
   * @param {string} module - Module name (e.g., 'education', 'trainer')
   * @param {string} action - Action name (e.g., 'view', 'create', 'update', 'delete')
   * @returns {boolean}
   */
  const hasPermission = (module, action) => {
    if (!user) return false;
    
    // Admin has all permissions
    if (user.rol === 'ADMIN') return true;
    
    // Check if user has permissions array
    if (user.permissions && Array.isArray(user.permissions)) {
      return user.permissions.includes(`${module}.${action}`);
    }
    
    // Fallback: Legacy role-based permissions
    // SORUMLU can do most things except admin-only operations
    if (user.rol === 'SORUMLU') {
      // Admin-only modules
      if (module === 'roles' || module === 'permissions') {
        return action === 'view';
      }
      // SORUMLU can view, create, update, delete on other modules
      return ['view', 'create', 'update', 'delete'].includes(action);
    }
    
    // EGITMEN can only view
    if (user.rol === 'EGITMEN') {
      return action === 'view';
    }
    
    return false;
  };

  /**
   * Check if user has any of the specified permissions
   * @param {string[]} permissions - Array of permission strings like ['education.view', 'education.create']
   * @returns {boolean}
   */
  const hasAnyPermission = (permissions) => {
    return permissions.some(permission => {
      const [module, action] = permission.split('.');
      return hasPermission(module, action);
    });
  };

  /**
   * Check if user has all of the specified permissions
   * @param {string[]} permissions - Array of permission strings
   * @returns {boolean}
   */
  const hasAllPermissions = (permissions) => {
    return permissions.every(permission => {
      const [module, action] = permission.split('.');
      return hasPermission(module, action);
    });
  };

  /**
   * Check if user is admin
   * @returns {boolean}
   */
  const isAdmin = () => {
    return user && user.rol === 'ADMIN';
  };

  const value = {
    user,
    loading,
    login,
    logout,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    isAdmin,
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;


