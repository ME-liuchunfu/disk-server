<!-- views/DiskView.vue -->
<template>
    <div class="disk-view">
        <!-- 顶部操作栏 -->
        <div class="disk-header">
            <h1 class="disk-title">
                <el-icon><HardDrive /></el-icon> 全部文件
            </h1>
            <div class="disk-stats">
                <span>已加载: {{ fileList.length }} 个项目</span>
                <el-button type="primary" @click="refreshFileList">
                    <el-icon><Refresh /></el-icon> 刷新
                </el-button>
            </div>
        </div>

        <!-- 上传区域 -->
<!--        <file-uploader @upload-complete="refreshFileList" class="upload-area" />-->

        <!-- 视图切换 -->
        <div class="view-switch">
            <el-button-group>
                <el-button
                        type="primary"
                        :icon="Folder"
                        :class="{ 'is-active': viewType === 'grid' }"
                        @click="viewType = 'grid'"
                ></el-button>
                <el-button
                        :icon="Grid"
                        :class="{ 'is-active': viewType === 'list' }"
                        @click="viewType = 'list'"
                ></el-button>
            </el-button-group>
        </div>

        <!-- 网格视图（默认） -->
        <div class="file-grid" v-if="viewType === 'grid'">
            <div
                    v-for="file in fileList"
                    :key="file.id"
                    class="file-item"
                    @click="handlePreview(file)"
                    @contextmenu.prevent="showContextMenu(file, $event)"
            >
                <div class="file-icon">
                    <el-icon :size="40">
                        <component :is="getFileIcon(file.type)"></component>
                    </el-icon>
                </div>
                <div class="file-name">{{ file.name }}</div>
                <div class="file-meta">
                    <span>{{ formatSize(file.size) }}</span>
                    <span>{{ file.modifyTime }}</span>
                </div>
            </div>
        </div>

        <!-- 列表视图 -->
        <div class="file-list" v-else>
            <el-table :data="fileList" border stripe>
                <el-table-column label="图标" width="80">
                    <template #default="scope">
                        <el-icon :size="20">
                            <component :is="getFileIcon(getFileType(scope.row))"></component>
                        </el-icon>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="文件名" min-width="200"></el-table-column>
                <el-table-column prop="size" label="大小" width="100">
                    <template #default="scope">{{ formatSize(scope.row.size) }}</template>
                </el-table-column>
                <el-table-column prop="type" label="类型" width="100"></el-table-column>
                <el-table-column prop="modifyTime" label="修改时间" width="180"></el-table-column>
                <el-table-column label="操作" width="120">
                    <template #default="scope">
                        <el-button
                                type="text"
                                icon="View"
                                @click.stop="handlePreview(scope.row)"
                        ></el-button>
                        <el-button
                                type="text"
                                icon="Download"
                                @click.stop="handleDownload(scope.row)"
                        ></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

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

        <!-- 右键菜单 -->
        <div
                v-if="contextMenuFile"
                class="context-menu"
                :style="{ top: contextMenuTop + 'px', left: contextMenuLeft + 'px' }"
        >
            <div class="menu-item" @click="handleDownload(contextMenuFile)">下载</div>
            <div class="menu-item" @click="handleDelete(contextMenuFile)">删除</div>
            <div class="menu-item" @click="handleRename(contextMenuFile)">重命名</div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
    HardDrive,
    Folder, Grid, Refresh,
    FolderOpened, File, Image, Video, Music,
    Document
} from '@element-plus/icons-vue'
// import FileUploader from '@/components/file/FileUploader.vue'
import FilePreview from '@/components/file/FilePreview.vue'
import { formatFileSize } from '@/utils/format'
import { diskAPI } from '@/api/disk'

// 状态管理
const fileList = ref([])
const viewType = ref('grid') // 视图类型：grid/list
const previewVisible = ref(false)
const currentFile = ref(null)
const isFullscreen = ref(false)
const contextMenuFile = ref(null)
const contextMenuTop = ref(0)
const contextMenuLeft = ref(0)

// 获取文件列表
const fetchFileList = async (data) => {
    try {
        data = data || {}
        const res = await diskAPI.scan(data)
        fileList.value = res || []
    } catch (error) {
        console.error('获取文件列表失败', error)
    }
}

// 刷新文件列表
const refreshFileList = () => {
    fetchFileList()
}

const getFileType = (data) => {
    if (data['folder'] === 0 && data['diskFileInfo']) {
        const fileType = (data['diskFileInfo']['fileType'] || '').toLowerCase();
        if (containsArrType(["video", "mp4"], fileType)) {
            return 'video';
        } else if (containsArrType(['image', "jpg", "png", "jpeg"], fileType)) {
            return 'image';
        } else if (containsArrType(["mp3", "flv", "m3u8"], fileType)) {
            return 'audio';
        } else if (containsArrType(["pdf", "html", "txt", "sql", "log", "css", "js", "java", "py"], fileType)) {
            return 'document';
        }
    }
    return 'folder'
}

const containsArrType = (arr, str) => {
    for (let k in arr) {
        if (str.indexOf(arr[k])) {
            return true;
        }
    }
    return false;
}

// 获取文件图标
const getFileIcon = (type) => {
    switch (type) {
        case 'folder': return FolderOpened
        case 'image': return Image
        case 'video': return Video
        case 'audio': return Music
        case 'document': return Document
        default: return File
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

// 显示右键菜单
const showContextMenu = (file, event) => {
    contextMenuFile.value = file
    contextMenuTop.value = event.clientY
    contextMenuLeft.value = event.clientX
    // 点击空白处关闭菜单
    document.addEventListener('click', closeContextMenu)
}

// 关闭右键菜单
const closeContextMenu = () => {
    contextMenuFile.value = null
    document.removeEventListener('click', closeContextMenu)
}

// 处理删除
const handleDelete = (file) => {
    closeContextMenu()
    // 实际项目中调用删除接口
    console.log('删除文件', file.name)
}

// 处理重命名
const handleRename = (file) => {
    closeContextMenu()
    // 实际项目中调用重命名接口
    console.log('重命名文件', file.name)
}

onMounted(() => {
    fetchFileList()
})
</script>

<style scoped>
.disk-view {
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
    display: flex;
    align-items: center;
    gap: 10px;
}

.disk-stats {
    display: flex;
    align-items: center;
    gap: 15px;
}

.upload-area {
    margin-bottom: 20px;
}

.view-switch {
    margin-bottom: 15px;
}

.file-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.file-item {
    width: 160px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    padding: 15px;
    border-radius: 8px;
    background: #fff;
    box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.file-item:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.file-icon {
    margin-bottom: 10px;
}

.file-name {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-bottom: 5px;
}

.file-meta {
    font-size: 12px;
    color: #666;
    display: flex;
    flex-direction: column;
    gap: 3px;
}

.context-menu {
    position: fixed;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.15);
    z-index: 999;
    padding: 5px 0;
}

.menu-item {
    padding: 8px 20px;
    cursor: pointer;
    font-size: 14px;
}

.menu-item:hover {
    background: #f5f7fa;
}
</style>
