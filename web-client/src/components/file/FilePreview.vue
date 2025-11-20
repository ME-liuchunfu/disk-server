<template>
  <div class="tra-file-preview">
    <!-- 工具栏 -->
    <div class="preview-toolbar">
      <el-button
          size="small"
          @click="$emit('toggle-fullscreen')"
      >
        {{ isFullscreen ? '退出全屏' : '全屏' }}
      </el-button>
    </div>

    <!-- 图片预览 -->
    <div v-if="fileType === 'image'" class="preview-image">
      <el-image
          :src="url"
          fit="contain"
          preview-teleported
          class="preview-img"
      ></el-image>
    </div>

    <!-- 视频预览 -->
    <div v-if="fileType === 'video'" class="preview-video">
      <video
          id="video-player"
          class="video-js vjs-default-skin vjs-big-play-centered"
          controls
          preload="auto"
          width="100%"
          height="auto"
      >
        <source :src="url" :type="fileType">
      </video>
    </div>

    <!-- 音频预览 -->
    <div v-if="fileType === 'audio'" class="preview-audio">
      <audio
          controls
          class="audio-player"
      >
        <source :src="url" :type="fileType">
      </audio>
    </div>

    <!-- xlsx -->
    <div v-if="'xlsx' === fileType" class="preview-audio">
      <vue-office-docx :src="url" />
    </div>

    <!-- pdf -->
    <div v-if="'pdf' === fileType" class="preview-audio">
      <vue-office-pdf :src="url" />
    </div>

    <!-- 文本预览 -->
    <div v-if="fileType === 'text'" class="preview-text">
      <pre v-if="loading" class="loading">加载中...</pre>
      <pre v-else-if="error" class="error">无法加载文件内容</pre>
      <div v-else>
        <highlightjs
            :code="content"
            :language="getFileLanguage()"
            class="hljs"
        ></highlightjs>
      </div>
    </div>

    <!-- 不支持的类型 -->
    <div v-if="fileType === 'other'" class="preview-unsupported">
      <el-icon class="unsupported-icon"><WarningFilled /></el-icon>
      <p>不支持预览此文件类型</p>
      <p>文件类型: {{ fileType }}</p>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  onMounted,
  defineProps,
  onUnmounted,
} from 'vue'
import { WarningFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

import hljs from 'highlight.js/lib/core';  // 核心库
import javascript from 'highlight.js/lib/languages/javascript';  // 正确路径
import html from 'highlight.js/lib/languages/htmlbars';  // 正确路径
import css from 'highlight.js/lib/languages/css';
import json from 'highlight.js/lib/languages/json';
import VueOfficeDocx from 'vue-office/lib/docx'
import VueOfficePdf from "vue-office/lib/pdf";
// import VueOfficePdf from 'vue-office/lib/pdf'


// 注册语言
hljs.registerLanguage('javascript', javascript)
hljs.registerLanguage('html', html)
hljs.registerLanguage('css', css)
hljs.registerLanguage('json', json)

const props = defineProps({
  url: {
    type: String,
    required: true,
    default: () => ''
  },
  fileType: {
    type: String,
    required: true,
    default: () => ''
  }
})

// 状态
const content = ref('')
const loading = ref(false)
const error = ref(false)
const videoPlayer = ref(null)
const isFullscreen = ref(false)

// 获取文本文件语言
const getFileLanguage = () => {
  const name = props.fileType || ''
  if (name.endsWith('js')) return 'javascript'
  if (name.endsWith('html')) return 'html'
  if (name.endsWith('css')) return 'css'
  if (name.endsWith('json')) return 'json'
  return 'plaintext'
}

// 加载文本内容
const loadTextContent = () => {
  loading.value = true
  axios.get(props.url)
      .then(res => {
        content.value = res.data
        loading.value = false
        error.value = false
      })
      .catch(err => {
        console.error('加载文本失败', err)
        loading.value = false
        error.value = true
      })
}

// 初始化视频播放器
const initVideoPlayer = () => {
  const videoElement = document.getElementById('video-player')
  if (videoElement && !videoPlayer.value) {
    videoPlayer.value = videojs(videoElement, {
      autoplay: false,
      controls: true,
      responsive: true,
      fluid: true
    })
  }
}

onMounted(() => {
  if (props.fileType.value === 'text' || props.fileType.value === 'txt') {
    loadTextContent()
  } else if (props.fileType.value === 'video') {
    initVideoPlayer()
  }
})

// 清理资源
onUnmounted(() => {
  if (videoPlayer.value) {
    videoPlayer.value.dispose()
  }
})
</script>

<style scoped>
.tra-file-preview {
  width: 100%;
  height: calc(100% - 40px);
  min-height: 500px;
}

.preview-toolbar {
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.preview-image {
  width: 100%;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
}

.preview-img {
  max-width: 100%;
  max-height: 100%;
}

.preview-video {
  width: 100%;
  background: #000;
  border-radius: 4px;
}

.video-js {
  width: 100% !important;
  height: 500px !important;
}

.preview-audio {
  width: 100%;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.audio-player {
  width: 100%;
}

.preview-text {
  width: 100%;
  height: 500px;
  overflow: auto;
}

.preview-text pre {
  padding: 16px;
  margin: 0;
}

.loading {
  color: #666;
  text-align: center;
  padding: 20px;
}

.error {
  color: #f56c6c;
  text-align: center;
  padding: 20px;
}

.preview-unsupported {
  width: 100%;
  height: 500px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
}

.unsupported-icon {
  font-size: 48px;
  color: #faad14;
  margin-bottom: 16px;
}
</style>