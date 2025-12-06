
export const commonUtil = {
    inArray: (arr, item) => {
        for (let k in arr) {
            if (arr[k] && arr[k] === item) {
                return true;
            }
        }
        return false;
    },
    toDataString: (data) =>{
        if (data instanceof String) {
            return data;
        }
        try {
            return JSON.stringify(data);
        } catch (e) {
            return data.toString();
        }
    },
    parseJson(value) {
        try {
            return JSON.parse(value);
        } catch (_) {
            return value;
        }
    }
}
