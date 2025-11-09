<template>
  <div class="tra-file-list">
    <el-table
        :data="fileList"
        border
        stripe
        :header-cell-style="{ background: '#f5f7fa' }"
    >
      <el-table-column prop="name" label="文件名" width="300">
        <template #default="scope">
          <div class="file-name">
            <el-icon :class="getFileIcon(scope.row.type)"><component :is="getFileIcon(scope.row.type)"></component></el-icon>
            <span>{{ scope.row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小" width="120">
        <template #default="scope">{{ formatSize(scope.row.size) }}</template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100"></el-table-column>
      <el-table-column prop="modifyTime" label="修改时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="handlePreview(scope.row)"
          >
            <el-icon><Eye /></el-icon> 预览
          </el-button>
          <el-button
              size="small"
              type="success"
              @click="handleDownload(scope.row)"
          >
            <el-icon><Download /></el-icon> 下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 预览弹窗 -->
    <el-dialog
        v-model="previewVisible"
        :title="currentFile?.name"
        width="85%"
        :fullscreen="isFullscreen"
    >
      <file-preview
          :file="currentFile"
          @toggle-fullscreen="isFullscreen = !isFullscreen"
      ></file-preview>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  Image as ImageIcon,
  Video as VideoIcon,
  Music as MusicIcon,
  FileText as TextIcon,
  File as OtherIcon,
  Eye,
  Download
} from '@element-plus/icons-vue'
import FilePreview from '@/components/file/FilePreview.vue'
import { formatFileSize } from '@/utils/format'

const previewVisible = ref(false)
const currentFile = ref(null)
const isFullscreen = ref(false)

// 获取文件图标
const getFileIcon = (type) => {
  switch (type) {
    case 'image': return ImageIcon
    case 'video': return VideoIcon
    case 'audio': return MusicIcon
    case 'text': return TextIcon
    default: return OtherIcon
  }
}

// 格式化文件大小
const formatSize = (size) => {
  return formatFileSize(size)
}

// 处理预览
const handlePreview = (file) => {
  currentFile.value = file
  previewVisible.value = true
  isFullscreen.value = false
}

// 处理下载
const handleDownload = (file) => {
  const link = document.createElement('a')
  link.href = file.url
  link.download = file.name
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<style scoped>
.tra-file-list {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>