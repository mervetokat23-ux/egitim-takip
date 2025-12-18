import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// Axios instance oluştur
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - token ekle
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - 401 ve 403 durumunda logout
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const currentPath = window.location.pathname;
    const requestUrl = error.config?.url || '';
    
    // Auth endpoint'leri için interceptor müdahale etmesin
    const isAuthEndpoint = requestUrl.includes('/auth/');
    
    if (error.response?.status === 401) {
      // Unauthorized - token geçersiz veya yok
      if (!isAuthEndpoint && currentPath !== '/login') {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    } else if (error.response?.status === 403) {
      // Forbidden - yetki yok
      // Auth endpoint'leri ve login sayfası için müdahale etme
      if (!isAuthEndpoint && currentPath !== '/login') {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        alert('Yetkiniz yok veya oturum süreniz dolmuş. Lütfen tekrar giriş yapın.');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
};

// Egitim API
export const egitimAPI = {
  getAll: (params) => api.get('/egitim', { params }),
  getById: (id) => api.get(`/egitim/${id}`),
  create: (data) => api.post('/egitim', data),
  update: (id, data) => api.put(`/egitim/${id}`, data),
  delete: (id) => api.delete(`/egitim/${id}`),
};

// Convenience method for Egitim
export const getEgitimler = (params) => egitimAPI.getAll(params).then(res => res.data);

// Kategori API
export const kategoriAPI = {
  getAll: (params) => api.get('/kategori', { params }),
  getPage: (params) => api.get('/kategori/page', { params }),
  getById: (id) => api.get(`/kategori/${id}`),
  create: (data) => api.post('/kategori', data),
  update: (id, data) => api.put(`/kategori/${id}`, data),
  delete: (id) => api.delete(`/kategori/${id}`),
};

// Egitmen API
export const egitmenAPI = {
  getAll: (params) => api.get('/egitmen', { params }), // params ile pageable desteklenebilir
  getPage: (params) => api.get('/egitmen/page', { params }),
  getById: (id) => api.get(`/egitmen/${id}`),
  create: (data) => api.post('/egitmen', data),
  update: (id, data) => api.put(`/egitmen/${id}`, data),
  delete: (id) => api.delete(`/egitmen/${id}`),
};

// Sorumlu API
export const sorumluAPI = {
  getAll: (params) => api.get('/sorumlu', { params }),
  getPage: (params) => api.get('/sorumlu/page', { params }),
  getById: (id) => api.get(`/sorumlu/${id}`),
  create: (data) => api.post('/sorumlu', data),
  update: (id, data) => api.put(`/sorumlu/${id}`, data),
  delete: (id) => api.delete(`/sorumlu/${id}`),
};

// Convenience method for Sorumlu
export const getSorumlular = (params) => sorumluAPI.getAll(params).then(res => res.data);

// Paydas API
export const paydasAPI = {
  getAll: (params) => api.get('/paydas', { params }),
  getPage: (params) => api.get('/paydas/page', { params }),
  getById: (id) => api.get(`/paydas/${id}`),
  create: (data) => api.post('/paydas', data),
  update: (id, data) => api.put(`/paydas/${id}`, data),
  delete: (id) => api.delete(`/paydas/${id}`),
};

// Proje API
export const projeAPI = {
  getAll: (params) => api.get('/proje', { params }),
  getPage: (params) => api.get('/proje/page', { params }),
  getById: (id) => api.get(`/proje/${id}`),
  create: (data) => api.post('/proje', data),
  update: (id, data) => api.put(`/proje/${id}`, data),
  delete: (id) => api.delete(`/proje/${id}`),
};

// Faaliyet API
export const faaliyetAPI = {
  getAll: (params) => api.get('/faaliyet', { params }),
  getById: (id) => api.get(`/faaliyet/${id}`),
  create: (data) => api.post('/faaliyet', data),
  update: (id, data) => api.put(`/faaliyet/${id}`, data),
  delete: (id) => api.delete(`/faaliyet/${id}`),
};

// Odeme API
export const odemeAPI = {
  getAll: (params) => api.get('/odeme', { params }),
  getById: (id) => api.get(`/odeme/${id}`),
  create: (data) => api.post('/odeme', data),
  update: (id, data) => api.put(`/odeme/${id}`, data),
  delete: (id) => api.delete(`/odeme/${id}`),
  calculateTotal: (unitPrice, quantity) => api.post('/odeme/calculate-total', null, {
    params: { unitPrice, quantity }
  }),
};

// Convenience methods for Odeme
export const getOdemeler = (params) => odemeAPI.getAll(params).then(res => res.data);
export const getOdemeById = (id) => odemeAPI.getById(id).then(res => res.data);
export const createOdeme = (data) => odemeAPI.create(data).then(res => res.data);
export const updateOdeme = (id, data) => odemeAPI.update(id, data).then(res => res.data);
export const deleteOdeme = (id) => odemeAPI.delete(id).then(res => res.data);
export const calculateTotalPrice = (unitPrice, quantity) => odemeAPI.calculateTotal(unitPrice, quantity).then(res => res.data);

export default api;

