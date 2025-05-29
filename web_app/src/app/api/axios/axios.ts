import axios from 'axios';
import { signOut } from 'next-auth/react';
// import Router from 'next/router';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_BACKEND_URL,
  withCredentials: true
});

api.interceptors.response.use(
  response => response,
  async error => {
    const status = error.response?.status;

    if (status === 403 || status === 500) {
      // await signOut({ callbackUrl: '/' });
    }

    return Promise.reject(error);
  }
);

export default api;
