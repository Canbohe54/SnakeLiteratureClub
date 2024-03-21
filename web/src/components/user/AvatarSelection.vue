<template>
    <el-dialog :model-value="visible" :modal="false" @close="handleOrgClose" style="max-width: 420px;">
        <div class="avatar-list-disp">
            <el-avatar :size="64" v-for="avatar in avatarList" class="selection-avatar" @click="handlePickup(avatar.id)" :id="'avatar'+avatar.id">
                <img :src="avatar.src"/>
            </el-avatar>
        </div>
        <div class="color-collapse">
                    背景颜色：
                    <el-avatar :size="20" v-for="color in colorList" :style="{backgroundColor: color.color}" :id="'color'+color.id" class="selection-color" @click="handleSimpleColorPickup(color)"></el-avatar>
                    <el-avatar :size="20" id="custom-color" class="selection-color"><label><input type="color" @click="handleCustomColorPickup" @input="handleCustomChange($event)"/></label></el-avatar><el-icon :size="15" class="more-color"><MoreFilled /></el-icon>
        </div>
        <el-button @click="handleSelection" :disabled="selection.avatar===''">确认头像</el-button>
    </el-dialog>
</template>
<script setup>
import { toRefs, reactive, ref } from 'vue';
import { MoreFilled } from '@element-plus/icons-vue';

const props = defineProps({
    visible: Boolean
})

let { visible } = toRefs(props)

const avatarList = reactive([])

//require.context（检索目录、是否检索子文件、正则表达式匹配的）
const requireContext = require.context('@/assets/avatars/', false, /^\.\/.*$/)
requireContext.keys().forEach(key=>{
	let file = key.substring(2)
	avatarList.push({
        id: file.split('.')[0],
        src:'avatars/'+file,
    })
})

const colorList = reactive([
    {id: 1, color: '#e9f3e2'},
    {id: 2, color: '#ffb8b8'},
    {id: 3, color: '#fdce8b'},
    {id: 4, color: '#b6fbc2'},
    {id: 5, color: '#90d4fe'},
    {id: 6, color: '#b4bcfe'}
])

const custom_color = ref('#000')

const selection = reactive({
    avatar: '',
    color: ''
})

const emit = defineEmits(['selection', 'close'])

const handleSelection = () => {
    emit('selection', selection)
    emit('close')
}

const handleOrgClose = () => {
    emit('close')
}

function handlePickup(id) {
    if(selection.avatar){
        $('#avatar'+selection.avatar).removeClass('activated')
    }
    if(selection.avatar === id){
        selection.avatar = ''
        return
    }
    selection.avatar = id
    $('#avatar'+id).addClass('activated')
}

function handleSimpleColorPickup(color) {
    let exist = colorList.find(c=>c.color===selection.color)
    if(selection.color&&exist){
        $('#color'+exist.id).removeClass('activated')
    }
    $(".selection-color").removeClass('activated')
    selection.color = color.color
    handlePreviewBgColor()
    $('#color'+color.id).addClass('activated')
}

function handleCustomColorPickup() {
    let exist = colorList.find(c=>c.color===selection.color)
    if(selection.color&&exist){
        $('#color'+exist.id).removeClass('activated')
    }
    $("#custom-color").addClass('activated')
    handlePreviewBgColor()
}

function handleCustomChange(event) {
    selection.color = event.target.value
    handlePreviewBgColor()
}

function handlePreviewBgColor(){
    $(".selection-avatar").css("background-color", selection.color);
}

</script>
<style scoped>

.avatar-list-disp {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    align-items: center;
    margin: 10px 0;
}

.selection-avatar {
    cursor: pointer;
    margin: 5px 0;
    background-color: #e9f3e2;
}

.color-collapse {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 10px 0;
}

.selection-color {
    cursor: pointer;
    margin: 0 2px;
}

.more-color {
    position: relative;
    transform: translateX(-1.2rem);
    color: #fff;
    pointer-events: none;
}

input[type="color"]::-webkit-color-swatch-wrapper {
    padding: 0;
    cursor: pointer;
}

.selection-avatar:hover {
    border: 2px solid #409EFF;
}

.activated {
    border: 2px solid #409EFF;
}
</style>
