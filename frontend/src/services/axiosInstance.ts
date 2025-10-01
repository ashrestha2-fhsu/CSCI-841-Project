


// src/services/axiosInstance.ts
import axios, { AxiosRequestConfig, AxiosError } from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: false, // set true only if you actually use cookies
});

axiosInstance.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = localStorage.getItem("token");
    if (token) {
      if (!config.headers) config.headers = {};
      (config.headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (res) => res,
  (error: AxiosError) => {
    // Optional: handle 401/403 here
    return Promise.reject(error);
  }
);

export default axiosInstance;
