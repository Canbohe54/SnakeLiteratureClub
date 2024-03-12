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
function transformElement(element : any) {
  if (element.children) {
    var children = element.children.map(transformElement);
    element = { ...element, children: children };
  }
  if (element.type === 'paragraph') {
    element = transformParagraph(element);
  }
  return element;
}

function transformParagraph(element : any) {
  if (element.alignment === 'center' && !element.styleId) {
    return { ...element, styleName: 'center' }; // 给标签增加style-name
  } else {
    return element;
  }
}
export const mammothOptions = {
  styleMap: ['u => u'],
  transformDocument: transformElement,
}
