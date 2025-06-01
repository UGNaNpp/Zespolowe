"use client";

import NavBar from '@/app/components/navbar/NavBar';
import Dashboard from '@/app/components/dashboard/Dashboard';
import styles from './dashboardPageStyle.module.scss';
import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios/axios";

export default function DashboardPage() {
    return (
        <main className={styles.main}>
            <NavBar title='Dashboard' titleUrl='/dashboard' subtitle='' subtitleUrl='' />
            <Dashboard />
        </main>
    );
}
