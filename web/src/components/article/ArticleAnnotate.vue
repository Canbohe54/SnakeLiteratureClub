<template>
    <div>
        <div class="choosed-box">
            <div class="choose-line">
                <h3 style="margin: 0;line-height: 40px;margin-right: 10px;font-weight: 500;">线条形状:</h3>
                <el-select v-model="choosedLineValue" placeholder="请选择形状" style="width: 60%;" @change="switchDrawingMode">
                    <el-option
                            v-for="item in options"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                    />
                </el-select>
            </div>
            <div class="choose-color">
                <h3 style="margin: 0;line-height: 40px;margin-right: 10px;font-weight: 500;">线条颜色:</h3>
                <el-color-picker v-model="lineColor" />
            </div>
            <div class="choose-color">
                <h3 style="margin: 0;line-height: 40px;margin-right: 10px;font-weight: 500;">文本批注:</h3>
                <el-tooltip
                        class="flex-center"
                        style="font-size: 18px; margin-left: 10px; display: flex !important;"
                        effect="dark"
                        content="双击进行文字批注"
                        placement="right"
                >
                    <i class="el-icon-question" />
                </el-tooltip>
            </div>
            <div class="writing-box">
                <button class="button" @click="insertSignature">
                    插入教师签名
                    <svg fill="currentColor" viewBox="0 0 24 24" class="icon">
                        <path clip-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25zm4.28 10.28a.75.75 0 000-1.06l-3-3a.75.75 0 10-1.06 1.06l1.72 1.72H8.25a.75.75 0 000 1.5h5.69l-1.72 1.72a.75.75 0 101.06 1.06l3-3z" fill-rule="evenodd" />
                    </svg>

                </button>
            </div>
            <div class="writing-box">
                <button class="button" @click="insertDate">
                    插入日期
                    <svg fill="currentColor" viewBox="0 0 24 24" class="icon">
                        <path clip-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25zm4.28 10.28a.75.75 0 000-1.06l-3-3a.75.75 0 10-1.06 1.06l1.72 1.72H8.25a.75.75 0 000 1.5h5.69l-1.72 1.72a.75.75 0 101.06 1.06l3-3z" fill-rule="evenodd" />
                    </svg>

                </button>
            </div>
            <div class="writing-box">
                <button class="delete-button" @click="undoDrawing">
                    Back
                </button>
                <el-tooltip
                        class="flex-center"
                        style="font-size: 18px; margin-left: 10px; display: flex !important;"
                        effect="dark"
                        content="将根据选择的线条形状撤回"
                        placement="right"
                >
                    <i class="el-icon-question" />
                </el-tooltip>
            </div>
        </div>

        <div class="view-box" :style="`width:${pdfSize.width}px;height:${pdfSize.height}px;user-select:none`">
            <iframe v-show="pdfContent !== null" ref="pdfViewer" :src="`${pdfContent}#scrollbars=0&toolbar=0&statusbar=0`" width="100%" height="100%" />
            <div class="board" @dblclick.stop="activeEdit($event)" @click.stop="closeEdit" @mousedown="moveDownPosition" @mousemove="movePosition" @mouseup="mouseupPosition">
                <!-- 文字显示 -->
                <div v-for="(d,index) in contentList" :key="index" :data-id="index" class="divbox" :style="`position:absolute;z-index:3;left:${d.left-6}px;top:${d.top-8}px;color:red`" @dblclick.stop="activeClick(d)">
                    <i class="el-icon-circle-close closediv" size="32" @click.stop="removediv(d,index)" />
                    <div v-html="d.inputValue" />
                </div>
                <!-- 标注框显示 -->
                <svg id="svgRect" class="svgRect">
                    <g>
                        <rect v-for="(d,index) in lineList" :key="index" :width="d.width" :height="d.height" :x="d.x" :y="d.y" class="svgrect" />
                    </g>
                </svg>

                <!-- 绘制自由线条的svg -->
                <svg id="svgLine" class="svgLine" @mousedown.stop="startDrawing" @mousemove.stop="draw" @mouseup.stop="stopDrawing">
                    <g>
                        <path v-for="(line, index) in freehandLines" :key="index" :d="line.pathData" :stroke="line.color" stroke-width="2" fill="none" />
                    </g>
                </svg>
            </div>
            <!-- 弹出写文字的框 -->
            <div v-if="inputShow" :style="inputPosition" class="inputBox">
                <textarea v-model="inputContent.inputValue" :rows="10" style="width:300px;" />
                <!-- <editor v-model="inputContent.inputValue" @blur="closeEdit" style='width:300px;height:200px'/> -->
                <div class="sureBtn">
                    <el-button size="mini" @click="closeEdit">取消</el-button>
                    <el-button type="success" size="mini" @click="submitEdit">确定</el-button>
                </div>
            </div>
            <!-- 换页 -->
            <div style="display: flex;flex-direction: row;height: 80px;justify-content: center;align-items: center;">
                <el-pagination background layout="prev, pager, next" :total="total" :current-page="page" :page-size="1" @current-change="d => (page=d) && modifyPdf(d)" />
                <el-button @click="saveAs">保存</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import { PDFDocument, rgb } from 'pdf-lib'
import fontkit from '@pdf-lib/fontkit'
import $ from 'jquery'
import {SYNC_POST} from "@/scripts/Axios";
import {ElMessage} from "element-plus";
import {errorCallback} from "@/scripts/ErrorCallBack";
import {useStore} from "vuex";
import {toRaw} from "vue";

export default {
    name: 'PdfEdit',
    props: {
        pdfParam: {
            type: String,
            default: ''
        },
        pdfIdMsg: {
            type: Object,
            required: true
        }
    },
    setup() {
        const store = useStore()
        return {
            store
        }
    },
    // components: { Editor },
    data() {
        return {
            userName: this.store.getters.getUserInfo.name,
            pdfSize: {
                width: 0,
                height: 0
            },
            lineColor: 'black',
            inputPosition: {
                position: 'absolute',
                top: '0px',
                left: '0px',
                zIndex: 4
            },
            options: [{
                value: '选项1',
                label: '自由线条'
            }, {
                value: '选项2',
                label: '矩形'
            }],
            choosedLineValue: '',
            inputShow: false,
            inputContent: {
                inputValue: null,
                contentsList: [],
                top: 0,
                left: 0,
                show: 1
            },
            contentListAll: [],
            contentList: [],
            pdfUrl: '',
            pdfContent: '',
            total: 0,
            pdfDoc: {},
            page: 1,
            height: 0,
            node: null,
            // select: this.$store.state.word.select,
            // groupId: this.$store.state.word.select.groupId,
            // experienceId: this.$store.state.word.select.experienceId,
            pdfTempDoc: null,
            lineListAll: [],
            lineList: [],
            ubuntuFont: null,
            testParam: '',
            drawing: false,
            pathData: '',
            freehandLines: [] // 用于存储自由线条的数组
        }
    },
    watch: {
        lineColor: function(newColor) {
            // 更新样式
            const svgRects = document.querySelectorAll('.svgrect')
            svgRects.forEach((rect) => {
                rect.style.stroke = newColor
            })

            const svgPaths = document.querySelectorAll('.svgLine path')
            svgPaths.forEach((path) => {
                path.style.stroke = newColor
            })
        }
        // ...其他 watch 监听
    },
    created() {
        const select = {
            // experienceId: this.pdfIdMsg.eid,
            // wordId: this.pdfIdMsg.wid,
            // userId: this.pdfIdMsg.uid,
            experienceName: '',
            experienceScore: '',
            groupName: '',
            // groupId: this.pdfIdMsg.gid
        }
        // this.$store.commit('word/SET_SELECT', select)
    },
    mounted() {
        // this.pdfUrl = process.env.VUE_APP_TARGET_API + '/ljkj_experienceFile/' + this.pdfIdMsg.uid + '.' + this.pdfIdMsg.gid + '.' + this.pdfIdMsg.eid + '.pdf'
        // var url = this.pdfUrl + '?' + Math.random()// 去除缓存
        let url = 'http://localhost:19198/article/File2Pdf?article_id=a3FrhDG2lxK2'
        const _this = this
        async function getPdfContent() { // 加载pdf，并分页
            const arrayBuffer = await fetch(url, { method: 'get' }).then((res) => res.arrayBuffer())
            _this.pdfDoc = await PDFDocument.load(arrayBuffer)
            _this.total = _this.pdfDoc.getPages().length// 总页数
            _this.modifyPdf(1)// 显示第一页
            // _this.drawLineDone()// 添加画线事件
        }
        getPdfContent()
    },
    methods: {
        // 文字拖动事件
        moveDownPosition(position) {
            const index = $(position.target).parents('.divbox').data('id')
            this.node = this.contentListAll[index]
        },
        movePosition(position) {
            if (this.node) {
                // eslint-disable-next-line no-empty
                if ((this.node.left < 0 && position.movementX < 0) || (this.node.top < 0 && position.movementY < 0) || (this.node.left > this.pdfSize.width && position.movementX > 0) || (this.node.top > this.pdfSize.height && position.movementY > 0)) {
                } else {
                    this.node.left = this.node.left * 1 + position.movementX * 1
                    this.node.top = this.node.top * 1 + position.movementY * 1
                }
            }
        },
        mouseupPosition(position) {
            this.node = null
        },
        // 切换绘制方式时调用
        switchDrawingMode() {
            // 清除之前绑定的所有事件

            // 根据选择的形状调用对应的绘制函数
            if (this.choosedLineValue === '选项2') {
                this.drawLineDone()
            } else if (this.choosedLineValue === '选项1') {
                $('#svgLine').off('mousedown mousemove mouseup')
            }
        },
        getFormattedDate() {
            const today = new Date()

            // 获取年、月、日
            const year = today.getFullYear().toString()
            const month = ('0' + (today.getMonth() + 1)).slice(-2) // 月份从0开始，需要加1
            const day = ('0' + today.getDate()).slice(-2)

            // 组合成 "YYYY-MM-DD" 格式
            const formattedDate = `${year}-${month}-${day}`

            return formattedDate
        },
        insertDate() {
            const insertDate = this.getFormattedDate()
            const insertContent = {
                contentsList: [],
                inputValue: insertDate,
                top: '20',
                left: '20',
                page: 1,
                show: 0
            }
            var inputContent = JSON.parse(JSON.stringify(insertContent))
            this.contentListAll.push(inputContent)
            this.contentList.push(inputContent)
            console.log(insertDate)
        },
        insertSignature() {
            const insertName = this.userName
            let paramLeft = this.pdfSize.width - 80
            paramLeft = paramLeft.toString()
            const insertContent = {
                contentsList: [],
                inputValue: insertName,
                top: '20',
                left: paramLeft,
                page: 1,
                show: 0
            }
            var inputContent = JSON.parse(JSON.stringify(insertContent))
            this.contentListAll.push(inputContent)
            this.contentList.push(inputContent)
        },
        // 添加画线矩形事件
        drawLineDone() {
            var _this = this
            $('#svgLine').mousedown(function(position) {
                const that = this
                const x1 = position.offsetX
                const y1 = position.offsetY
                const str = {
                    page: _this.page,
                    x: x1 * 1,
                    y: y1 * 1,
                    width: 0,
                    height: 0
                }
                _this.lineList.push(str)
                $(this).mousemove(function(e) {
                    const x = e.offsetX
                    const y = e.offsetY
                    const width = x - x1 * 1
                    const height = y - y1 * 1
                    if (width > 0) {
                        str.width = width
                    } else {
                        str.width = -width
                        str.x = x
                        str.y = y
                    }
                    if (height > 0) {
                        str.height = height
                    } else {
                        str.height = -height
                        str.x = x
                        str.y = y
                    }
                })
                $(this).mouseup(function(e) {
                    $(that).unbind('mousemove')
                    $(that).unbind('mouseup')
                    _this.lineListAll.push(str)

                    // 手动设置新矩形的颜色
                    const newRect = document.querySelector('.svgrect:last-child')
                    if (newRect) {
                        newRect.style.stroke = _this.lineColor
                    }
                })
            })
        },
        // 删除文字
        removediv(d, index) {
            this.$confirm('确定要删除该标记吗?', '提示', {
                type: 'warning'
            }).then(() => {
                this.contentList.splice(index, 1)
                let temp = ''
                this.contentListAll.map((val, index) => {
                    if (val === d) {
                        temp = index
                        return
                    }
                })
                this.contentListAll.splice(temp, 1)
            })
        },
        // 自由线条
        startDrawing(event) {
            if (this.choosedLineValue === '选项1') {
                this.drawing = true
                const { offsetX, offsetY } = event
                this.pathData = `M ${offsetX} ${offsetY}`

                // 新增：添加新的自由线条对象到数组
                this.freehandLines.push({
                    pathData: `M ${offsetX} ${offsetY}`,
                    color: this.lineColor
                })
            }
        },

        draw(event) {
            if (this.drawing && this.choosedLineValue === '选项1') {
                const { offsetX, offsetY } = event
                this.pathData += ` L ${offsetX} ${offsetY}`

                // 新增：更新最后一个自由线条对象的 pathData
                if (this.freehandLines.length > 0) {
                    this.freehandLines[this.freehandLines.length - 1].pathData += ` L ${offsetX} ${offsetY}`
                }
            }
        },

        stopDrawing() {
            if (this.choosedLineValue === '选项1') {
                this.drawing = false
            }
        },
        undoDrawing() {
            // 在这里根据选择的值进行处理
            if (this.choosedLineValue === '选项1') {
                // 处理选项一的情况，撤回自由线条
                this.undoFreehandLines()
            } else if (this.choosedLineValue === '选项2') {
                // 处理选项二的情况，撤回矩形线条
                this.undoRectangles()
            }
        },

        undoFreehandLines() {
            // 实现撤回自由线条的逻辑
            // 你需要根据你的数据结构删除最后一条自由线条
            if (this.freehandLines.length > 0) {
                this.freehandLines.pop()
            }
        },

        undoRectangles() {
            // 实现撤回矩形线条的逻辑
            // 你需要根据你的数据结构删除最后一条矩形线条
            if (this.lineList.length > 0) {
                this.lineList.pop()
            }
        },
        // 显示pdf和各未保存的标记
        async modifyPdf(p) { // p是显示第几页
            this.contentList = []
            this.lineList = []
            this.closeEdit()
            for (let i = 0; i < this.contentListAll.length; i++) { // 该页所存在的文字
                if (this.contentListAll[i].page === p) {
                    this.contentList.push(JSON.parse(JSON.stringify(this.contentListAll[i])))
                }
            }
            for (let i = 0; i < this.lineListAll.length; i++) { // 该页所存在的线
                if (this.lineListAll[i].page === p) {
                    this.lineList.push(JSON.parse(JSON.stringify(this.lineListAll[i])))
                }
            }
            const _this = this
            const page = _this.pdfDoc.getPage(p * 1 - 1)
            const { width, height } = page.getSize()
            this.height = height
            _this.pdfSize.width = `${width + 2}`
            _this.pdfSize.height = `${height + 2}`
            const pdfTempDoc = await PDFDocument.create()// pdf的显示
            const copiedPages = await pdfTempDoc.copyPages(_this.pdfDoc, [p * 1 - 1])
            pdfTempDoc.addPage(copiedPages[0])
            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                const blob = new Blob([await pdfTempDoc.save()], { type: 'application/pdf' })
                console.log(blob)
            } else {
                const pdfUrl = URL.createObjectURL(
                    new Blob([await pdfTempDoc.save()], { type: 'application/pdf' })
                )
                _this.pdfContent = pdfUrl
            }
        },
        // 在PDF-lib库中，borderColor 期望的类型是 Color，而不是简单的字符串。你可以使用PDF-lib提供的 rgb 函数来创建颜色对象。
        hexToRgb(hex) {
            // 去掉可能的 # 前缀
            hex = hex.replace(/^#/, '')

            // 解析RGB值
            const bigint = parseInt(hex, 16)
            const r = (bigint >> 16) & 255
            const g = (bigint >> 8) & 255
            const b = bigint & 255

            return { r, g, b }
        },
        // 保存pdf
        async saveAs() {
            const { r, g, b } = this.hexToRgb(this.lineColor)
            const paramColor = rgb(r / 255, g / 255, b / 255)
            const pages = this.pdfDoc.getPages()
            const url = require('@/assets/STSong.ttf')

            const fontBytes = await fetch(url).then((res) => res.arrayBuffer())// 添加字体包，没有字体包不显示中文
            this.pdfDoc.registerFontkit(fontkit)
            this.ubuntuFont = await this.pdfDoc.embedFont(fontBytes, { subset: true })// 不加subset:true,pdf会变得很大
            // 把所有的文字和线框的标记都画到pdf上去
            // FIXME: 无法保存绘图后的PDF
            let newPdf = {}
            for (let k = 0; k < this.page; k++) {
                const firstPage = pages[k]
                console.log(firstPage)
                for (let i = 0; i < this.contentListAll.length; i++) {
                    const content = this.contentListAll[i]
                    if (content.page - 1 === k) {
                        // for (let j = 0; j < content.contentsList.length; j++) {
                        const text = content.inputValue
                        toRaw(firstPage).drawText(toRaw(text), {
                            x: toRaw(content).left * 1,
                            y: toRaw(this.height) * 1 - toRaw(content.top) * 1,
                            size: 14,
                            font: toRaw(this.ubuntuFont),
                            color: toRaw(paramColor)
                        })
                    }
                    // }
                }
                console.log(pages[k])
                console.log(firstPage === pages[k])
                for (let i = 0; i < this.lineListAll.length; i++) {
                    const content = this.lineListAll[i]
                    if (content.page - 1 === k) {
                        const firstPage = pages[k]
                      firstPage.drawRectangle({
                            x: content.x * 1,
                            y: this.height * 1 - content.y * 1,
                            width: content.width * 1,
                            height: -content.height * 1,
                            borderWidth: 2,
                            borderColor: paramColor,
                            opacity: 0,
                            borderOpacity: 1
                        })
                    }
                }
            }
            // 保存自由线条
            for (let k = 0; k < this.page; k++) {
                const firstPage = pages[k]

                // 保存自由线条
                for (let i = 0; i < this.freehandLines.length; i++) {
                    const line = this.freehandLines[i]
                    if (line.page - 1 === k) {
                      firstPage.drawSvgPath(line.pathData, {
                            borderColor: paramColor,
                            borderWidth: 2
                        })
                    }
                }
            }
            let newPDFDoc = PDFDocument.create()
            // 合并所有page
            // for(let i = 0; i < this.page; i++){
            //   newPDFDoc =
            // }
            // 把pdf转化成base64
            const pdfContent = await this.pdfDoc.saveAsBase64({
                dataUri: true
            })
            const base64Data = pdfContent.split(',')[1]
            // 将Base64字符串转换为Uint8Array
            const byteCharacters = atob(base64Data)
            const byteNumbers = new Array(byteCharacters.length)
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i)
            }
            const uint8Array = new Uint8Array(byteNumbers)

            // 创建Blob对象
            const blob = new Blob([uint8Array], { type: 'application/pdf' })

            // 现在，'blob' 是一个Blob对象，你可以使用它进行后续操作

            // 此处调接口，把base64返给后台
            const pdfRawData = blob
            // const htmlString = ''
            // this.$store.dispatch('word/uploadExperiencePdf', {
            //     htmlString,
            //     pdfRawData
            // })
            let param = new FormData()
            param.append('articleId', this.store.getters.getUserInfo.id)
            param.append('approvalArticle', pdfRawData)
            await SYNC_POST('/auditor/saveApprovalArticle', param, async (response) => {
                if (response.status === 200 && response.data.message === 'Success.') {
                  console.log('Save successfully!')
                  ElMessage({
                    showClose: true,
                    message: '已成功保存批注!',
                    type: 'success'
                  })
                  location.href = '/#/user/' + this.store.getters.getUserInfo.id
                } else {
                  errorCallback(response)
                }
              }
            )
            this.contentListAll = []
            this.contentList = []
            this.lineListAll = []
            this.lineList = []
            this.modifyPdf(this.page)
        },
        // 双击打开写字板
        activeEdit(e) {
            const { offsetX, offsetY } = e
            this.inputPosition.top = `${offsetY}px`
            this.inputPosition.left = `${offsetX}px`
            this.inputShow = true
            this.inputContent.top = `${offsetY}`
            this.inputContent.left = `${offsetX}`
            this.inputContent.page = this.page
            this.inputContent.show = 0
        },
        // 保存文字
        submitEdit() {
            if (this.inputContent.show === 0) {
                var inputContent = JSON.parse(JSON.stringify(this.inputContent))
                console.log(inputContent)
                this.contentListAll.push(inputContent)
                this.contentList.push(inputContent)
            }
            this.closeEdit()
        },
        // 关闭写字板
        closeEdit() {
            this.inputContent = JSON.parse(JSON.stringify(this.inputContent))
            this.inputContent.inputValue = null
            this.inputShow = false
        },
        // 双击修改文字标记
        activeClick(d) {
            this.inputContent = d
            this.inputPosition.left = `${d.left}px`
            this.inputPosition.top = `${d.top}px`
            this.inputContent.show = 1
            this.inputShow = true
        }
    }
}
</script>

<style scoped>
.view-box {
    position: relative;
    border: 1px solid #ccc;
    width: 100%;
    height: 600px;
    margin: 10px auto;
    margin-bottom: 50px;
}
.pdf-input {
    width: 100px;
    line-height: 20px;
    border: 1px solid #ccc;
    background: #eee;
    z-index: 3;
}
.inputBox {
    background: #fff;
    padding: 5px;
    border-radius: 5px;
}
.sureBtn {
    text-align: right;
    margin-top: 10px;
}
.board {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
}
.svgrect {
    stroke: rgb(237, 10, 10);
    stroke-width: 2;
    position: relative;
    fill-opacity: 0;
}
.svgLine {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
    z-index: 1;
}

.svgRect {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
    z-index: 1;
}
.divbox {
    cursor: move;
    border: 1px solid red;
    z-index: 3;
    padding: 5px;
    white-space: nowrap;
}
.movediv {
    position: absolute;
    top: -8px;
    left: -8px;
}
.closediv {
    position: absolute;
    top: -8px;
    right: -8px;
    cursor: pointer;
    background: #f64404;
    color: #fff;
    border-radius: 50%;
}
.choosed-box {
    width: 230px;
    position: absolute;
    top: 93px;
    right: 0;
}

.back-box {
    position: absolute;
    top: 93px;
    right: 150px;
}
.choose-line {
    height: 40px;
    display: flex;
    flex-direction: row;
    margin-bottom: 15px;
}
.choose-color,
.writing-box {
    height: 40px;
    display: flex;
    flex-direction: row;
    margin-bottom: 15px;
}

.button {
    position: relative;
    transition: all 0.3s ease-in-out;
    box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.2);
    padding-block: 0.5rem;
    padding-inline: 1.25rem;
    background-color: #1E96E1;
    border-radius: 9999px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ffff;
    gap: 10px;
    font-weight: bold;
    border: 3px solid #ffffff4d;
    outline: none;
    overflow: hidden;
    font-size: 15px;
}

.icon {
    width: 24px;
    height: 24px;
    transition: all 0.3s ease-in-out;
}

.button:hover {
    transform: scale(1.05);
    border-color: #fff9;
}

.button:hover .icon {
    transform: translate(4px);
}

.button:hover::before {
    animation: shine 1.5s ease-out infinite;
}

.button::before {
    content: "";
    position: absolute;
    width: 100px;
    height: 100%;
    background-image: linear-gradient(
            120deg,
            rgba(255, 255, 255, 0) 30%,
            rgba(255, 255, 255, 0.8),
            rgba(255, 255, 255, 0) 70%
    );
    top: 0;
    left: -100px;
    opacity: 0.6;
}

@keyframes shine {
    0% {
        left: -100px;
    }

    60% {
        left: 100%;
    }

    to {
        left: 100%;
    }
}

.delete-button {
    background-color: #1E96E1;
    color: #fff;
    font-size: 14px;
    border: 0.5px solid rgba(0, 0, 0, 0.1);
    padding-bottom: 8px;
    width: 60px;
    height: 65px;
    border-radius: 15px 15px 12px 12px;
    cursor: pointer;
    position: relative;
    will-change: transform;
    transition: all .1s ease-in-out 0s;
    user-select: none;
    /* Add gradient shading to each side */
    background-image: linear-gradient(to right, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0)),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0));
    background-position: bottom right, bottom right;
    background-size: 100% 100%, 100% 100%;
    background-repeat: no-repeat;
    box-shadow: inset -4px -10px 0px rgba(255, 255, 255, 0.4),
    inset -4px -8px 0px rgba(0, 0, 0, 0.3),
    0px 2px 1px rgba(0, 0, 0, 0.3),
    0px 2px 1px rgba(255, 255, 255, 0.1);
    transform: perspective(70px) rotateX(5deg) rotateY(0deg);
}

.delete-button::after {
    content: '';
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-image: linear-gradient(to bottom, rgba(255, 255, 255, 0.2), rgba(0, 0, 0, 0.5));
    z-index: -1;
    border-radius: 15px;
    box-shadow: inset 4px 0px 0px rgba(255, 255, 255, 0.1),
    inset 4px -8px 0px rgba(0, 0, 0, 0.3);
    transition: all .1s ease-in-out 0s;
}

.delete-button::before {
    content: '';
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-image: linear-gradient(to right, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0)),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0));
    background-position: bottom right, bottom right;
    background-size: 100% 100%, 100% 100%;
    background-repeat: no-repeat;
    z-index: -1;
    border-radius: 15px;
    transition: all .1s ease-in-out 0s;
}

.delete-button:active {
    will-change: transform;
    transform: perspective(80px) rotateX(5deg) rotateY(1deg) translateY(3px) scale(0.96);
    height: 64px;
    border: 0.25px solid rgba(0, 0, 0, 0.2);
    box-shadow: inset -4px -8px 0px rgba(255, 255, 255, 0.2),
    inset -4px -6px 0px rgba(0, 0, 0, 0.8),
    0px 1px 0px rgba(0, 0, 0, 0.9),
    0px 1px 0px rgba(255, 255, 255, 0.2);
    transition: all .1s ease-in-out 0s;
}

.delete-button::after:active {
    background-image: linear-gradient(to bottom,rgba(0, 0, 0, 0.5), rgba(255, 255, 255, 0.2));
}

.delete-button:active::before {
    content: "";
    display: block;
    position: absolute;
    top: 5%;
    left: 20%;
    width: 50%;
    height: 80%;
    background-color: rgba(255, 255, 255, 0.1);
    animation: overlay 0.1s ease-in-out 0s;
    pointer-events: none;
}

@keyframes overlay {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

.delete-button:focus {
    outline: none;
}

</style>
