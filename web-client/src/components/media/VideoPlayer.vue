<template>
  <div class="full-format-video-player" :class="{ 'is-fullscreen': isFullscreen }">
    <!-- 新增：顶部控制栏（返回+关闭） -->
    <div class="top-controls">
      <button class="control-btn back-btn" @click="handleBack" aria-label="返回">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
          <path d="M19 12H5M5 12l7 7M5 12l7-7"></path>
        </svg>
      </button>
      <div class="title-wrap"><span>{{videoTitle}}</span></div>
      <button class="control-btn close-btn" @click="handleClose" aria-label="关闭">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
    </div>

    <!-- 视频容器（高度100vh，宽度自适应视频比例） -->
    <div class="video-container">
      <!-- 视频包裹层（实现水平居中+宽度自适应） -->
      <div class="video-wrapper" :style="{ width: videoWrapperWidth + 'px' }">
        <video
            ref="videoRef"
            class="video-js vjs-big-play-centered"
            :poster="poster"
            :playsinline="true"
            webkit-playsinline
        ></video>
      </div>
    </div>

    <!-- 自定义控制栏（宽度跟随视频包裹层） -->
    <div
        class="custom-controls"
        :class="{ visible: showControls }"
        :style="{ width: videoWrapperWidth + 'px' }"
        @mouseenter="showControls = true"
        @mouseleave="showControls = isPlaying"
    >
      <!-- 进度条 -->
      <div class="progress-container" @click="handleProgressClick">
        <div class="progress-bg">
          <div class="progress-buffered" :style="{ width: `${bufferedPercent}%` }"></div>
          <div class="progress-played" :style="{ width: `${progressPercent}%`, background: primaryColor }"></div>
          <div class="progress-handle" :style="{ left: `${progressPercent}%`, background: primaryColor }"></div>
        </div>
        <div class="time-display">
          <span>{{ formatTime(currentTime) }}</span>
          <span>/{{ formatTime(duration) }}</span>
        </div>
      </div>

      <!-- 控制按钮组 -->
      <div class="controls-group">
        <button class="control-btn" @click="togglePlay" :aria-label="isPlaying ? '暂停' : '播放'">
          <svg v-if="!isPlaying" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <polygon points="5 3 19 12 5 21 5 3"></polygon>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <rect x="6" y="4" width="4" height="16"></rect>
            <rect x="14" y="4" width="4" height="16"></rect>
          </svg>
        </button>

        <div class="volume-control">
          <button class="control-btn" @click="toggleMute">
            <svg v-if="!isMuted && volume > 0.7" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
              <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
              <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
              <path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
            </svg>
            <svg v-else-if="!isMuted && volume > 0" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
              <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
              <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
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
          >
        </div>

        <div class="speed-control">
          <select class="speed-select" v-model="playbackRate" @change="handleSpeedChange">
            <option value="0.5">0.5x</option>
            <option value="0.75">0.75x</option>
            <option value="1">1x</option>
            <option value="1.25">1.25x</option>
            <option value="1.5">1.5x</option>
            <option value="2">2x</option>
          </select>
        </div>

        <div class="quality-control" v-if="videoQualities.length > 1">
          <select class="quality-select" v-model="currentQuality" @change="handleQualityChange">
            <option v-for="quality in videoQualities" :key="quality.value" :value="quality.value">
              {{ quality.label }}
            </option>
          </select>
        </div>

        <button class="control-btn" @click="togglePictureInPicture" :disabled="!supportsPiP">
          <svg v-if="!isPiP" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
            <polyline points="7 10 12 15 17 10"></polyline>
            <line x1="12" y1="15" x2="12" y2="3"></line>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
            <polyline points="7 10 12 15 17 10"></polyline>
            <line x1="12" y1="3" x2="12" y2="15"></line>
            <line x1="21" y1="9" x2="3" y2="9"></line>
          </svg>
        </button>

        <button class="control-btn" @click="toggleFullscreen">
          <svg v-if="!isFullscreen" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <polyline points="15 3 21 3 21 9"></polyline>
            <polyline points="9 21 3 21 3 15"></polyline>
            <line x1="21" y1="3" x2="14" y2="10"></line>
            <line x1="3" y1="21" x2="10" y2="14"></line>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <polyline points="16 18 22 12 16 6"></polyline>
            <polyline points="8 6 2 12 8 18"></polyline>
            <line x1="22" y1="12" x2="10" y2="12"></line>
            <line x1="2" y1="12" x2="14" y2="12"></line>
          </svg>
        </button>
      </div>
    </div>

    <!-- 加载中动画 -->
    <div class="loading-overlay" v-if="isLoading">
      <div class="loading-spinner"></div>
    </div>

    <!-- 错误提示 -->
    <div class="error-overlay" v-if="isError">
      <div class="error-content">
        <i class="error-icon">⚠️</i>
        <p class="error-message">{{ errorMessage || '视频加载失败，请检查地址或格式是否支持' }}</p>
        <button class="retry-btn" @click="reloadVideo">重试</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, defineEmits, defineProps, nextTick } from 'vue'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

// Props 定义（新增：是否显示返回/关闭按钮，默认显示）
const props = defineProps({
  videoSrc: {type: String, default: ''},
  videoSources: {type: Array, default: () => []},
  videoTitle: {type: String, default: '未知视频'},
  poster: {type: String, default: ''},
  preload: {type: String, default: 'metadata', validator: (val) => ['none', 'metadata', 'auto'].includes(val)},
  primaryColor: {type: String, default: '#ff4d4f'},
  videoQualities: {type: Array, default: () => []},
  defaultQuality: {type: String, default: ''},
  showBackBtn: {type: Boolean, default: true}, // 控制是否显示返回按钮
  showCloseBtn: {type: Boolean, default: true}  // 控制是否显示关闭按钮
})

// 事件发射（新增 back 和 close 事件）
const emit = defineEmits([
  'play', 'pause', 'ended', 'error', 'progress-change',
  'loaded', 'fullscreen-change', 'pip-change', 'quality-change',
  'back', 'close' // 新增事件：返回、关闭
])

// 核心状态
const videoRef = ref(null)
const player = ref(null)
const isPlaying = ref(false)
const isMuted = ref(false)
const isLoading = ref(false)
const isError = ref(false)
const isFullscreen = ref(false)
const isPiP = ref(false)
const supportsPiP = ref(!!document.pictureInPictureEnabled)
const showControls = ref(false)
const volume = ref(0.8)
const currentTime = ref(0)
const duration = ref(0)
const progressPercent = ref(0)
const bufferedPercent = ref(0)
const playbackRate = ref(1)
const currentQuality = ref(props.defaultQuality || (props.videoQualities[0]?.value || ''))
const errorMessage = ref('')
const videoWrapperWidth = ref(0) // 视频包裹层宽度（自适应计算）
const videoAspectRatio = ref(16 / 9) // 视频原始宽高比（默认16:9，加载后更新）

// 格式化时间
const formatTime = (time) => {
  if (isNaN(time)) return '00:00'
  const hours = Math.floor(time / 3600)
  const minutes = Math.floor((time % 3600) / 60)
  const seconds = Math.floor(time % 60)
  return hours > 0
      ? `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      : `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 计算视频包裹层宽度（根据视频宽高比和100vh高度）
const calculateVideoWrapperWidth = () => {
  const viewportHeight = window.innerHeight // 100vh = 视口高度
  // 宽度 = 高度 * 视频宽高比（确保视频高度=100vh，宽度自适应比例）
  const calculatedWidth = viewportHeight * videoAspectRatio.value
  // 限制最大宽度不超过视口宽度（避免横向溢出）
  videoWrapperWidth.value = Math.min(calculatedWidth, window.innerWidth)
}

// 初始化 video.js 播放器
const initPlayer = () => {
  if (!videoRef.value || player.value) return

  const sources = props.videoSources.length
      ? props.videoSources
      : props.videoSrc
          ? [{src: props.videoSrc, type: getVideoMimeType(props.videoSrc)}]
          : []

  // 配置：禁用fluid，强制高度100%，宽度自适应
  const options = {
    sources,
    poster: props.poster,
    preload: props.preload,
    autoplay: false,
    controls: false,
    responsive: true,
    fluid: false,
    playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2],
    fill: true
  }

  player.value = videojs(videoRef.value, options, () => {
    console.log('视频播放器初始化完成')
    player.value.volume(volume.value)
    setupPlayerListeners()
    emit('loaded')
  })
}

// 获取视频 MIME 类型
const getVideoMimeType = (url) => {
  const ext = url.split('.').pop().toLowerCase()
  const mimeMap = {
    mp4: 'video/mp4', webm: 'video/webm', ogg: 'video/ogg',
    avi: 'video/x-msvideo', mov: 'video/quicktime', flv: 'video/x-flv',
    mkv: 'video/x-matroska', wmv: 'video/x-ms-wmv', m4v: 'video/x-m4v'
  }
  return mimeMap[ext] || 'video/mp4'
}

// 监听播放器事件（新增loadedmetadata获取视频宽高比）
const setupPlayerListeners = () => {
  if (!player.value) return

  // 视频元数据加载完成：获取原始宽高比
  player.value.on('loadedmetadata', () => {
    const video = videoRef.value
    if (video.videoWidth && video.videoHeight) {
      videoAspectRatio.value = video.videoWidth / video.videoHeight // 宽/高 比例
      calculateVideoWrapperWidth() // 计算自适应宽度
    }
  })

  player.value.on('play', () => {
    isPlaying.value = true;
    isLoading.value = false;
    emit('play')
  })
  player.value.on('pause', () => {
    isPlaying.value = false;
    emit('pause')
  })
  player.value.on('waiting', () => {
    isLoading.value = true
  })
  player.value.on('canplay', () => {
    isLoading.value = false
  })
  player.value.on('timeupdate', () => {
    currentTime.value = player.value.currentTime()
    duration.value = player.value.duration() || 0
    progressPercent.value = duration.value ? (currentTime.value / duration.value) * 100 : 0
  })
  player.value.on('progress', () => {
    const buffered = player.value.buffered()
    if (buffered.length > 0) {
      const bufferedEnd = buffered.end(buffered.length - 1)
      bufferedPercent.value = (bufferedEnd / (player.value.duration() || 1)) * 100
    }
  })
  player.value.on('volumechange', () => {
    volume.value = player.value.volume()
    isMuted.value = player.value.muted()
  })
  player.value.on('ended', () => {
    isPlaying.value = false;
    emit('ended')
  })
  player.value.on('error', (err) => {
    isLoading.value = false;
    isError.value = true;
    errorMessage.value = getErrorMsg(player.value.error());
    emit('error', err)
  })
  player.value.on('fullscreenchange', () => {
    isFullscreen.value = player.value.isFullscreen()
    emit('fullscreen-change', isFullscreen.value)
  })

  if (supportsPiP.value) {
    videoRef.value.addEventListener('enterpictureinpicture', () => {
      isPiP.value = true;
      emit('pip-change', true)
    })
    videoRef.value.addEventListener('leavepictureinpicture', () => {
      isPiP.value = false;
      emit('pip-change', false)
    })
  }
}

// 解析错误信息
const getErrorMsg = (error) => {
  if (!error) return '未知错误'
  const errorMap = {
    2: '网络错误，无法加载视频',
    3: '视频解码失败，格式不支持',
    4: '视频地址无效或无法访问',
    5: '视频加载超时'
  }
  return errorMap[error.code] || `错误码: ${error.code}，${error.message}`
}

// 核心控制方法
const togglePlay = () => {
  if (!player.value || isError.value) return;
  player.value.paused() ? player.value.play() : player.value.pause()
}
const toggleMute = () => {
  if (!player.value) return;
  const isMutedNow = player.value.muted();
  player.value.muted(!isMutedNow);
  isMuted.value = !isMutedNow
}
const handleSpeedChange = () => {
  if (!player.value) return;
  player.value.playbackRate(playbackRate.value)
}
const handleQualityChange = () => {
  if (!player.value || !props.videoQualities.length) return
  const qualityItem = props.videoQualities.find(item => item.value === currentQuality.value)
  if (qualityItem && qualityItem.src) {
    const currentTime = player.value.currentTime()
    player.value.src({src: qualityItem.src, type: getVideoMimeType(qualityItem.src)})
    player.value.one('loadedmetadata', () => {
      player.value.currentTime(currentTime)
      if (isPlaying.value) player.value.play()
      // 重新计算宽高比（切换清晰度可能改变视频比例）
      const video = videoRef.value
      if (video.videoWidth && video.videoHeight) {
        videoAspectRatio.value = video.videoWidth / video.videoHeight
        calculateVideoWrapperWidth()
      }
    })
    emit('quality-change', currentQuality.value)
  }
}
const handleProgressClick = (e) => {
  if (!player.value || duration.value === 0 || isLoading.value) return
  const containerWidth = e.currentTarget.offsetWidth
  const clickX = e.offsetX
  const newPercent = (clickX / containerWidth) * 100
  const newTime = (newPercent / 100) * duration.value
  currentTime.value = newTime;
  progressPercent.value = newPercent;
  player.value.currentTime(newTime);
  emit('progress-change', newTime)
}
const handleVolumeChange = () => {
  if (!player.value) return;
  player.value.volume(volume.value);
  if (volume.value > 0 && isMuted.value) {
    player.value.muted(false);
    isMuted.value = false
  }
}
const toggleFullscreen = () => {
  if (!player.value) return;
  player.value.isFullscreen() ? player.value.exitFullscreen() : player.value.requestFullscreen()
}
const togglePictureInPicture = async () => {
  if (!supportsPiP.value || !videoRef.value) return
  try {
    document.pictureInPictureElement ? await document.exitPictureInPicture() : await videoRef.value.requestPictureInPicture()
  } catch (err) {
    console.error('画中画模式切换失败:', err);
    errorMessage.value = '画中画模式不支持'
  }
}
const reloadVideo = () => {
  isError.value = false;
  errorMessage.value = ''
  if (player.value) {
    player.value.load();
    isLoading.value = true
  } else {
    initPlayer()
  }
}

// 新增：返回按钮点击事件（发射 back 事件）
const handleBack = () => {
  emit('back')
  if (player.value && isPlaying.value) {
    player.value.pause()
  }
}

// 新增：关闭按钮点击事件（发射 close 事件）
const handleClose = () => {
  emit('close')
  // 关闭时暂停视频（可选）
  if (player.value && isPlaying.value) {
    player.value.pause()
  }
}

// 监听窗口大小变化，重新计算宽度
const handleResize = () => {
  calculateVideoWrapperWidth()
  // 通知 video.js 窗口变化，更新播放器尺寸
  // if (player.value) {
    // player.value.handleResize()
  // }
}

// 监听视频源变化
watch(
    () => [props.videoSrc, props.videoSources],
    () => {
      isPlaying.value = false;
      isError.value = false;
      currentTime.value = 0;
      progressPercent.value = 0;
      bufferedPercent.value = 0;
      duration.value = 0
      // 重置宽高比为默认值
      videoAspectRatio.value = 16 / 9
      videoWrapperWidth.value = 0

      if (player.value) {
        const sources = props.videoSources.length
            ? props.videoSources
            : props.videoSrc
                ? [{src: props.videoSrc, type: getVideoMimeType(props.videoSrc)}]
                : []
        player.value.src(sources);
        player.value.load();
        isLoading.value = true
      } else {
        initPlayer()
      }
    },
    {deep: true, immediate: true}
)

// 监听默认清晰度变化
watch(() => props.defaultQuality, (newVal) => {
  if (newVal && props.videoQualities.some(item => item.value === newVal)) {
    currentQuality.value = newVal
  }
}, {immediate: true})

watch(() => props.poster, (newVal) => {
  if (newVal && player.value) {
    player.value.poster(newVal)
  }
}, {immediate: true})

// 初始化与销毁
onMounted(() => {
  initPlayer()
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
  // 初始计算一次宽度
  nextTick(() => {
    calculateVideoWrapperWidth()
  })
})

onUnmounted(() => {
  if (player.value) {
    player.value.dispose();
    player.value = null
  }
  if (document.pictureInPictureElement) {
    document.exitPictureInPicture().catch(err => console.error('退出画中画失败:', err))
  }
  // 移除窗口 resize 监听
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 容器：宽度100%，高度100vh，背景黑色（左右留白时显示） */
.full-format-video-player {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
  position: relative;
  background: #000; /* 左右留白时显示黑色背景 */
}

/* 新增：顶部控制栏（返回+关闭） */
.top-controls {
  position: absolute;
  top: 20px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
  z-index: 200; /* 确保在视频和控制栏之上 */
}

.back-btn, .close-btn {
  background: rgba(0, 0, 0, 0.5); /* 半透明黑色背景 */
  backdrop-filter: blur(4px); /* 毛玻璃效果（增强视觉） */
  border: none;
  color: white;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.back-btn:hover, .close-btn:hover {
  background: rgba(0, 0, 0, 0.7);
  transform: scale(1.05);
}

/* 隐藏不需要的按钮（通过props控制） */
.back-btn:not([aria-hidden="true"]) {
  display: flex;
}

.back-btn[aria-hidden="true"] {
  display: none;
}

.close-btn:not([aria-hidden="true"]) {
  display: flex;
}

.close-btn[aria-hidden="true"] {
  display: none;
}

.title-wrap{
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  text-wrap: nowrap;
  color: #fff;
  text-align: left;
  line-height: 36px;
  padding: 0 20px;
}
/* 视频容器：高度100vh，水平居中 */
.video-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
}

/* 视频包裹层：宽度自适应计算，高度100vh */
.video-wrapper {
  height: 100vh;
  position: relative;
}

/* 视频元素：宽度100%，高度100%，完整显示无裁剪 */
video {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 关键：完整显示视频，不拉伸、不裁剪 */
  object-position: center;
}

/* 隐藏 video.js 原生控件 */
::v-deep .vjs-big-play-button {
  display: none !important;
}

::v-deep .video-js .vjs-control-bar {
  display: none !important;
}

/* 自定义控制栏：水平居中，跟随视频包裹层宽度 */
.custom-controls {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%); /* 水平居中 */
  padding: 16px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0) 100%);
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 100;
  box-sizing: border-box;
  border-radius: 8px;
}

.custom-controls.visible {
  opacity: 1;
}

/* 进度条 */
.progress-container {
  width: 100%;
  margin-bottom: 8px;
  position: relative;
}

.progress-bg {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  position: relative;
  cursor: pointer;
}

.progress-buffered {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 2px;
  z-index: 1;
}

.progress-played {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 2px;
  z-index: 2;
  transition: width 0.2s ease;
}

.progress-handle {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 0 2px white;
  z-index: 3;
  transition: left 0.2s ease, transform 0.2s ease;
}

.progress-bg:hover .progress-handle {
  transform: translateY(-50%) scale(1.2);
}

.time-display {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 4px;
  display: flex;
  gap: 4px;
}

/* 控制按钮组 */
.controls-group {
  display: flex;
  align-items: center;
  gap: 16px;
}

.control-btn {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.control-btn:disabled {
  color: rgba(255, 255, 255, 0.3);
  cursor: not-allowed;
}

.control-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

/* 音量控制 */
.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100px;
}

.volume-slider {
  width: 100%;
  height: 2px;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 1px;
  outline: none;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: white;
  cursor: pointer;
}

/* 倍速/画质选择 */
.speed-select, .quality-select {
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 12px;
  cursor: pointer;
  outline: none;
}

.speed-select option, .quality-select option {
  background: #333;
}

/* 加载中动画 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误提示 */
.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.7);
  z-index: 200;
  color: white;
}

.error-content {
  text-align: center;
  padding: 24px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.5);
  max-width: 300px;
}

.error-icon {
  font-size: 32px;
  margin-bottom: 16px;
  display: block;
}

.error-message {
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.5;
}

.retry-btn {
  padding: 8px 24px;
  border-radius: 4px;
  border: none;
  background: v-bind(primaryColor);
  color: white;
  cursor: pointer;
  transition: background 0.2s ease;
}

.retry-btn:hover {
  background: darken(v-bind(primaryColor), 10%);
}

/* 全屏适配 */
.full-format-video-player.is-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw !important;
  height: 100vh !important;
  z-index: 9999;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .top-controls {
    padding: 0 15px;
  }

  .back-btn, .close-btn {
    width: 36px;
    height: 36px;
  }

  .controls-group {
    gap: 8px;
  }

  .volume-control {
    width: 80px;
  }

  .speed-select, .quality-select {
    padding: 3px 6px;
    font-size: 11px;
  }

  .control-btn {
    width: 32px;
    height: 32px;
  }

  .custom-controls {
    padding: 12px;
    bottom: 15px;
  }

  .loading-spinner {
    width: 32px;
    height: 32px;
  }
}
</style>