<template>
  <div class="xls-viewer-container">
    <!-- 工具栏 -->
    <div class="xls-toolbar" v-if="!isError && workbook">
      <div class="toolbar-left">
        <!-- 工作表切换 -->
        <select
            class="toolbar-select"
            v-model="activeSheetName"
            @change="switchSheet"
            aria-label="切换工作表"
        >
          <option v-for="name in sheetNames" :key="name">{{ name }}</option>
        </select>

        <!-- 分页控制 -->
        <div class="pagination" v-if="totalRows > pageSize">
          <button
              class="toolbar-btn"
              @click="prevPage"
              :disabled="currentPage <= 1"
              aria-label="上一页"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 18l-6-6 6-6"/>
            </svg>
          </button>
          <span class="page-info">
            第 {{ currentPage }} 页 / 共 {{ totalPages }} 页（{{ totalRows }} 条数据）
          </span>
          <button
              class="toolbar-btn"
              @click="nextPage"
              :disabled="currentPage >= totalPages"
              aria-label="下一页"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </button>
          <select
              class="toolbar-select page-size-select"
              v-model="pageSize"
              @change="resetPage"
              aria-label="每页条数"
          >
            <option value="10">10 条/页</option>
            <option value="20">20 条/页</option>
            <option value="50">50 条/页</option>
            <option value="100">100 条/页</option>
          </select>
        </div>
      </div>

      <div class="toolbar-right">
        <!-- 搜索框 -->
        <div class="search-box">
          <input
              type="text"
              v-model="searchKeyword"
              @input="handleSearch"
              placeholder="搜索表格内容..."
              aria-label="搜索表格"
          />
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="search-icon">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
        </div>

        <!-- 导出按钮 -->
        <button
            class="toolbar-btn export-btn"
            @click="exportToCsv"
            aria-label="导出CSV"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="export-icon">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="7 10 12 15 17 10"/>
            <line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
          导出 CSV
        </button>
        <button
            class="toolbar-btn export-btn"
            @click="exportToExcel"
            aria-label="导出Excel"
        >
          导出 Excel
        </button>
      </div>
    </div>

    <!-- 加载中 -->
    <div class="xls-loading" v-if="isLoading && !isError">
      <div class="loading-spinner"></div>
      <p class="loading-text">加载 Excel 中...</p>
    </div>

    <!-- 错误提示 -->
    <div class="xls-error" v-if="isError">
      <div class="error-icon">⚠️</div>
      <p class="error-message">{{ errorMessage || 'Excel 加载失败，请检查文件格式或地址' }}</p>
      <button class="retry-btn" @click="reloadXls">重试</button>
    </div>

    <!-- 空白状态 -->
    <div class="xls-empty" v-if="!isLoading && !isError && !workbook">
      <p class="empty-text">请传入有效的 Excel 文件或地址</p>
    </div>

    <!-- 表格内容（表头固定 + 滚动） -->
    <div
        class="xls-table-container"
        v-if="!isLoading && !isError && workbook && tableData.length"
    >
      <table class="xls-table">
        <!-- 表头 -->
        <thead>
        <tr>
          <th
              v-for="(col, index) in tableHeaders"
              :key="index"
              :style="{ width: `${getColumnWidth(index)}px` }"
              @mousedown="startResize(index, $event)"
              class="resizable-th"
          >
            {{ col || `列${index + 1}` }}
            <div class="resize-handle" :data-col-index="index"></div>
          </th>
        </tr>
        </thead>
        <!-- 表体 -->
        <tbody>
        <tr
            v-for="(row, rowIndex) in paginatedData"
            :key="rowIndex"
            :class="{ 'odd-row': rowIndex % 2 === 0 }"
        >
          <td
              v-for="(cell, colIndex) in row"
              :key="colIndex"
              :style="{ width: `${getColumnWidth(colIndex)}px` }"
              :class="{ 'search-highlight': isCellHighlighted(rowIndex + (currentPage - 1) * pageSize, colIndex) }"
          >
            {{ cell ?? '' }} <!-- 处理 null/undefined -->
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 无数据提示 -->
    <div class="xls-no-data" v-if="!isLoading && !isError && workbook && !tableData.length">
      <p class="no-data-text">当前工作表无数据</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, defineProps, defineEmits } from 'vue'
import * as XLSX from 'xlsx'
import eventBus from "@/utils/eventBus";

// Props 定义
const props = defineProps({
  initialSheet: { type: Number, default: 0 }, // 初始工作表索引（0开始）
  defaultColWidth: { type: Number, default: 120 }, // 默认列宽
  maxColWidth: { type: Number, default: 500 }, // 最大列宽
  minColWidth: { type: Number, default: 60 }, // 最小列宽
})

// 事件发射
const emit = defineEmits(['loaded', 'error', 'sheet-change', 'page-change', 'close'])

// 核心状态
const workbook = ref(null) // Excel 工作簿实例
const sheetNames = ref([]) // 所有工作表名称
const activeSheetName = ref('') // 当前激活的工作表名称
const tableHeaders = ref([]) // 表格表头
const tableData = ref([]) // 完整表格数据（过滤后）
const filteredData = ref([]) // 搜索过滤后的数据
const currentPage = ref(1) // 当前页码
const totalRows = ref(0) // 总数据条数
const totalPages = ref(0) // 总页数
const pageSize = ref(20)
const isLoading = ref(false) // 加载状态
const isError = ref(false) // 错误状态
const errorMessage = ref('') // 错误信息
const searchKeyword = ref('') // 搜索关键词
const highlightPositions = ref([]) // 搜索高亮位置（[{row, col}]）
const colWidths = ref([]) // 各列宽度（动态调整）
const isResizing = ref(false) // 是否正在调整列宽
const currentResizeCol = ref(-1) // 当前调整的列索引
const loadUrl = ref(null)

eventBus.on('media-event:prexls', event => {
  if (event['type'] === 'show') {
    loadUrl.value = event['value']['url']
    loadXls();
  }
})

// 计算分页后的数据
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// 初始化列宽
const initColWidths = (colCount) => {
  colWidths.value = Array(colCount).fill(props.defaultColWidth)
}

// 获取列宽
const getColumnWidth = (index) => {
  return colWidths.value[index] || props.defaultColWidth
}

// 切换工作表
const switchSheet = () => {
  const sheet = workbook.value.Sheets[activeSheetName.value]
  parseSheetData(sheet)
  emit('sheet-change', activeSheetName.value)
}

// 解析工作表数据
const parseSheetData = (sheet) => {
  // 转换为 JSON 格式（header: 1 表示第一行为表头）
  const jsonData = XLSX.utils.sheet_to_json(sheet, { header: 1 })
  if (jsonData.length === 0) {
    tableHeaders.value = []
    tableData.value = []
    filteredData.value = []
    totalRows.value = 0
    totalPages.value = 0
    initColWidths(0)
    return
  }

  // 提取表头和数据
  tableHeaders.value = jsonData[0] // 第一行为表头
  tableData.value = jsonData.slice(1) // 其余为数据行
  filteredData.value = [...tableData.value] // 初始过滤数据 = 完整数据

  // 更新分页信息
  totalRows.value = filteredData.value.length
  totalPages.value = Math.ceil(totalRows.value / pageSize.value)
  currentPage.value = 1 // 切换工作表后重置页码

  // 初始化列宽（根据表头数量）
  initColWidths(tableHeaders.value.length)
}

// 加载 Excel 文件
const loadXls = async () => {
  isLoading.value = true
  isError.value = false
  errorMessage.value = ''
  workbook.value = null
  sheetNames.value = []
  activeSheetName.value = ''

  try {
    let data = null
    if (loadUrl.value) {
      const response = await fetch(loadUrl.value, {
        method: 'GET',
        responseType: 'arraybuffer'
      })
      if (!response.ok) throw new Error(`HTTP 错误：${response.status}`)
      data = await response.arrayBuffer()
    } else {
      isLoading.value = false
      return
    }

    // 解析 Excel 工作簿
    workbook.value = XLSX.read(data, { type: 'array' })
    sheetNames.value = workbook.value.SheetNames

    // 激活初始工作表
    const initialSheetName = sheetNames.value[props.initialSheet] || sheetNames.value[0]
    activeSheetName.value = initialSheetName
    const activeSheet = workbook.value.Sheets[initialSheetName]

    // 解析工作表数据
    parseSheetData(activeSheet)

    emit('loaded', {
      workbook: workbook.value,
      sheetNames: sheetNames.value,
      activeSheet: initialSheetName
    })
  } catch (err) {
    console.error('Excel 加载失败:', err)
    isError.value = true
    errorMessage.value = err.message || '未知错误'
    emit('error', err)
  } finally {
    isLoading.value = false
  }
}

// 分页控制
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    emit('page-change', currentPage.value)
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    emit('page-change', currentPage.value)
  }
}

const resetPage = () => {
  currentPage.value = 1
  totalPages.value = Math.ceil(filteredData.value.length / pageSize.value)
  emit('page-change', currentPage.value)
}

// 搜索功能
const handleSearch = () => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  highlightPositions.value = []

  if (!keyword) {
    filteredData.value = [...tableData.value] // 清空搜索词，恢复原始数据
    totalRows.value = tableData.value.length
    totalPages.value = Math.ceil(totalRows.value / pageSize.value)
    currentPage.value = 1
    return
  }

  // 过滤包含关键词的数据，并记录高亮位置
  const filtered = []
  tableData.value.forEach((row, rowIndex) => {
    let rowHasKeyword = false
    const rowHighlightCols = []

    row.forEach((cell, colIndex) => {
      const cellValue = String(cell || '').toLowerCase()
      if (cellValue.includes(keyword)) {
        rowHasKeyword = true
        rowHighlightCols.push(colIndex)
      }
    })

    if (rowHasKeyword) {
      filtered.push(row)
      // 记录高亮位置（原始数据的行索引）
      rowHighlightCols.forEach(colIndex => {
        highlightPositions.value.push({ row: rowIndex, col: colIndex })
      })
    }
  })

  filteredData.value = filtered
  totalRows.value = filtered.length
  totalPages.value = Math.ceil(totalRows.value / pageSize.value)
  currentPage.value = 1
}

// 判断单元格是否需要高亮
const isCellHighlighted = (rowIndex, colIndex) => {
  return highlightPositions.value.some(pos => pos.row === rowIndex && pos.col === colIndex)
}

// 列宽调整功能
const startResize = (colIndex, e) => {
  isResizing.value = true
  currentResizeCol.value = colIndex
  const startX = e.clientX
  const originalWidth = getColumnWidth(colIndex)

  // 鼠标移动事件（调整列宽）
  const handleMouseMove = (moveEvent) => {
    if (!isResizing.value) return
    const diffX = moveEvent.clientX - startX
    let newWidth = originalWidth + diffX
    // 限制最小/最大宽度
    newWidth = Math.max(props.minColWidth, Math.min(props.maxColWidth, newWidth))
    colWidths.value[colIndex] = newWidth
  }

  // 鼠标松开事件（结束调整）
  const handleMouseUp = () => {
    isResizing.value = false
    currentResizeCol.value = -1
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }

  // 绑定全局事件
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 导出为 CSV
const exportToCsv = () => {
  if (!workbook.value) return
  const sheet = workbook.value.Sheets[activeSheetName.value]
  const csv = XLSX.utils.sheet_to_csv(sheet)
  downloadFile(csv, `${activeSheetName.value}.csv`, 'text/csv')
}

// 导出为 Excel
const exportToExcel = () => {
  if (!workbook.value) return
  const wbout = XLSX.write(workbook.value, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([wbout], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  downloadFile(blob, `${activeSheetName.value}.xlsx`, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
}

// 下载文件工具函数
const downloadFile = (data, filename, mimeType) => {
  const url = URL.createObjectURL(new Blob([data], { type: mimeType }))
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url) // 释放资源
}

// 重新加载 Excel
const reloadXls = () => {
  loadXls()
}

// 监听搜索关键词变化
watch(searchKeyword, handleSearch, { immediate: true })

</script>

<style scoped>
.xls-viewer-container {
  width: 100%;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

/* 工具栏 */
.xls-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.toolbar-select {
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  background-color: white;
  cursor: pointer;
}

.toolbar-btn {
  background: transparent;
  border: none;
  color: #333;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
  font-size: 14px;
}

.toolbar-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.toolbar-btn:hover:not(:disabled) {
  background-color: #f0f0f0;
}

.export-btn {
  background-color: #42b983;
  color: white;
  margin-left: 8px;
}

.export-btn:hover {
  background-color: #359469;
}

.export-icon {
  margin-right: 4px;
}

/* 分页样式 */
.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-info {
  min-width: 120px;
  text-align: center;
}

.page-size-select {
  width: 100px;
}

/* 搜索框 */
.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box input {
  padding: 6px 12px 6px 32px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  width: 220px;
  transition: border-color 0.2s ease;
}

.search-box input:focus {
  outline: none;
  border-color: #42b983;
}

.search-icon {
  position: absolute;
  left: 8px;
  color: #999;
}

/* 加载中 */
.xls-loading {
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
.xls-error {
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
.xls-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
  font-size: 14px;
}

/* 无数据提示 */
.xls-no-data {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #999;
  font-size: 14px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 表格容器（表头固定 + 滚动） */
.xls-table-container {
  width: 100%;
  overflow-x: auto;
  overflow-y: auto;
  max-height: calc(100vh - 220px);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 表格样式 */
.xls-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.xls-table thead {
  position: sticky;
  top: 0;
  background-color: #f8f9fa;
  z-index: 10;
}

.xls-table th,
.xls-table td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
  white-space: nowrap; /* 禁止换行 */
  overflow: hidden;
  text-overflow: ellipsis;
}

.xls-table th {
  color: #333;
  font-weight: 500;
  border-right: 1px solid #e0e0e0;
}

.xls-table th:last-child {
  border-right: none;
}

.xls-table td {
  color: #666;
  border-right: 1px solid #f0f0f0;
}

.xls-table td:last-child {
  border-right: none;
}

.odd-row {
  background-color: #fafafa;
}

.xls-table tbody tr:hover {
  background-color: #f5fafe;
  cursor: default;
}

/* 搜索高亮 */
.search-highlight {
  background-color: #fff3cd;
  color: #856404;
  font-weight: 500;
}

/* 列宽调整 */
.resizable-th {
  position: relative;
  user-select: none;
}

.resize-handle {
  position: absolute;
  top: 0;
  right: 0;
  width: 6px;
  height: 100%;
  cursor: col-resize;
  background-color: transparent;
  z-index: 100;
}

.resize-handle:hover {
  background-color: rgba(66, 185, 131, 0.3);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>