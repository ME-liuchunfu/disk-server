<template>
  <div class="tra-disk-view">
    <div class="disk-header">
      <h1 class="disk-title">
        <el-icon><HardDrive /></el-icon> 云存储
      </h1>
      <div class="disk-stats">
        <span>已使用: {{ usedStorage }}</span>
        <span>总容量: {{ totalStorage }}</span>
      </div>
    </div>

    <div class="disk-upload">
      <file-uploader @upload-complete="fetchFileList"></file-uploader>
    </div>

    <div class="disk-content">
      <FileList :file-list="fileList"></FileList>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { HardDrive } from '@element-plus/icons-vue'
import FileUploader from '@/components/file/FileUploader.vue'
import FileList from '@/components/file/FileList.vue'
import { formatFileSize } from '@/utils/format'

// 模拟数据
const fileList = ref([])
const usedStorage = ref('0 MB')
const totalStorage = ref('10 GB')

// 获取文件列表
const fetchFileList = () => {
  // 实际项目中替换为真实API请求
  // 这里使用模拟数据
  fileList.value = [
    {
      id: '1',
      name: 'nature.jpg',
      size: 2097152, // 2MB
      type: 'image',
      mimeType: 'image/jpeg',
      url: 'https://picsum.photos/1200/800',
      modifyTime: '2023-11-01 14:30:00'
    },
    {
      id: '2',
      name: 'document.txt',
      size: 10240, // 10KB
      type: 'text',
      mimeType: 'text/plain',
      url: '/sample.txt',
      modifyTime: '2023-11-02 09:15:00'
    },
    {
      id: '3',
      name: 'music.mp3',
      size: 5242880, // 5MB
      type: 'audio',
      mimeType: 'audio/mpeg',
      url: '/sample.mp3',
      modifyTime: '2023-11-03 16:45:00'
    },
    {
      id: '4',
      name: 'video.mp4',
      size: 15728640, // 15MB
      type: 'video',
      mimeType: 'video/mp4',
      url: '/sample.mp4',
      modifyTime: '2023-11-04 11:20:00'
    },
    {
      id: '5',
      name: 'data.json',
      size: 2048, // 2KB
      type: 'text',
      mimeType: 'application/json',
      url: '/sample.json',
      modifyTime: '2023-11-05 10:05:00'
    }
  ]

  // 计算已使用存储
  const total = fileList.value.reduce((sum, file) => sum + file.size, 0)
  usedStorage.value = formatFileSize(total)
}

onMounted(() => {
  fetchFileList()
})
</script>

<style scoped>
.tra-disk-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.disk-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.disk-title {
  margin: 0;
  color: #333;
  display: flex;
  align-items: center;
  gap: 10px;
}

.disk-stats {
  color: #666;
  font-size: 14px;
}

.disk-stats span {
  margin-left: 15px;
}

.disk-upload {
  margin-bottom: 20px;
}

.disk-content {
  flex: 1;
}
</style>