
import {commonUtil} from "@/utils/utils";

const CACHE_KEY = {
    LOGIN: "LOGIN",
    LOGIN_DATA: "LOGIN_DATA",
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
        return token;
    }
}

export default infos
