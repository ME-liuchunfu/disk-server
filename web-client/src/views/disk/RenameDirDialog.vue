<template>
  <div>
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

      <!-- 操作按钮 -->
      <el-form-item class="form-actions is-align-center">
        <el-button type="default" @click="handleCancel">取消</el-button>
        <el-button type="primary" native-type="submit">确认</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import {ref, reactive, defineProps, defineEmits, watch} from 'vue';
import { ElMessage } from 'element-plus';
import { diskAPI } from '@/api/disk'
import { useLoadingStore } from '@/stores/loading';

// 接收父组件传入的文件夹列表
const props = defineProps({
  folder: {
    type: Object,
    default: () => {},
    required: true
  }
});

// 定义事件
const emit = defineEmits(['submit', 'cancel']);

// 表单数据
const folderForm = reactive({
  id: 0,
  title: null
});

watch(
    () => props.folder,
    (newFolder) => {
      // 深拷贝避免直接修改 props
      Object.assign(folderForm, { ...newFolder })
    },
{ immediate: true } // 初始化时立即执行
)

// 表单规则
const formRules = {
  title: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { max: 50, message: '名称长度不能超过50个字符', trigger: 'blur' }
  ]
};

const loadingStore = useLoadingStore();

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
      title: folderForm.title.trim(),
      id: folderForm.id
    };
    let res = await diskAPI.update(updateItem)
    console.log(res)
    // 触发创建事件
    emit('submit', updateItem);

    // 重置表单
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
  emit('cancel');
};

// 重置表单
const resetForm = () => {
  folderFormRef.value?.resetFields();
};
</script>

<style scoped>

</style>
