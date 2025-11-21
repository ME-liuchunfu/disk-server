<template>
    <div class="audio-player" :class="{ 'is-playing': isPlaying, 'is-loading': isLoading }">
        <!-- 音频封面 -->
        <div class="audio-cover" v-if="coverSrc">
            <img :src="coverSrc" :alt="audioTitle" class="cover-img" :class="{ 'rotate': isPlaying }">
        </div>

        <div class="audio-detail">
            <!-- 音频信息 -->
            <div class="audio-info">
                <h4 class="audio-title">{{ audioTitle || '未知音频' }}</h4>
                <p class="audio-status" v-if="isError">
                    <i class="icon-error">⚠️</i> 音频加载失败
                </p>
                <p class="audio-status" v-else-if="isLoading">加载中...</p>
                <p class="audio-duration" v-else>
                    {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
                </p>
            </div>
            <!-- 进度条 -->
            <div
                class="progress-container"
                @click="handleProgressClick"
                :disabled="isLoading || isError"
            >
                <div class="progress-bg">
                    <!-- 已播放进度 -->
                    <div
                        class="progress-played"
                        :style="{ width: `${progressPercent}%`, background: primaryColor }"
                    ></div>
                    <!-- 缓存进度（可选） -->
                    <div
                        class="progress-buffered"
                        :style="{ width: `${bufferedPercent}%` }"
                    ></div>
                    <!-- 进度滑块 -->
                    <div
                        class="progress-handle"
                        :style="{ left: `${progressPercent}%`, background: primaryColor }"
                    ></div>
                </div>
            </div>
        </div>

        <!-- 控制区 -->
        <div class="controls">
            <!-- 上一曲/下一曲 -->
            <button
                    class="control-btn prev-next-btn"
                    @click="emit('prev')"
                    :disabled="!hasPrevNext || isLoading || isError"
                    v-if="hasPrevNext"
            >
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M19 20L9 12L19 4V20Z"></path>
                    <path d="M5 19H9V5H5V19Z"></path>
                </svg>
            </button>

            <!-- 播放/暂停 -->
            <button
                    class="control-btn play-btn"
                    @click="togglePlay"
                    :disabled="isLoading || isError"
                    :aria-label="isPlaying ? '暂停' : '播放'"
            >
                <svg
                        v-if="!isPlaying && !isLoading"
                        xmlns="http://www.w3.org/2000/svg"
                        width="28"
                        height="28"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="white"
                        stroke-width="2"
                >
                    <polygon points="5 3 19 12 5 21 5 3"></polygon>
                </svg>
                <svg
                        v-else-if="isPlaying"
                        xmlns="http://www.w3.org/2000/svg"
                        width="28"
                        height="28"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="white"
                        stroke-width="2"
                >
                    <rect x="6" y="4" width="4" height="16"></rect>
                    <rect x="14" y="4" width="4" height="16"></rect>
                </svg>
                <!-- 加载中动画 -->
                <div class="loading-spinner" v-else></div>
            </button>

            <!-- 上一曲/下一曲 -->
            <button
                    class="control-btn prev-next-btn"
                    @click="emit('next')"
                    :disabled="!hasPrevNext || isLoading || isError"
                    v-if="hasPrevNext"
            >
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M5 4L15 12L5 20V4Z"></path>
                    <path d="M19 5H15V19H19V5Z"></path>
                </svg>
            </button>

            <!-- 音量调节 -->
            <div class="volume-control">
                <button
                        class="control-btn volume-btn"
                        @click="toggleMute"
                        :disabled="isLoading || isError"
                >
                    <svg
                            v-if="!isMuted && volume > 0.7"
                            xmlns="http://www.w3.org/2000/svg"
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="2"
                    >
                        <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                        <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
                        <path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
                    </svg>
                    <svg
                            v-else-if="!isMuted && volume > 0"
                            xmlns="http://www.w3.org/2000/svg"
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="2"
                    >
                        <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                        <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
                    </svg>
                    <svg
                            v-else
                            xmlns="http://www.w3.org/2000/svg"
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="2"
                    >
                        <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                        <line x1="15" y1="12" x2="21" y2="12"></line>
                        <line x1="18" y1="9" x2="21" y2="12"></line>
                        <line x1="18" y1="15" x2="21" y2="12"></line>
                    </svg>
                </button>
                <input
                        type="range"
                        class="volume-slider"
                        min="0"
                        max="1"
                        step="0.01"
                        v-model="volume"
                        @input="handleVolumeChange"
                        :disabled="isLoading || isError"
                >
            </div>

            <!-- 倍速播放 -->
            <div class="speed-control">
                <select
                        class="speed-select"
                        v-model="playbackRate"
                        @change="handleSpeedChange"
                        :disabled="isLoading || isError"
                >
                    <option value="0.5">0.5x</option>
                    <option value="0.75">0.75x</option>
                    <option value="1">1x</option>
                    <option value="1.25">1.25x</option>
                    <option value="1.5">1.5x</option>
                    <option value="2">2x</option>
                </select>
            </div>

            <!-- 循环模式 -->
            <button
                    class="control-btn loop-btn"
                    @click="toggleLoop"
                    :class="{ active: loopMode !== 'none' }"
                    :disabled="isLoading || isError"
            >
                <svg
                        v-if="loopMode === 'none'"
                        xmlns="http://www.w3.org/2000/svg"
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                >
                    <path d="M2 3v6h6"></path>
                    <path d="M22 21v-6h-6"></path>
                    <path d="M14.5 2a9 9 0 0 1 0 18M9.5 22a9 9 0 0 1 0-18"></path>
                </svg>
                <svg
                        v-else-if="loopMode === 'single'"
                        xmlns="http://www.w3.org/2000/svg"
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                >
                    <path d="M2 3v6h6"></path>
                    <path d="M22 21v-6h-6"></path>
                    <path d="M14.5 2a9 9 0 0 1 0 18M9.5 22a9 9 0 0 1 0-18"></path>
                    <circle cx="8" cy="12" r="1"></circle>
                </svg>
                <svg
                        v-else
                        xmlns="http://www.w3.org/2000/svg"
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                >
                    <path d="M2 3v6h6"></path>
                    <path d="M22 21v-6h-6"></path>
                    <path d="M14.5 2a9 9 0 0 1 0 18M9.5 22a9 9 0 0 1 0-18"></path>
                    <path d="M6 18L18 6"></path>
                </svg>
            </button>


        </div>
    </div>
    <!-- 原生音频元素（隐藏） -->
    <audio
        ref="audioRef"
        :src="audioSrc"
        :preload="preload"
        @loadedmetadata="handleLoadedMetadata"
        @timeupdate="handleTimeUpdate"
        @progress="handleProgress"
        @ended="handleEnded"
        @error="handleError"
        @loadeddata="handleLoadedData"
        @waiting="handleWaiting"
        @canplay="handleCanPlay"
    >
        您的浏览器不支持音频播放
    </audio>
</template>

<script setup>
import { ref, watch, onUnmounted, defineProps, defineEmits } from 'vue'

// Props 定义
const props = defineProps({
    audioSrc: {
        type: String,
        required: true,
        validator: (val) => {
            const validExts = ['mp3', 'wav', 'ogg', 'aac', 'm4a', 'flac'];
            const ext = val.split('.').pop().toLowerCase();
            return validExts.includes(ext);
        }
    },
    audioTitle: {
        type: String,
        default: '未知音频'
    },
    coverSrc: {
        type: String,
        default: '' // 音频封面图URL（可选）
    },
    preload: {
        type: String,
        default: 'metadata',
        validator: (val) => ['none', 'metadata', 'auto'].includes(val)
    },
    hasPrevNext: {
        type: Boolean,
        default: false // 是否显示上下曲按钮
    },
    primaryColor: {
        type: String,
        default: '#42b983' // 主题色（默认Vue绿色）
    }
})

// 事件发射
const emit = defineEmits([
    'play', 'pause', 'ended', 'error',
    'prev', 'next', 'progress-change', 'loaded'
])

// 核心状态
const audioRef = ref(null)
const isPlaying = ref(false)
const isMuted = ref(false)
const isLoading = ref(false)
const isError = ref(false)
const volume = ref(0.8)
const currentTime = ref(0)
const duration = ref(0)
const progressPercent = ref(0)
const bufferedPercent = ref(0)
const playbackRate = ref(1)
const loopMode = ref('none') // none/single/list

// 格式化时间（秒 → mm:ss）
const formatTime = (time) => {
    const minutes = Math.floor(time / 60)
    const seconds = Math.floor(time % 60)
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 播放/暂停切换
const togglePlay = () => {
    const audio = audioRef.value
    if (!audio) return

    if (isPlaying.value) {
        audio.pause()
        emit('pause')
    } else {
        audio.play().catch(err => {
            console.error('播放失败:', err)
            emit('error', err)
        })
        emit('play')
    }
    isPlaying.value = !isPlaying.value
}

// 静音切换
const toggleMute = () => {
    const audio = audioRef.value
    if (!audio) return

    isMuted.value = !isMuted.value
    audio.muted = isMuted.value
}

// 循环模式切换（none → single → list → none）
const toggleLoop = () => {
    const audio = audioRef.value
    if (!audio) return

    switch (loopMode.value) {
        case 'none':
            loopMode.value = 'single'
            audio.loop = true
            break
        case 'single':
            loopMode.value = 'list'
            audio.loop = false
            break
        case 'list':
            loopMode.value = 'none'
            audio.loop = false
            break
    }
}

// 进度条点击跳转
const handleProgressClick = (e) => {
    const audio = audioRef.value
    if (!audio || duration.value === 0 || isLoading.value) return

    const containerWidth = e.currentTarget.offsetWidth
    const clickX = e.offsetX
    const newPercent = (clickX / containerWidth) * 100

    currentTime.value = (newPercent / 100) * duration.value
    progressPercent.value = newPercent
    audio.currentTime = currentTime.value

    emit('progress-change', currentTime.value)
}

// 音量调节
const handleVolumeChange = () => {
    const audio = audioRef.value
    if (!audio) return

    audio.volume = volume.value
    if (volume.value > 0 && isMuted.value) {
        isMuted.value = false
        audio.muted = false
    }
}

// 倍速调节
const handleSpeedChange = () => {
    const audio = audioRef.value
    if (!audio) return
    audio.playbackRate = playbackRate.value
}

// 音频元数据加载完成（获取总时长）
const handleLoadedMetadata = () => {
    const audio = audioRef.value
    if (audio) {
        duration.value = audio.duration || 0
        audio.volume = volume.value
        emit('loaded')
    }
}

// 播放进度更新
const handleTimeUpdate = () => {
    const audio = audioRef.value
    if (!audio) return

    currentTime.value = audio.currentTime
    progressPercent.value = duration.value ? (currentTime.value / duration.value) * 100 : 0
}

// 缓存进度更新
const handleProgress = () => {
    const audio = audioRef.value
    if (!audio || !audio.buffered.length) return

    const bufferedEnd = audio.buffered.end(audio.buffered.length - 1)
    bufferedPercent.value = (bufferedEnd / duration.value) * 100
}

// 播放结束
const handleEnded = () => {
    isPlaying.value = false
    emit('ended')

    // 列表循环逻辑（需父组件配合）
    if (loopMode.value === 'list') {
        emit('next')
    }
}

// 加载错误
const handleError = (e) => {
    isLoading.value = false
    isError.value = true
    console.error('音频加载错误:', e)
    emit('error', e)
}

// 加载数据完成
const handleLoadedData = () => {
    isLoading.value = false
}

// 等待加载（缓冲中）
const handleWaiting = () => {
    isLoading.value = true
}

// 可播放
const handleCanPlay = () => {
    isLoading.value = false
}

// 监听音频地址变化，重置状态
watch(
    () => props.audioSrc,
    () => {
        isPlaying.value = false
        isError.value = false
        currentTime.value = 0
        progressPercent.value = 0
        bufferedPercent.value = 0
        duration.value = 0

        const audio = audioRef.value
        if (audio) {
            isLoading.value = true
            audio.load() // 重新加载音频
        }
    },
    { immediate: true }
)

// 组件卸载时清理
onUnmounted(() => {
    const audio = audioRef.value
    if (audio) {
        audio.pause()
        // 移除事件监听
        audio.removeEventListener('loadedmetadata', handleLoadedMetadata)
        audio.removeEventListener('timeupdate', handleTimeUpdate)
        audio.removeEventListener('progress', handleProgress)
        audio.removeEventListener('ended', handleEnded)
        audio.removeEventListener('error', handleError)
    }
})
</script>

<style scoped>
.audio-player {
    display: flex;
    flex-direction: row;
    justify-items: auto;
    align-items: center;
    gap: 20px;
    width: 100%;
    padding: 10px;
    box-sizing: border-box;
    border-radius: 16px 10px 0 0;
    background: #ffffff;
}

/* 封面样式 */
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
.audio-detail {
    display: flex;
    flex-direction: column;
    vert-align: middle;
}
/* 音频信息 */
.audio-info {
    gap: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.audio-title {
    font-size: 14px;
    color: #1d2129;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.audio-status {
    font-size: 13px;
    color: #ff4d4f;
    margin: 0;
}

.audio-duration {
    font-size: 12px;
    color: #86909c;
    margin: 0;
}

/* 进度条 */
.progress-container {
    width: 100%;
    height: 5px;
    border-radius: 4px;
    background: #f2f3f5;
    position: relative;
    cursor: pointer;
    margin-top: 5px;
}

.progress-bg {
    width: 100%;
    height: 100%;
    border-radius: 4px;
    position: relative;
}

.progress-buffered {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background: #e5e6eb;
    border-radius: 4px;
    z-index: 1;
}

.progress-played {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    border-radius: 4px;
    z-index: 2;
    transition: width 0.2s ease;
}

.progress-handle {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 16px;
    height: 16px;
    border-radius: 50%;
    box-shadow: 0 0 0 2px white, 0 2px 6px rgba(0, 0, 0, 0.15);
    z-index: 3;
    transition: left 0.2s ease, transform 0.2s ease;
}

.progress-handle:hover {
    transform: translateY(-50%) scale(1.2);
}

/* 控制区 */
.controls {
    display: flex;
    align-items: center;
    gap: 12px;
}

.control-btn {
    background: transparent;
    border: none;
    cursor: pointer;
    color: #4e5969;
    padding: 5px;
    border-radius: 50%;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
}

.control-btn:disabled {
    color: #c9cdD4;
    cursor: not-allowed;
}

.control-btn:hover:not(:disabled) {
    color: v-bind(primaryColor);
    background: rgba(66, 185, 131, 0.1);
}

/* 播放按钮 */
.play-btn {
    width: 30px;
    height: 30px;
    background: v-bind(primaryColor);
    color: white;
    box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.play-btn:hover:not(:disabled) {
    background: #359469;
    transform: scale(1.05);
    color: white;
    box-shadow: 0 6px 16px rgba(66, 185, 131, 0.4);
}

/* 加载动画 */
.loading-spinner {
    width: 20px;
    height: 20px;
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: white;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

/* 音量控制 */
.volume-control {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    max-width: 120px;
}

.volume-slider {
    width: 100%;
    height: 4px;
    -webkit-appearance: none;
    background: #f2f3f5;
    border-radius: 2px;
    outline: none;
}

.volume-slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: v-bind(primaryColor);
    cursor: pointer;
    transition: transform 0.2s ease;
}

.volume-slider::-webkit-slider-thumb:hover {
    transform: scale(1.2);
}

/* 倍速控制 */
.speed-control {
    margin-left: auto;
}

.speed-select {
    padding: 5px;
    border-radius: 10px;
    border: 1px solid #e5e6eb;
    background: white;
    font-size: 13px;
    color: #4e5969;
    cursor: pointer;
    transition: border-color 0.2s ease;
    outline: none;
}

.speed-select:focus {
    border-color: v-bind(primaryColor);
}

/* 循环按钮 */
.loop-btn.active {
    color: v-bind(primaryColor);
    background: rgba(66, 185, 131, 0.1);
}
</style>
