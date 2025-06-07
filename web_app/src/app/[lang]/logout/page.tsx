"use client";

import { signOut } from 'next-auth/react';
import { useEffect } from "react";
import api from "@/app/api/axios/axios";

export default function LogoutPage() {
  useEffect(() => {
    const logout = async () => {
      try {
        await api.get('api/security/delete-token');
        await signOut({ callbackUrl: '/' });
      } catch (logoutError) {
        console.error('Logout request failed:', logoutError);
      }
    };

    logout();
  }, []);

  return null;
}
