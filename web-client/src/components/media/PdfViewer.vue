<template>
  <div class="pdf-viewer-container">
    <!-- 工具栏 -->
    <div class="pdf-toolbar" v-if="iframeSrc">
      <div class="toolbar-left">
        <button
            class="toolbar-btn"
            @click="handleClose"
            aria-label="返回"
        >
          返回
        </button>
        <button
            class="toolbar-btn"
            @click="downloadPdf"
            :disabled="!allowDownload"
            aria-label="下载PDF"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="7 10 12 15 17 10"/>
            <line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
        </button>
        <button
            class="toolbar-btn"
            @click="printPdf"
            :disabled="!allowPrint"
            aria-label="打印PDF"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="6 9 6 2 18 2 18 9"/>
            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
            <rect x="6" y="14" width="12" height="8"/>
          </svg>
        </button>
      </div>

      <div class="toolbar-middle">
        <button
            class="toolbar-btn"
            @click="zoomOut"
            :disabled="zoom <= 0.5 || !allowZoom"
            aria-label="缩小"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            <line x1="11" y1="8" x2="11" y2="14"/>
            <line x1="8" y1="11" x2="14" y2="11"/>
          </svg>
        </button>
        <span class="zoom-info">{{ Math.round(zoom * 100) }}%</span>
        <button
            class="toolbar-btn"
            @click="zoomIn"
            :disabled="zoom >= 2 || !allowZoom"
            aria-label="放大"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            <line x1="11" y1="8" x2="11" y2="14"/>
            <line x1="8" y1="11" x2="14" y2="11"/>
          </svg>
        </button>
        <button
            class="toolbar-btn"
            @click="resetZoom"
            :disabled="!allowZoom"
            aria-label="重置缩放"
        >
          重置
        </button>
        <button
            class="toolbar-btn"
            @click="reloadPdf"
            aria-label="重载"
        >
          重载
        </button>
      </div>

      <div class="toolbar-right">
        <button
            class="toolbar-btn"
            @click="openInNewTab"
            aria-label="新窗口打开"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
            <polyline points="15 3 21 3 21 9"/>
            <line x1="10" y1="14" x2="21" y2="3"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 加载中 -->
    <div class="pdf-loading" v-if="isLoading && !isError">
      <div class="loading-spinner"></div>
      <p class="loading-text">加载 PDF 中...</p>
    </div>

    <!-- 错误提示 -->
    <div class="pdf-error" v-if="isError">
      <div class="error-icon">⚠️</div>
      <p class="error-message">{{ errorMessage || 'PDF 加载失败，请检查文件地址或格式是否正确' }}</p>
      <button class="retry-btn" @click="reloadPdf">重试</button>
    </div>

    <!-- 原生 PDF 预览容器（iframe 嵌入） -->
    <div class="pdf-content" v-if="iframeSrc">
      <iframe
          ref="pdfIframe"
          :src="iframeSrc"
          :style="{ transform: `scale(${zoom})`, transformOrigin: 'top left' }"
          class="pdf-iframe"
          frameborder="0"
          scrolling="auto"
          @load="handleIframeLoad"
          @error="handleIframeError"
          aria-label="PDF 预览"
      ></iframe>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'
import eventBus from "@/utils/eventBus";

eventBus.on('media-event:prepdf', event => {
  if (event['type'] === 'show') {
    iframeSrc.value = event['value']['url']
    reloadPdf()
  }
})

// Props 定义
const props = defineProps({
  initialZoom: { type: Number, default: 1 }, // 初始缩放（1=100%）
  allowDownload: { type: Boolean, default: true }, // 允许下载
  allowPrint: { type: Boolean, default: true }, // 允许打印
  allowZoom: { type: Boolean, default: true }, // 允许缩放
  proxyUrl: { type: String, default: '' }, // 跨域代理地址
  height: { type: [String, Number], default: 'calc(100vh - 140px)' }, // 预览高度
})

// 事件发射
const emit = defineEmits(['loaded', 'error', 'zoom-change', 'close'])

// 核心状态
const pdfIframe = ref(null) // iframe 引用
const zoom = ref(props.initialZoom) // 缩放比例
const isLoading = ref(false) // 加载状态
const isError = ref(false) // 错误状态
const errorMessage = ref('') // 错误信息
const iframeSrc = ref('')

const handleClose = () => {
  emit('close')
}

// 重新加载 PDF
const reloadPdf = () => {
  // 刷新 iframe（强制重新加载）
  if (pdfIframe.value) {
    pdfIframe.value.src = iframeSrc.value;
  }
}

// 处理 iframe 加载完成
const handleIframeLoad = () => {
  isLoading.value = false
  emit('loaded')
}

// 处理 iframe 加载错误
const handleIframeError = () => {
  isLoading.value = false
  isError.value = true
  errorMessage.value = '浏览器不支持 PDF 预览，或文件无法访问'
  emit('error', new Error('iframe 加载 PDF 失败'))
}

// 缩放控制
const zoomIn = () => {
  zoom.value = Math.min(zoom.value + 0.1, 2) // 最大 200%
  emit('zoom-change', zoom.value)
}

const zoomOut = () => {
  zoom.value = Math.max(zoom.value - 0.1, 0.5) // 最小 50%
  emit('zoom-change', zoom.value)
}

const resetZoom = () => {
  zoom.value = 1
  emit('zoom-change', zoom.value)
}

// 下载 PDF
const downloadPdf = () => {
  if (!iframeSrc.value) return

  const link = document.createElement('a')
  link.href = iframeSrc.value
  link.download = props.pdfFile?.name || 'preview.pdf'
  link.click()
}

// 打印 PDF（调用浏览器打印功能）
const printPdf = () => {
  if (pdfIframe.value) {
    pdfIframe.value.contentWindow.print()
  }
}

// 新窗口打开 PDF
const openInNewTab = () => {
  if (iframeSrc.value) {
    window.open(iframeSrc.value, '_blank')
  }
}

// 监听初始缩放比例变化
watch(
    () => props.initialZoom,
    (newVal) => {
      zoom.value = newVal
    },
    { immediate: true }
)

</script>

<style scoped>
.pdf-viewer-container {
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f5f5;
  position: relative;
}

/* 工具栏 */
.pdf-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar-btn {
  background: transparent;
  border: none;
  color: #333;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.toolbar-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.toolbar-btn:hover:not(:disabled) {
  background-color: #f0f0f0;
}

.zoom-info {
  color: #666;
  font-size: 14px;
  margin: 0 8px;
}

.toolbar-left, .toolbar-middle, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 加载中 */
.pdf-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #42b983;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.loading-text {
  color: #666;
  font-size: 14px;
}

/* 错误提示 */
.pdf-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  text-align: center;
  padding: 20px;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-message {
  color: #ff4d4f;
  font-size: 14px;
  margin-bottom: 20px;
  max-width: 400px;
}

.retry-btn {
  padding: 8px 24px;
  border: none;
  border-radius: 4px;
  background-color: #42b983;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.retry-btn:hover {
  background-color: #359469;
}

/* 空白状态 */
.pdf-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
  font-size: 14px;
}

/* PDF 内容容器 */
.pdf-content {
  width: 100%;
  overflow: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  box-sizing: border-box;
  /* 适配缩放后的滚动 */
  min-height: v-bind(height);
}

/* iframe 样式 */
.pdf-iframe {
  width: 100%;
  height: 100%;
  min-height: v-bind(height);
  transition: transform 0.3s ease;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .pdf-viewer-container {
    padding: 10px;
  }

  .pdf-toolbar {
    padding: 8px 10px;
    gap: 5px;
  }

  .toolbar-btn {
    width: 32px;
    height: 32px;
  }

  .zoom-info {
    font-size: 12px;
    margin: 0 4px;
  }

  .toolbar-left, .toolbar-middle, .toolbar-right {
    gap: 4px;
  }

  .toolbar-middle {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 8px;
  }

  .pdf-content {
    padding: 10px;
    min-height: calc(100vh - 120px);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>