<template>
    <div class="document-preview">
        <div class="docker-preview-close"><span class="btn-close" @click="handleClose"><el-icon><Close /></el-icon></span></div>
        <vue-office-pdf v-if="fileType === 'pdf'" :src="src" />
        <vue-office-docx v-else-if="fileType === 'docx'" :src="src" />
        <vue-office-docx v-else-if="fileType === 'xlsx'" :src="src" />
        <vue-office-docx v-else-if="fileType === 'sheet'" :src="src" />
        <div v-else class="unsupported">不支持的文件类型：{{ fileType }}</div>
    </div>
</template>

<script setup>
import {defineEmits, defineProps} from 'vue';
import VueOfficeDocx from 'vue-office/lib/docx'
import VueOfficePdf from 'vue-office/lib/pdf'


const emit = defineEmits(['close']);

// 接收文件 URL 或二进制流
defineProps({
    src: {
        type: String,
        required: true,
        default: ''
    },
    fileType: {
        type: String,
        required: true,
        default: 'unknown'
    }
});

const handleClose = () => {
    emit('close');
}

</script>

<style scoped>
.document-preview {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}
.docker-preview-close {
    height: 30px;
    display: flow;
    width: 100%;
    justify-content: flex-end;
    flex-direction: row;
    padding-top: 10px;
}
.btn-close {
    margin-left: auto;
    display: block;
    width: 20px;
    height: 20px;
    cursor: pointer;
}
.unsupported {
    padding: 20px;
    text-align: center;
    color: #a6a6a6;
    line-height: 100vh;
    flex: 1;
}
</style>
