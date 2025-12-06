
import {commonUtil} from "@/utils/utils";

const CACHE_KEY = {
    LOGIN: "LOGIN",
    LOGIN_DATA: "LOGIN_DATA",
    USER_INFO: "USER_INFO",
}

const infos = {
    isLogin: ()=>{
        const isLogin = localStorage.getItem(CACHE_KEY.LOGIN) === 'true'
        return isLogin;
    },
    setLogin: (data)=>{
        const value = commonUtil.toDataString(data);
        localStorage.setItem(CACHE_KEY.LOGIN_DATA, value);
        localStorage.setItem(CACHE_KEY.LOGIN, 'true');
    },
    token: () =>{
        const token = localStorage.getItem(CACHE_KEY.LOGIN_DATA);
        if (token) {
            return commonUtil.parseJson(token);
        }
        return token;
    },
    logout: () =>{
        localStorage.clear()
    },
    setUserInfo(data) {
        if (data) {
            const value = commonUtil.toDataString(data);
            localStorage.setItem(CACHE_KEY.USER_INFO, value);
        }
    },
    getUserInfo() {
        const value = localStorage.getItem(CACHE_KEY.USER_INFO);
        if (value) {
            return commonUtil.parseJson(value);
        }
        return null
    }
}

export default infos
