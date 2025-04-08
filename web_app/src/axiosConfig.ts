import axios from "axios";
import { getSession } from "next-auth/react";

const axiosConfig = axios.create({
    baseURL: process.env.BACKEND_URL,
    // baseURL: "http://localhost:8080",
});

axiosConfig.interceptors.request.use((config) => {async (config) => {
    const session = await getSession();
    const token = typeof window !== "undefined" ? session?.accessToken : null;
    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }

}
    return config;
});

export default axiosConfig;
