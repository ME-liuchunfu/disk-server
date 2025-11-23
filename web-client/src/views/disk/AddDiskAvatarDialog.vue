<template>
  <el-dialog
      v-model="show"
      title="更改封面"
      width="500"
      align-center
  >
    <el-form
        ref="folderFormRef"
        :model="folderForm"
        :rules="formRules"
        label-width="100px"
        class="folder-form"
        @submit.prevent="handleSubmit"
    >
      <!-- 名称输入框 -->
      <el-form-item label="名称" prop="title" required>
        <el-input
            v-model="folderForm.title"
            placeholder="请输入名称"
            clearable
            maxlength="50"
        />
      </el-form-item>

      <el-form-item label="封面" prop="fileId" required>
        <el-input
            v-model="folderForm.fileId"
            placeholder="资源id"
            readonly
        />
      </el-form-item>

      <el-form-item>
        <image-upload class="upload-area" @success="handlerUploadSuccess" />
      </el-form-item>
      <!-- 操作按钮 -->
      <el-form-item class="form-actions is-align-center">
        <el-button type="default" @click="handleCancel">取消</el-button>
        <el-button type="primary" native-type="submit">确认</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup>
import {ref, defineEmits, reactive} from 'vue';
import { ElMessage } from 'element-plus';
import { diskAPI } from '@/api/disk'
import { useLoadingStore } from '@/stores/loading';
import eventBus from "@/utils/eventBus";
import ImageUpload from "@/components/file/ImageUpload.vue";


const show = ref(false)
const emit = defineEmits(['flush']);

eventBus.on('event:disk:add-avatar', event => {
  console.log(event)
  const value = event['value'];
  if (event['type'] === 'avatar') {
    folderForm.id = value.id;
    folderForm.title = value.title
    show.value = true;
    eventBus.emit('event:disk:add-avatar-url', value['avatarPath'])
  }
})

// 表单数据
const folderForm = reactive({
  id: 0,
  title: null,
  fileId: null,
});

// 表单规则
const formRules = {
  title: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { max: 50, message: '名称长度不能超过50个字符', trigger: 'blur' }
  ],
  fileId: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ]
};

const loadingStore = useLoadingStore();

const handlerUploadSuccess = (res) => {
  console.log("upload", res);
  if (res && res['refId']) {
    folderForm.fileId = res['refId'];
  }
}

// 表单引用
const folderFormRef = ref(null);

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await folderFormRef.value.validate();
    loadingStore.show();
    // 准备提交的数据（深拷贝避免修改源数据）
    const updateItem = {
      fileId: folderForm.fileId,
      id: folderForm.id
    };
    let res = await diskAPI.updateAvatar(updateItem)
    console.log(res)
    show.value = false;
    emit('flush')
    // 触发创建事件
    resetForm();
    // 显示成功消息
    ElMessage.success('创建成功');
  } catch (error) {
    // 验证失败不做处理，Element Plus会自动显示错误提示
    console.log(error)
  }
  loadingStore.hide();
};

// 取消操作
const handleCancel = () => {
  resetForm();
  show.value = false;
};

// 重置表单
const resetForm = () => {
  folderFormRef.value?.resetFields();
};
</script>

<style scoped>

</style>
