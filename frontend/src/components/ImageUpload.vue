<template>
  <div class="image-upload">
    <el-upload
      class="upload-demo"
      :http-request="customUpload"
      :on-remove="handleRemove"
      :before-upload="beforeUpload"
      :file-list="fileList"
      :limit="limit"
      list-type="picture-card"
      :disabled="disabled"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
    <div v-if="tip" class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ImageUpload',
  components: {
    Plus
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    limit: {
      type: Number,
      default: 1
    },
    disabled: {
      type: Boolean,
      default: false
    },
    tip: {
      type: String,
      default: '只能上传jpg/png文件，且不超过10MB'
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const fileList = ref([])

    // 监听modelValue变化
    watch(() => props.modelValue, (newVal) => {
      if (newVal) {
        if (fileList.value.length === 0 || fileList.value[0]?.url !== newVal) {
          fileList.value = [{
            name: 'image',
            url: newVal
          }]
        }
      } else {
        fileList.value = []
      }
    }, { immediate: true })

    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt10M) {
        ElMessage.error('图片大小不能超过10MB!')
        return false
      }
      return true
    }

    const customUpload = async (options) => {
      const formData = new FormData()
      formData.append('file', options.file)

      try {
        const response = await api.post('/file/upload/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (response.data.code === 200) {
          const imageUrl = response.data.data.url
          emit('update:modelValue', imageUrl)
          fileList.value = [{
            name: options.file.name,
            url: imageUrl
          }]
          ElMessage.success('图片上传成功')
        } else {
          ElMessage.error(response.data.message || '上传失败')
        }
      } catch (error) {
        ElMessage.error('图片上传失败: ' + (error.response?.data?.message || error.message || '未知错误'))
      }
    }

    const handleRemove = () => {
      emit('update:modelValue', '')
      fileList.value = []
    }

    return {
      fileList,
      beforeUpload,
      customUpload,
      handleRemove
    }
  }
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  line-height: 146px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 148px;
  height: 148px;
}
</style>
