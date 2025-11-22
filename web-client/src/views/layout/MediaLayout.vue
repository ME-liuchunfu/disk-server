<template>
    <div class="media-layout" :class="{'hide': hide}">
        <span class="open-close" @click="handleShowHide" v-show="mediaData.data.url"><el-icon><CloseBold /></el-icon></span>
        <!-- 音频 -->
        <div class="player-container" v-if="mediaData.type === 'audio'">
            <div class="player-container-icon" v-show="hide && mediaData.data.url">
                <div class="audio-cover rotate" @click="handleShowHide">
                    <img :src="mediaData.data.cover" :alt="mediaData.data.title" class="cover-img">
                </div>
            </div>
            <AudioPlayer
                :audio-src="mediaData.data.url"
                :audio-title="mediaData.data.title"
                :cover-src="mediaData.data.cover"
                primaryColor="#2c3e50"
            />
        </div>
        <div class="player-wrapper" v-if="mediaData.type === 'video'">
            <VideoPlayer
                :video-src="mediaData.data.url"
                :video-title="mediaData.data.title"
                :poster="mediaData.data.cover"
                primaryColor="#42b983"
                autoplay
                @play="handlePlay"
                @error="handleError"
                @back="handleBack"
                @close="handleClose"
            />
        </div>
    </div>
</template>

<script setup>
import eventBus from '@/utils/eventBus'
import {onMounted, onUnmounted, ref} from "vue";
import AudioPlayer from "@/components/media/AudioPlayer.vue";
import VideoPlayer from "@/components/media/VideoPlayer.vue";
import {ElMessage} from "element-plus";

const hide = ref(false)
const mediaData = ref({
    type: null,
    data: {
        cover: null,
        url: null,
        title: null,
    }
})

// 单视频播放回调
const handlePlay = () => {
  ElMessage.info('视频开始播放')
}

const handleError = (err) => {
  ElMessage.error('视频播放错误:' + err)
}

// 处理返回按钮逻辑（示例：路由返回）
const handleBack = () => {
  handleShowHide()
}

// 处理关闭按钮逻辑（示例：关闭组件/页面）
const handleClose = () => {
  handleShowHide()
}

const handleShowHide = () =>{
    hide.value = !hide.value;
}

const handleReceivedData = (data) => {
    console.log('media-data', data)
    if (data && data['type']) {
        const type = data['type']
        const value = data['value']
        if (type === 'audio') {
            mediaData.value.type = type;
            const mergedObj = Object.assign({}, mediaData.value.data, value);
            mediaData.value.data = mergedObj;
            hide.value = false;
        } else if (type === 'video') {
            mediaData.value.type = type;
            const mergedObj = Object.assign({}, mediaData.value.data, value);
            mediaData.value.data = mergedObj;
            hide.value = false;
        }
    }
}

// 组件挂载时监听事件
onMounted(() => {
    eventBus.on('media-event', handleReceivedData)
})

// 组件卸载时移除监听，避免内存泄漏
onUnmounted(() => {
    eventBus.off('media-event', handleReceivedData)
})

</script>

<style scoped>
    .media-layout {
        z-index: 9999;
        position: fixed;
        height: auto;
        width: 100%;
        bottom: 0;
        left: 0;
        transition: 1s;
    }
    .open-close {
        right: 0;
        position: absolute;
        top: 30px;
        transform: translate(-50%, -50%);
        cursor: pointer;
        height: 20px;
        background-color: #ffffff;
    }
    .hide {
        height: 0 !important;
    }
    .player-container {
        transition: 1s;
    }
    .player-container-icon {
        position: fixed;
        left: 0;
        bottom: 0;
    }
    .audio-cover {
        padding: 5px;
        width: 50px;
        height: 50px;
        display: flex;
        border-radius: 50%;
        overflow: hidden;
    }
    .cover-img {
        flex: 1;
        display: block;
        border-radius: 50%;
        margin: 0;
        width: 50px;
        height: 50px;
        object-fit: cover;
        transition: transform 0.3s ease;
    }

    .rotate {
        animation: rotate 8s linear infinite;
    }

    @keyframes rotate {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
</style>
