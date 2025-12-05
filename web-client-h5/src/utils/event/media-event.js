import eventBus from "@/utils/eventBus";
import {ElMessage} from "element-plus";

const event = (type, value) => {
    eventBus.emit('media-event', {
        type: type,
        value: value
    })
}

const mediaEvent = {
    pdf: (value) => event('pdf', value),
    audio: (value) => event('audio', value),
    video: (value) => event('video', value),
    doc: (value) => event('doc', value),
    xls: (value) => event('xls', value),
    handler: (type, callback) => {
        const hand = handleMap[type];
        if (hand) {
            callback(hand)
        } else {
            ElMessage.info('不支持预览类型' + type)
        }
    }
}

const handleMap = {
    'pdf': mediaEvent.pdf,
    'mp3': mediaEvent.audio,
    'mp4': mediaEvent.video,
    'doc': mediaEvent.doc,
    'docx': mediaEvent.doc,
    'xls': mediaEvent.xls,
    'xlsx': mediaEvent.xls,
}

export const mediaEventHandle = mediaEvent