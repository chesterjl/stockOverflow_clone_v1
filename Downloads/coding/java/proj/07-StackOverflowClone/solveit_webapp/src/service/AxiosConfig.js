import axios from "axios";
import {BASE_URL} from "./ApiEndpoints.js";

const AxiosConfig = axios.create({
    baseURL: BASE_URL,
    headers: {
        Accept: "application/json",
    }
})

// list of endpoints that do not require authorization header
const excludeEndpoints = ["/login", "/register", "/status", "/activate"];

// request interceptors
AxiosConfig.interceptors.request.use((config) => {
    const  shouldSkipToken= excludeEndpoints.some((endpoint) => {
        return config.url?.includes(endpoint)
    });

    if (!shouldSkipToken) {
        const accessToken =  localStorage.getItem("token");
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

//response interceptors
AxiosConfig.interceptors.response.use((response) => {
    return response;
}, (error) => {
    if (error.response) {
        if (error.response.status === 401) {
            window.location.href = '/login';
        } else if (error.response.status === 500) {
            console.log("Server Error: Please try again later.");
        }
    } else if (error.code === "ECONNABORTED") {
        console.log("Request Timeout: Please try again later.");
    }
    return Promise.reject(error);
});

export default AxiosConfig;
