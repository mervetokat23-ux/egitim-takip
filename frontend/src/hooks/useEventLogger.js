import { useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom';
import api from '../services/api';

/**
 * useEventLogger Hook
 * 
 * Frontend'de kullanıcı aksiyonlarını backend'e loglamak için custom hook.
 * Button click, page view ve form submit eventlerini otomatik olarak kaydeder.
 * 
 * Kullanım:
 * 
 * 1. Page View (otomatik):
 *    const { logPageView } = useEventLogger();
 * 
 * 2. Button Click:
 *    const { logButtonClick } = useEventLogger();
 *    <button onClick={() => logButtonClick('Save Button')}>Kaydet</button>
 * 
 * 3. Form Submit:
 *    const { logFormSubmit } = useEventLogger();
 *    <form onSubmit={(e) => { e.preventDefault(); logFormSubmit('Login Form'); }}>
 */
const useEventLogger = () => {
  const location = useLocation();

  // Kullanıcı bilgisini localStorage'dan al
  const getUserId = () => {
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      return user.id || null;
    } catch {
      return null;
    }
  };

  // Backend'e log gönder
  const sendLog = useCallback(async (action, details = '') => {
    try {
      const userId = getUserId();
      const payload = {
        userId,
        action,
        page: location.pathname,
        details
      };

      // API çağrısını sessizce yap (hata olsa bile kullanıcı deneyimini etkilemesin)
      await api.post('/api/logs/frontend', payload);
    } catch (error) {
      // Log hatası kullanıcı deneyimini etkilememeli
      console.debug('Event logging failed:', error);
    }
  }, [location.pathname]);

  // 1. Page View Logger (otomatik)
  const logPageView = useCallback(() => {
    sendLog('PAGE_VIEW', `Viewed ${location.pathname}`);
  }, [location.pathname, sendLog]);

  // Sayfa her değiştiğinde otomatik logla
  useEffect(() => {
    logPageView();
  }, [logPageView]);

  // 2. Button Click Logger
  const logButtonClick = useCallback((buttonName, additionalDetails = '') => {
    const details = additionalDetails 
      ? `${buttonName} - ${additionalDetails}` 
      : buttonName;
    sendLog('BUTTON_CLICK', details);
  }, [sendLog]);

  // 3. Form Submit Logger
  const logFormSubmit = useCallback((formName, additionalDetails = '') => {
    const details = additionalDetails 
      ? `${formName} - ${additionalDetails}` 
      : formName;
    sendLog('FORM_SUBMIT', details);
  }, [sendLog]);

  // 4. Custom Event Logger (genel amaçlı)
  const logCustomEvent = useCallback((action, details = '') => {
    sendLog(action, details);
  }, [sendLog]);

  return {
    logPageView,
    logButtonClick,
    logFormSubmit,
    logCustomEvent
  };
};

export default useEventLogger;


