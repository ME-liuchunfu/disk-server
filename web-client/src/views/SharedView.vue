<!-- src/views/SharedView.vue -->
<template>
  <div class="shared-view">
    <h2>共享文件</h2>
    <p>此处展示所有共享给你的文件</p>
    <!-- 可复用之前的FileUploader和FileList组件 -->
    <file-uploader @upload-complete="fetchSharedFiles"></file-uploader>
    <FileList :file-list="sharedFiles"></FileList>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FileUploader from '@/components/file/FileUploader.vue'
import FileList from '@/components/file/FileList.vue'

const sharedFiles = ref([])

// 模拟获取共享文件列表
const fetchSharedFiles = () => {
  sharedFiles.value = [
    {
      id: 's1',
      name: '团队计划.xlsx',
      size: 307200,
      type: 'other',
      mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      url: '/team-plan.xlsx',
      modifyTime: '2023-11-06 14:30:00'
    }
    // 可添加更多模拟数据
  ]
}
</script>

<style scoped>
.shared-view {
  padding: 20px;
}
</style>