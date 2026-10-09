import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const getHealthStatus = async () => {
  const response = await api.get('/health');
  return response.data;
};

export default api;
