<template>
  <div class="tra-uploader">
    <el-upload
        class="upload-area"
        action="/api/upload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :file-list="fileList"
        :auto-upload="true"
        multiple
        drag
    >
      <el-icon class="upload-icon"><UploadFilled /></el-icon>
      <div class="upload-text">
        <p>拖拽文件到此处上传</p>
        <p>或 <span class="upload-link">点击选择文件</span></p>
      </div>
      <template #tip>
        <div class="upload-tip">
          支持格式: 图片、视频、音频、文本等
        </div>
      </template>
    </el-upload>

    <!-- 上传进度条 -->
    <el-progress
        v-if="progress > 0 && progress < 100"
        :percentage="progress"
        stroke-width="2"
        class="upload-progress"
    ></el-progress>
  </div>
</template>
<!-- components/file/FileUploader.vue -->
<script setup>
import { ref, defineEmits } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElUpload, ElProgress } from 'element-plus'

const fileList = ref([])
const progress = ref(0)

// 正确声明自定义事件（无需导入defineEmits）
const emit = defineEmits(['upload-complete'])  // 修复这里的写法

const handleSuccess = () => {
  ElMessage.success('文件上传成功')
  progress.value = 0
  emit('upload-complete')  // 触发事件
}

const handleError = () => {
  ElMessage.error('文件上传失败')
  progress.value = 0
}

const handleProgress = (event) => {
  progress.value = Math.floor(event.percent)
}
</script>

<style scoped>
.tra-uploader {
  margin-bottom: 20px;
}

.upload-area {
  border: 2px dashed #ccc;
  border-radius: 6px;
  padding: 30px;
  text-align: center;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
}

.upload-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.upload-text {
  color: #666;
}

.upload-link {
  color: #409eff;
  cursor: pointer;
}

.upload-tip {
  margin-top: 10px;
  color: #999;
  font-size: 12px;
}

.upload-progress {
  margin-top: 10px;
}
</style>