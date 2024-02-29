import axios from 'axios'
import mammoth from 'mammoth'

function base64ToBlob(base64: any, mimeType: any) {
  let bytes = window.atob(base64);
  let ab = new ArrayBuffer(bytes.length);
  let ia = new Uint8Array(ab);
  for (let i = 0; i < bytes.length; i++) {
    ia[i] = bytes.charCodeAt(i);
  }
  return new Blob([ia], { type: mimeType });
}

async function uploadBase64Image(base64Image: any, mime: any) {
  const formData = new FormData()
  formData.append("file", base64ToBlob(base64Image, mime))
  return {
    data: {
     path:""
    }
  }
  // return await axios({
  //   method: "post",
  //   url: "http://localhost:3000/uploadfile", // 本地图片上传的API地址
  //   data: formData,
  //   config: { headers: { "Content-Type": "multipart/form-data" } }
  // })
}

// const mammothOptions = {
//   convertImage: mammoth.images.imgElement(function(image) {
//     return image.read("base64").then(async (imageBuffer) => {
//       const result = await uploadBase64Image(imageBuffer, image.contentType);
//       return {
//         src: result.data.path // 获取图片线上的URL地址
//       };
//     });
//   })
// };
const mammothOptions = {
  convertImage: mammoth.images.imgElement(function(image) {
    return image.readAsBase64String().then(function(imageBuffer) {
      return {
        src: "data:" + image.contentType + ";base64," + imageBuffer
      };
    });
  })
};
