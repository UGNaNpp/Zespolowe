import axios from 'axios';
import { signOut } from 'next-auth/react';
// import Router from 'next/router';


// TODO - JAK 403 | 500 - wylogowanie (czekanie na endpoint)

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_BACKEND_URL,
  withCredentials: true
});

api.interceptors.response.use(
  response => response,
  async error => {
    const status = error.response?.status;

    if (status === 403 || status === 500) {
      try {
        // await axios.get(`${process.env.NEXT_PUBLIC_BACKEND_URL}api/security/delete-token`);

        // await signOut({ callbackUrl: '/' });
      } catch (logoutError) {
        console.error('Logout request failed:', logoutError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
