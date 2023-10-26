<template>
    <canvas ref="canvas" :width="contentWidth" :height="contentHeight" @click="reloadCanvas"></canvas>
</template>

<script>
export default {
  data () {
    return {
      contentWidth: 120,
      contentHeight: 40,
      backgroundColor: 'rgb(0,0,0,0.2)'// 画布背景颜色
    }
  },
  mounted () {
    this.createdCode()// 页面初始化时生成验证码
  },
  methods: {
    createdCode () {
      const len = 4 // 验证码长度
      const chars = 'ABCDEFGHJKMNPQRSTWXYZ123456789' // 验证码字符集
      const codeList = Array.from({ length: len }, () =>
        chars.charAt(Math.floor(Math.random() * chars.length))
      )

      const identifyCode = codeList.join('')//    验证码  <-----------

      this.$emit('getImageCode', identifyCode.toLowerCase())// 将验证码字符串通过事件 emit 给父组件
      const canvas = this.$refs.canvas// 获取 canvas 元素
      const ctx = canvas.getContext('2d')
      ctx.textBaseline = 'bottom'
      if (this.backgroundColor !== '' && this.backgroundColor != null) { // 绘制画布背景颜色
        ctx.fillStyle = this.backgroundColor
      } else {
        ctx.fillStyle = this.randomColor(255, 255)
      }
      ctx.fillRect(0, 0, this.contentWidth, this.contentHeight)
      codeList.forEach((code, i) => { // 绘制验证码字符
        this.drawText(ctx, code, i + 1, len)
      })
      this.drawLine(ctx)// 绘制干扰线
      this.drawDot(ctx)// 绘制干扰点
    },
    randomNum (min, max) { // 生成指定范围内的随机整数
      return Math.floor(Math.random() * (max - min) + min)
    },
    randomColor (min, max) { // 生成指定范围内的随机颜色
      const r = this.randomNum(min, max)
      const g = this.randomNum(min, max)
      const b = this.randomNum(min, max)
      return `rgb(${r},${g},${b})`
    },
    drawText (ctx, txt, i, len) { // 绘制验证码字符
      ctx.fillStyle = this.randomColor(0, 160)
      ctx.font = `${this.randomNum(25, 30)}px SimHei`
      const x = (i / (len + 1)) * 120
      const y = this.randomNum(30, 35)
      const deg = this.randomNum(-45, 45)
      ctx.translate(x, y)
      ctx.rotate((deg * Math.PI) / 180)
      ctx.fillText(txt, 0, 0)
      ctx.rotate((-deg * Math.PI) / 180)
      ctx.translate(-x, -y)
    },
    drawLine (ctx) { // 绘制干扰线
      for (let i = 0; i < 5; i++) {
        ctx.strokeStyle = this.randomColor(100, 255)
        ctx.beginPath()
        ctx.moveTo(this.randomNum(0, 120), this.randomNum(0, 40))
        ctx.lineTo(this.randomNum(0, 120), this.randomNum(0, 40))
        ctx.stroke()
      }
    },
    drawDot (ctx) { // 绘制干扰点
      for (let i = 0; i < 80; i++) {
        ctx.fillStyle = this.randomColor(0, 255)
        ctx.beginPath()
        ctx.arc(this.randomNum(0, 120), this.randomNum(0, 40), 1, 0, 2 * Math.PI)
        ctx.fill()
      }
    },
    reloadCanvas () { // 点击按钮时清除画布并重新生成验证码
      const canvas = this.$refs.canvas
      const ctx = canvas.getContext('2d')
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      this.createdCode()
    }
  }
}
</script>
