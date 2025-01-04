import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const request = axios.create({
    baseURL: 'http://localhost:10086',
    timeout: 5000
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        console.log('发送请求:', config.url);
        if (!config.url.includes('/login')) {
            const token = sessionStorage.getItem('jwt'); // 改为从 sessionStorage 获取 jwt
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
                console.log('使用token:', token);
            }
        }
        return config;
    },
    error => Promise.reject(error)
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        console.log('收到响应:', response.config.url, response.data);
        const res = response.data;

        if (res.code === 200) {
            return res;
        }

        switch (res.code) {
            case 401:
                Message.error('未登录或登录已过期');
                sessionStorage.clear();  // 改为清除 sessionStorage
                router.push('/login');
                break;
            case 403:
                Message.error('没有权限执行此操作');
                break;
            default:
                console.warn('操作返回非200状态:', res);
        }

        return Promise.reject(res);
    },
    error => {
        console.error('请求错误:', error);
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    Message.error('未登录或登录已过期');
                    sessionStorage.clear();  // 改为清除 sessionStorage
                    router.push('/login');
                    break;
                case 403:
                    Message.error('没有权限访问');
                    break;
                case 500:
                    Message.error('服务器错误，请稍后重试');
                    break;
                default:
                    Message.error('未知错误，请稍后重试');
            }
        } else if (error.code === 'ECONNABORTED') {
            Message.error('请求超时，请检查网络');
        } else {
            Message.error('网络错误，请稍后重试');
        }
        return Promise.reject(error);
    }
);

export default request;