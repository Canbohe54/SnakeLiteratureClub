<template>
    <el-avatar
        :size="size"
        :shape="shape"
        :icon="icon"
        :alt="alt"
        :fit="fit"
        :lazy="lazy"
        :error="error"
        :loading="loading"
        :draggable="draggable"
        :onPreview="onPreview"
        :onClick="onClick"
        :onError="onError"
        :onLoading="onLoading"
        :onDrag="onDrag"
        :onDragEnd="onDragEnd"
        :onDragEnter="onDragEnter"
        :onDragLeave="onDragLeave"
        :onDragOver="onDragOver"
        :onDragStart="onDragStart"
        :onDrop="onDrop"
        :id="'avatar_'+avatar.avatar+'_'+avatar.color.split('#')[1]"
    >
        <img :id="'avatar-img_'+avatar.avatar+'_'+avatar.color.split('#')[1]" />
    </el-avatar>
</template>

<script setup>
import { ref, reactive, onMounted, toRefs, defineProps } from 'vue';

const props = defineProps({
    pictureUrl: String,
    size: {
        type: Number,
        default: 64,
    },
    shape: {
        type: String,
        default: 'circle',
    },
    icon: {
        type: String,
        default: '',
    },
    alt: {
        type: String,
        default: 'avatar',
    },
    fit: {
        type: String,
        default: 'cover',
    },
    lazy: {
        type: Boolean,
        default: false,
    },
    error: {
        type: String,
        default: '',
    },
    loading: {
        type: String,
        default: '',
    },
    draggable: {
        type: Boolean,
        default: false,
    },
    previewSrcList: {
        type: Array,
        default: () => [],
    },
    previewSrcIndex: {
        type: Number,
        default: 0,
    },
    onPreview: {
        type: Function,
        default: () => {},
    },
    onClick: {
        type: Function,
        default: () => {},
    },
    onError: {
        type: Function,
        default: () => {},
    },
    onLoading: {
        type: Function,
        default: () => {},
    },
    onDrag: {
        type: Function,
        default: () => {},
    },
    onDragEnd: {
        type: Function,
        default: () => {},
    },
    onDragEnter: {
        type: Function,
        default: () => {},
    },
    onDragLeave: {
        type: Function,
        default: () => {},
    },
    onDragOver: {
        type: Function,
        default: () => {},
    },
    onDragStart: {
        type: Function,
        default: () => {},
    },
    onDrop: {
        type: Function,
        default: () => {},
    },
})

const { pictureUrl } = toRefs(props)

const avatar = reactive(pictureUrl.value? JSON.parse(pictureUrl.value) : null)

function init(){
    if (avatar){
        $("#avatar_"+avatar.avatar+"_"+avatar.color.split('#')[1]).css("background-color", `${avatar.color}`)
        $("#avatar-img_"+avatar.avatar+"_"+avatar.color.split('#')[1]).attr("src", 'avatars/'+`${avatar.avatar}`+'.png')
    }
    console.log(avatar)
}
onMounted(() => {
    init()
})
init()
</script>
