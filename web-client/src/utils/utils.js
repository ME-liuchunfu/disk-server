
export const commonUtil = {
    inArray: (arr, item) => {
        for (let k in arr) {
            if (arr[k] && arr[k] === item) {
                return true;
            }
        }
        return false;
    }
}