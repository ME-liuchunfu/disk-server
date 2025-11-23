<template>
  <div class="doc-viewer-container">
    <!-- 工具栏 -->
    <div class="doc-toolbar">
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
      </div>
      <div class="toolbar-middle">
        <h2>{{filename}}</h2>
      </div>
    </div>

    <div class="doc-content">
      <div ref="previewRef" class="docx-preview" />
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue'
import eventBus from "@/utils/eventBus";
import { renderAsync } from 'docx-preview';

eventBus.on('media-event:predoc', event => {
  if (event['type'] === 'show') {
    reset();
    docUrl.value = event['value']['url']
    filename.value = event['value']['title']
    loadDoc()
  }
})

// 事件发射
const emit = defineEmits(['close'])

// 核心状态
const filename = ref('') // 加载状态
const docUrl = ref('')
const previewRef = ref(null);

const handleClose = () => {
  emit('close')
  reset()
}

const reset = () => {
  filename.value = ''
  docUrl.value = ''
  previewRef.value = null
}

const loadDoc = async () => {
  if (!docUrl.value) return;
  // 处理File对象转Blob
  const blob = typeof docUrl.value === 'string'
      ? await fetch(docUrl.value).then(res => res.blob())
      : docUrl;
  // 渲染预览（支持自定义样式、工具栏）
  await renderAsync(blob, previewRef.value, null, {
    className: 'custom-docx-style',
    inWrapper: true,
    ignoreWidth: false,
    ignoreHeight: false,
  });
}

// 下载 PDF
const downloadPdf = () => {
  if (!docUrl.value) return

  const link = document.createElement('a')
  link.href = docUrl.value
  link.download = filename.value || 'preview.pdf'
  link.click()
}

</script>

<style scoped>
.doc-viewer-container {
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f5f5;
  position: relative;
}

/* 工具栏 */
.doc-toolbar {
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

.toolbar-left, .toolbar-middle, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 加载中 */
.doc-loading {
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
.doc-error {
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
.doc-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
  font-size: 14px;
}

/* PDF 内容容器 */
.doc-content {
  height: calc(100vh - 120px);
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

.toolbar-middle {
  order: 3;
  width: 100%;
  justify-content: center;
  margin-top: 8px;
  text-align: center;
  flex: 1;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>