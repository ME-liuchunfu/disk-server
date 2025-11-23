<template>
  <el-dialog
      v-model="show"
      title="spider"
      width="500"
      align-center
  >
    <el-form
        ref="formDataRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="folder-form"
        @submit.prevent="handleSubmit"
    >
      <el-form-item label="目录" prop="url" required>
        <el-input
            readonly
            v-model="formData.folderName"
            placeholder="请输入目录"
        />
      </el-form-item>
      <!-- 名称输入框 -->
      <el-form-item label="路径" prop="url" required>
        <el-input
            type="textarea"
            rows="10"
            v-model="formData.url"
            placeholder="请输入路径"
            clearable
        />
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
import {defineEmits, reactive, ref} from 'vue';
import {ElMessage} from 'element-plus';
import {diskAPI} from '@/api/disk'
import {useLoadingStore} from '@/stores/loading';
import eventBus from "@/utils/eventBus";


const show = ref(false)
const emit = defineEmits(['flush']);

eventBus.on('event:disk:spider', event => {
  console.log(event)
  if (event['type'] === 'spider') {
     const value = event['value'];
     show.value = true;
     formData.folderName = value['title']
     formData.folderId = value['id']
  }
})

// 表单数据
const formData = reactive({
  url: null,
  folderId: null,
  folderName: null
});

// 表单规则
const formRules = {
  url: [
    { required: true, message: '请输入url', trigger: 'blur' }
  ],
  folderName: [
    { required: true, message: '请输入目录', trigger: 'blur' }
  ]
};

const loadingStore = useLoadingStore();

// 表单引用
const formDataRef = ref(null);

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await formDataRef.value.validate();
    loadingStore.show();
    // 准备提交的数据（深拷贝避免修改源数据）
    const dataForm = {
      url: formData.url,
      folderId: formData.folderId
    };
    let regex = /http[s]?:\/\/[\w.-]+[\w\/-]*[\w.-]*\??[\w=&:\-\+\%]*[/]*/;
    dataForm['url'] = dataForm['url'].match(regex)[0];
    let res = await diskAPI.spider(dataForm)
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
  formDataRef.value?.resetFields();
};
</script>

<style scoped>

</style>
