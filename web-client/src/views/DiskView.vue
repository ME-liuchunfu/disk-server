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
                    <img class="file-grid-img" v-if="file['avatarUrl']" :src="file['avatarUrl']">
                    <div class="file-grid-content">
                      <div class="file-icon" v-if="!file['avatarUrl']">
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
                width="100%"
                height="100vh"
                append-to-body
                :fullscreen="isFullscreen"
        >
            <file-preview
                    :file-type="previewFile.fileType"
                    :url="previewFile.url"
                    @toggle-fullscreen="isFullscreen = !isFullscreen"
            ></file-preview>
        </el-dialog>

        <!-- 新增目录 -->
        <add-folder @flush="enterDirPath(null)" />
        <!-- 重命名 -->
        <rename-dir-dialog @flush="enterDirPath(null)"/>
        <!-- 新增封面 -->
        <add-disk-avatar-dialog @flush="enterDirPath(null)"/>
        <!-- 右键菜单 -->
        <div
                v-if="contextMenuFile"
                class="context-menu"
                :style="{ top: contextMenuTop + 'px', left: contextMenuLeft + 'px' }"
        >
            <div class="menu-item" v-if="contextMenuFile.folder === 0" @click="handleDownload(contextMenuFile)">下载</div>
            <div class="menu-item" @click="handleDelete(contextMenuFile)">删除</div>
            <div class="menu-item" @click="handleRename(contextMenuFile)">重命名</div>
            <div class="menu-item" @click="handleAddAvatar(contextMenuFile)">更新封面</div>
        </div>
    </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
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
import eventBus from '@/utils/eventBus'
import AddDiskAvatarDialog from "@/views/disk/AddDiskAvatarDialog.vue";

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

const previewFile = ref({
  'url': '',
  'fileType': ''
})

onMounted(()=>{
    try {
      const jsonPathdir = localStorage.getItem("cacheDirQue") || '[]'
      dirPathQueue.value = JSON.parse(jsonPathdir)
    } catch (e) {
      console.log(e)
    }
    enterDirPath();
})

const loadingStore = useLoadingStore()

// 获取文件列表
const fetchFileList = async (data) => {
    try {
        const flush = !data;
        data = data || {}
        const res = await diskAPI.scan(data)
        if (res) {
          const url = await downloads.tokens()
          for (let k in res) {
            if (res[k]['avatarPath']) {
              res[k]['avatarUrl'] = downloads.joinUrl(url, res[k]['avatarPath'])
            }
          }
        }
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
        case 'mp4': return Video
        case 'audio': return Music
        case 'mp3': return Music
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
const handlePreview = async (file) => {
    if (!file['diskFileInfo']) {
      ElMessage.error('文件错误');
      return
    }
    const downUrl = await downloads.getDownTokeUrl(file['diskFileInfo']['path'])
    let coverUrl = null;
    if (file['avatarUrl']) {
      coverUrl = file['avatarUrl'];
    }

    // const downUrl = "http://localhost/1.mp3";
    // const cover = "http://localhost/cover.png";
    if (file['diskFileInfo']['fileType'] === 'mp3') {
        eventBus.emit('media-event', {
            type: 'audio',
            value: {
                title:file['title'],
                url: downUrl,
                cover: coverUrl
            }
        });
        return
    }
  if (file['diskFileInfo']['fileType'] === 'mp4') {
    eventBus.emit('media-event', {
      type: 'video',
      value: {
        title:file['title'],
        url: downUrl,
        cover: coverUrl
      }
    });
    return
  }
    previewFile.value.url = downUrl;
    previewFile.value.fileType = file['diskFileInfo']['fileType'];
    currentFile.value = file;

    previewVisible.value = true
    isFullscreen.value = false
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
  closeContextMenu();
  eventBus.emit('event:disk:rename-dir', {
    type: 'rename',
    value: {
      id: file.id,
      title: file.title,
      folder: file.folder
    }
  })
}

// 更新封面
const handleAddAvatar = (file) => {
  eventBus.emit('event:disk:add-avatar', {
    type: 'avatar',
    value: {...file}
  })
}

const cacheDirHandle = () => {
  try{
   localStorage.setItem("cacheDirQue", JSON.stringify([...dirPathQueue.value]));
  } catch (e) {
    console.log(e)
  }
}

const handlerClickItem = (file) => {
    if (file['folder'] === 1) {
        ElMessage.success('进入目录:' + file.title);
        dirPathQueue.value.push({id:file.id, title: file.title});
        cacheDirHandle()
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
        const folders = res || [];
        let selectIndex = -1;
        if (len > 0) {
          const id = dirPathQueue.value[len -1]['id'];
          let ids = folders.filter(v=>v['id'] === id)
          if (ids.length > 0) {
            selectIndex = folders.indexOf(ids[0])
          }
        }
        eventBus.emit('event:disk:add-folder', {
          type: 'addfolder',
          value: {
            folders: folders,
            selectIndex: selectIndex
          }
        })
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
    cacheDirHandle()
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
    align-content: flex-start;
}
.file-grid-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  object-fit: cover;
  object-position: center; /* 图片居中显示 */
  opacity: 0.7; /* 可选：调整背景图透明度 */
  z-index: 0; /* 底层：确保兄弟元素在上方 */
}
.file-grid-content {
  position: relative; /* 相对定位，提升层级 */
  z-index: 1; /* 高于背景图 z-index:0 */
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: end;
  flex-wrap: nowrap;
  align-content: flex-start;
  flex-direction: column;
}
.file-item {
  position: relative;
  overflow: hidden;
  cursor: pointer;
    width: 160px;
    height: 180px;
    text-align: center;
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
  width: 100%;
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
