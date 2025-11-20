<!-- views/DiskView.vue -->
<template>
    <div class="disk-view">
        <!-- 顶部操作栏 -->
        <div class="disk-header">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item class="cursor" @click="refreshFileList">首页</el-breadcrumb-item>
                <el-breadcrumb-item class="cursor" @click="enterDirPath(index)" v-for="(nav, index) in dirPathQueue" :key="nav.id">{{nav.title}}</el-breadcrumb-item>
            </el-breadcrumb>
            <div class="disk-stats">
                <span>已加载: {{ fileList.length }} 个项目</span>
                <el-button type="primary" @click="refreshFileList">
                    <el-icon><Refresh /></el-icon> 刷新
                </el-button>
            </div>
        </div>
        <!-- 视图切换 -->
        <div class="view-switch">
            <el-space wrap :size="20">
                <el-button-group class="ml-4">
                    <el-button type="success" :icon="ArrowLeftBold" @click="backDirHandler"/>
                    <el-button type="primary" :icon="Refresh" @click="enterDirPath(null)" />
                </el-button-group>
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
                <el-button-group>
                    <el-button type="success" :icon="FolderAdd" @click="addFolderHandler"/>
                    <el-button type="primary" :icon="Edit" />
                    <el-button type="primary" :icon="Share" />
                    <el-button type="primary" :icon="Delete" />
                </el-button-group>
            </el-space>
        </div>

        <div v-if="fileList.length > 0" class="el-container dir-el-container">
            <!-- 网格视图（默认） -->
            <div class="file-grid" v-if="viewType === 'grid'">
                <div
                    v-for="file in fileList"
                    :key="file.id"
                    class="file-item"
                    @click="handlerClickItem(file)"
                    @contextmenu.prevent="showContextMenu(file, $event)"
                >
                    <div class="file-icon">
                        <el-icon :size="40">
                            <component :is="getFileIcon(getFileType(file))"></component>
                        </el-icon>
                    </div>
                    <el-tooltip :content="file.title" placement="right" effect="light">
                        <div class="file-name">
                            {{ file.title }}
                        </div>
                    </el-tooltip>
                    <div class="file-meta">
                        <span>{{ formatSize(file.size) }}</span>
                        <span>{{ file.createTime }}</span>
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
                    <el-table-column prop="title" label="文件名" min-width="200"></el-table-column>
                    <el-table-column prop="diskFileInfo" label="大小" width="100">
                        <template #default="scope">{{ formatSize(scope.row.diskFileInfo?.fileSize) }}</template>
                    </el-table-column>
                    <el-table-column prop="diskFileInfo" label="类型" width="100">
                        <template #default="scope">{{ scope.row.diskFileInfo?.fileType || scope.row.folder === 1 ? "目录": "文件"}}</template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="修改时间" width="180"></el-table-column>
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
        </div>
        <el-empty v-else description="目录为空" />

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

        <el-dialog
            v-model="dialogs.addDir.show"
            :title="dialogs.addDir.title"
            width="60%"
            >
            <AddFolder @create="createAddDir"
                       @cancel="dialogs.addDir.show = false"
                       :folders="dialogs.addDir.folders"/>
        </el-dialog>

        <!-- 重命名 -->
        <el-dialog  v-model="dialogs.updateDir.show"
                    :title="dialogs.updateDir.title"
                    width="500px"
        >
          <RenameDirDialog
              @submit="enterDirPath(null);dialogs.updateDir.show = false;"
              @cancel="dialogs.updateDir.show = false"
              :folder="dialogs.updateDir.folder"/>
        </el-dialog>

        <document-preview
            class="document-preview"
            :class="{'document-preview-show': dialogs.previewConfig.show}"
            @close="callHandleClosePreview"
            :file-type="dialogs.previewConfig.fileType"
            :src="dialogs.previewConfig.url"/>

        <!-- 右键菜单 -->
        <div
                v-if="contextMenuFile"
                class="context-menu"
                :style="{ top: contextMenuTop + 'px', left: contextMenuLeft + 'px' }"
        >
            <div class="menu-item" v-if="contextMenuFile.folder === 0" @click="handleDownload(contextMenuFile)">下载</div>
            <div class="menu-item" @click="handleDelete(contextMenuFile)">删除</div>
            <div class="menu-item" @click="handleRename(contextMenuFile)">重命名</div>
        </div>
    </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue'
import {
    Folder, Grid, Refresh,
    FolderOpened, File, Image, Video, Music,
    Document,
    Delete,
    Edit,
    Share,
    FolderAdd, ArrowLeftBold
} from '@element-plus/icons-vue'
import FilePreview from '@/components/file/FilePreview.vue'
import { formatFileSize } from '@/utils/format'
import { diskAPI } from '@/api/disk'
import {ElMessage, ElMessageBox} from "element-plus"
import { useLoadingStore } from '@/stores/loading';
import AddFolder from '@/views/disk/AddDiskDir.vue'
import RenameDirDialog from "@/views/disk/RenameDirDialog.vue";
import {downloads} from "@/utils/downloads";
import DocumentPreview from "@/components/file/DocumentPreview.vue";


// 状态管理
const fileList = ref([])
const viewType = ref('grid') // 视图类型：grid/list
const previewVisible = ref(false)
const currentFile = ref(null)
const isFullscreen = ref(false)
const contextMenuFile = ref(null)
const contextMenuTop = ref(0)
const contextMenuLeft = ref(0)
const dirPathQueue = ref([])

watch(
    () => dirPathQueue.value,
    () => {
        localStorage.setItem("cacheQue", dirPathQueue.value)
    },
    // { immediate: true } // 初始化时立即执行
)

onMounted(()=>{
    try {
        const cacheQue = localStorage.getItem("cacheQue") || '[]'
        dirPathQueue.value = JSON.parse(cacheQue)
    } catch (e) {
        console.log(e)
    }
})

const dialogs = ref({
    addDir:{
        show: false,
        title: "目录操作",
        folders: []
    },
    updateDir:{
      show: false,
      title: "重命名",
      folder: {}
    },
    previewConfig: {
        show: false,
        fileType: '',
        url: null
    }
})

const loadingStore = useLoadingStore()

// 获取文件列表
const fetchFileList = async (data) => {
    try {
        const flush = !data;
        data = data || {}
        const res = await diskAPI.scan(data)
        fileList.value = res || [];
        if (flush) {
            dirPathQueue.value = [];
        }
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
    if (!size) {
        return '';
    }
    return formatFileSize(size)
}

// 处理预览
const handlePreview = (file) => {
    currentFile.value = file
    previewVisible.value = true
    isFullscreen.value = false
    handleHttpPreview(file);
}

// 处理下载
const handleDownload = async (file) => {
    try {
        if (file['folder'] !== 0 || !file['diskFileInfo']) {
            ElMessage.warning(`不是 ${file.title} 文件，不支持下载`)
            return
        }
        loadingStore.show()
        const downUrl = await downloads.getDownTokeUrl(file['diskFileInfo']['path'])
        let name = file.title;
        if (name.indexOf(".") === -1) {
            name = name + file['diskFileInfo']['fileType']
        }
        downloads.down(downUrl, name)
    } catch (e) {
        ElMessage.error(`${file.title} 文件，下载失败`)
    }
    loadingStore.hide()
}

const callHandleClosePreview = () => {
    dialogs.value.previewConfig.show = false;
}

const handleHttpPreview = async (file) => {
    try {
        if (file['folder'] !== 0 || !file['diskFileInfo']) {
            ElMessage.warning(`不是 ${file.title} 文件，不支持预览`)
            return
        }
        loadingStore.show()
        const downUrl = await downloads.getDownTokeUrl(file['diskFileInfo']['path'])
        dialogs.value.previewConfig.url = downUrl;
        dialogs.value.previewConfig.fileType = file['diskFileInfo']['fileType'];
        if (downUrl) {
            dialogs.value.previewConfig.show = true;
        } else {
            ElMessage.error("文件预览错误")
        }
    } catch (e) {
        ElMessage.error(`${file.title} 文件，下载失败`)
    }
    loadingStore.hide()
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
    ElMessageBox.confirm(
        `确定要删除 ${file.title} 及其子目录文件 ?`,
        'Warning',
        {
          confirmButtonText: 'OK',
          cancelButtonText: 'Cancel',
          type: 'warning',
        }
    )
    .then(async () => {
      try {
        await diskAPI.del({ids: [file.id]})
        ElMessage.success("删除成功")
        enterDirPath(null);
      } catch (error) {
        ElMessage.error(error || "删除失败")
      }
    })
    .catch(()=>{

    })
}

// 处理重命名
const handleRename = (file) => {
  dialogs.value.updateDir.folder = file;
  closeContextMenu();
  dialogs.value.updateDir.show = true;
}

const handlerClickItem = (file) => {
    if (file['folder'] === 1) {
        ElMessage.success('进入目录:' + file.title);
        dirPathQueue.value.push({id:file.id, title: file.title});
        enterDirPath();
        return;
    }
    handlePreview(file);
}

const addFolderHandler = async () =>{
    // 查询库
    let len = dirPathQueue.value.length;
    let parentId = len > 1 ? dirPathQueue.value[len -2]['id'] : null;
    try {
        loadingStore.show();
        const res = await diskAPI.getDir(parentId);
        dialogs.value.addDir.folders = res || [];
        dialogs.value.addDir.show = true;
    } catch (err) {
        console.log(err)
    }
    loadingStore.hide()

}

// api 新增

const backDirHandler = () => {
    let len = dirPathQueue.value.length;
    let parentId = len <= 1 ? null :  dirPathQueue.value[len - 2]['id'];
    if (len > 0) {
        dirPathQueue.value.pop();
    }
    fetchFileList({
        parentId: parentId
    })
}

const enterDirPath = (index) => {
    let parentId = null;
    let len = dirPathQueue.value.length;
    if (!index) {
        parentId = len > 0 ? dirPathQueue.value[len -1]['id'] : null
    } else {
        if (len > index) {
            parentId = dirPathQueue.value[index]['id']
        }
    }

    fetchFileList({
        parentId: parentId
    })
}

const createAddDir = (item) => {
    console.log(item);
    enterDirPath();
}

onMounted(() => {
    fetchFileList()
})
</script>

<style scoped>

.disk-view {
    overflow: hidden;
    margin: 0 auto;
    padding: 20px;
    min-height: 0;
    display: flex;
    flex-direction: column;
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
.dir-el-container {
    height: calc(100vh - 170px);
    width: 100%;
}

.file-grid::-webkit-scrollbar {
    width: 6px;
}

.file-grid::-webkit-scrollbar-thumb {
    background-color: #abf5b9;
    border-radius: 3px;
}
.file-grid {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    overflow-y: auto;
    padding: 10px;
}

.file-item {
    width: 160px;
    max-height: 120px;
    height: 120px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    padding: 15px;
    border-radius: 8px;
    background: #fff;
    box-shadow: 0 0 6px 6px rgba(0,0,0,0.08);
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
.cursor{
    cursor: pointer;
}
.document-preview {
    display: none;
    position: fixed;
    z-index: 9999;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #ffffff;
    transition-duration: 1s;
}

.document-preview-show {
    display: flex;
}
</style>
