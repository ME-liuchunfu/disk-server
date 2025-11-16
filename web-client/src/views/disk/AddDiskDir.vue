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
            <el-form-item label="名称" prop="name" required>
                <el-input
                    v-model="folderForm.name"
                    placeholder="请输入名称"
                    clearable
                    maxlength="50"
                />
            </el-form-item>

            <!-- 类型选择 -->
            <el-form-item label="类型" required>
                <el-radio-group v-model="folderForm.type">
                    <el-radio :label="1" border>文件夹</el-radio>
                    <el-radio :label="0" border>文件</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="站内/外" required>
                <el-radio-group v-model="folderForm.from">
                    <el-radio :label="1" border>站内</el-radio>
                    <el-radio v-if="folderForm.type === 0" :label="0" border>站外</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="关联文件">
                <el-input
                    v-if="folderForm.from === 0"
                    v-model="folderForm.outer"
                    placeholder="请输入url"
                    clearable
                    maxlength="50"
                >
                    <template #append>
                        <el-button :icon="Search" @click="handlerOuter" />
                    </template>
                </el-input>
                <any-upload v-else class="upload-area" @success="handlerUploadSuccess" />

            </el-form-item>
            <el-form-item v-show="outerData.show">
                <el-checkbox v-for="(item, index) in outerData.data"
                             :key="index"
                             v-model="item.ck"
                             :label="item.originalFilename"
                             size="large"
                />
            </el-form-item>

            <!-- 父级文件夹选择（带搜索） -->
            <el-form-item label="父级文件夹">
                <el-select
                    v-model="folderForm.parentId"
                    placeholder="请选择父级文件夹"
                    clearable
                    filterable
                    default-first-option
                    :filter-method="filterParentFolders"
                >
                    <el-option :value="null" label="-- 无（根目录） --" />
                    <el-option
                        v-for="folder in filteredParentFolders"
                        :key="folder.id"
                        :label="folder.name"
                        :value="folder.id"
                    />
                </el-select>
            </el-form-item>

            <!-- 操作按钮 -->
            <el-form-item class="form-actions">
                <el-button type="default" @click="handleCancel">取消</el-button>
                <el-button type="primary" native-type="submit">确认创建</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive, defineProps, defineEmits } from 'vue';
import { ElMessage } from 'element-plus';
import { diskAPI } from '@/api/disk'
import AnyUpload from '@/components/file/AnyUpload.vue'
import {
    Search
} from '@element-plus/icons-vue'
import { useLoadingStore } from '@/stores/loading';

// 接收父组件传入的文件夹列表
const props = defineProps({
    folders: {
        type: Array,
        default: () => [],
        validator: (value) => {
            return value.every(item => 'id' in item && 'name' in item);
        }
    }
});

// 定义事件
const emit = defineEmits(['create', 'cancel']);

// 表单数据
const folderForm = reactive({
    name: null,
    type: 1, // 1:文件夹，0:文件
    parentId: null,
    refFileId: null,
    from: 1,
    outer: null
});

const outerData = ref({
    show: false,
    data: []
})

// 表单规则
const formRules = {
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { max: 50, message: '名称长度不能超过50个字符', trigger: 'blur' }
    ]
};

const loadingStore = useLoadingStore();

const handlerOuter = async () => {
  if (!folderForm.outer) {
      ElMessage.warning("缺失站外地址");
      return;
  }
  try {
      loadingStore.show();
      const resp = await diskAPI.outerDown([folderForm.outer]);
      console.log(resp);
      if (resp) {
          for (let respKey in resp) {
              resp[respKey]['ck'] = 0;
          }
      }
      outerData.value.data = resp || [];
      if (outerData.value.data.length > 0) {
          folderForm.name = outerData.value.data[0]['originalFilename']
          outerData.value.show = true;
      }
  } catch (e) {
      console.log(e)
  }
    loadingStore.hide();
}

// 文件夹过滤相关
const filteredParentFolders = ref([...props.folders]);

// 过滤父级文件夹的方法
const filterParentFolders = (query) => {
    if (!query) {
        filteredParentFolders.value = [...props.folders];
        return;
    }

    const keyword = query.toLowerCase();
    filteredParentFolders.value = props.folders.filter(folder =>
        folder.name.toLowerCase().includes(keyword)
    );
};

const handlerUploadSuccess = (res) => {
  console.log("upload", res);
  if (res && res['refId']) {
      folderForm.refFileId = res['refId'];
      folderForm.name = res['originalFilename'];
  }
}

// 表单引用
const folderFormRef = ref(null);

// 提交表单
const handleSubmit = async () => {
    try {
        // 表单验证
        await folderFormRef.value.validate();

        // 准备提交的数据（深拷贝避免修改源数据）
        const newItem = {
            name: folderForm.name.trim(),
            type: folderForm.type,
            from: folderForm.from,
            parentId: folderForm.parentId || null,
            refFileId: folderForm.refFileId
        };

        let outer = null;
        if (newItem.from === 0) {
            for (let i = 0; i < outerData.value.data.length; i++) {
                let up = outerData.value.data[i];
                if (up['ck']) {
                    let res = await diskAPI.add({
                        parentId: newItem.parentId,
                        title: up['originalFilename'],
                        folder: 0,
                        outer: outer,
                        from: newItem.from,
                        refFileId: up['refId']
                    })
                    console.log(res)
                }
            }
        } else {
            let res = await diskAPI.add({
                parentId: newItem.parentId,
                title: newItem.name,
                folder: newItem.type,
                outer: outer,
                from: newItem.from,
                refFileId: newItem.refFileId
            })
            console.log(res)
        }
        // 触发创建事件
        emit('create', newItem);

        // 重置表单
        resetForm();

        // 显示成功消息
        ElMessage.success('创建成功');
    } catch (error) {
        // 验证失败不做处理，Element Plus会自动显示错误提示
        return;
    }
};

// 取消操作
const handleCancel = () => {
    resetForm();
    emit('cancel');
};

// 重置表单
const resetForm = () => {
    folderFormRef.value?.resetFields();
    folderForm.type = '1'; // 重置为默认文件夹类型
};
</script>

<style scoped>

.folder-form {
    margin-top: 15px;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}

:deep(.el-radio-group) {
    display: flex;
    gap: 20px;
}
.el-radio.is-bordered {
    border: none;
}
</style>
