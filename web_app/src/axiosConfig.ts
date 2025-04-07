import axios from "axios";

const axiosConfig = axios.create({
    // baseURL: process.env.BACKEND_URL,
    baseURL: "http://localhost:8080",
});

axiosConfig.interceptors.request.use((config) => {
    const token = typeof window !== "undefined" ? "TESTOWY_TOKEN" : null;
        console.log("I was there")

    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default axiosConfig;
