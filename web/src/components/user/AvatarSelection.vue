<template>
    <el-dialog :model-value="visible" :modal="false" @close="handleOrgClose" style="max-width: 420px;">
        <div class="avatar-list-disp">
            <el-avatar :size="64" v-for="avatar in avatarList" class="selection-avatar" @click="handlePickup(avatar.id)" :id="avatar.id">
                <img :src="avatar.src"/>
            </el-avatar>
        </div>
        
        <el-button @click="handleSelection" :disabled="selectedAvatar===''">选择头像</el-button>
    </el-dialog>
</template>
<script setup>
import { toRefs, reactive, ref } from 'vue';

const props = defineProps({
    visible: Boolean
})

let { visible } = toRefs(props)

const avatarList = reactive([])

//require.context（检索目录、是否检索子文件、正则表达式匹配的）
const requireContext = require.context('@/assets/avatars/group1/', false, /^\.\/.*$/) 
requireContext.keys().forEach(key=>{
	let file = key.substring(2)
	avatarList.push({
        id: file.split('.')[0],
        src:'avatars/group1/'+file,
    })
})

const selectedAvatar = ref('')

const emit = defineEmits(['selection', 'close'])

const handleSelection = () => {
    emit('selection', selectedAvatar)
    emit('close')
}

const handleOrgClose = () => {
    emit('close')
}

function handlePickup(id) {
    console.log(id)
    if(selectedAvatar.value){
        $('#'+selectedAvatar.value).removeClass('activated')
    }
    selectedAvatar.value = id
    $('#'+id).addClass('activated')
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
}

.selection-avatar:hover {
    border: 2px solid #409EFF;
}

.activated {
    border: 2px solid #409EFF;
}
</style>