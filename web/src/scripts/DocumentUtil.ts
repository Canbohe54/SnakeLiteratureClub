import mammoth from "mammoth";
import {mammothOptions} from "@/scripts/mammothOption";

export const documentToHtml = async (file: any) => {
  let subFileNames = file.name.split('.')
  if (subFileNames[subFileNames.length - 1] == 'docx') {
    return documentToDOCX(file)
  }
  return `<iframe :src="${URL.createObjectURL(file)}"/>`
}
export const documentToDOCX = async (file: any) => {
  return new Promise(function (resolve, reject) {
    const fileReader = new FileReader()
    let html = ''
    fileReader.onload = async (event) => {
      const arrayBuffer = event.target?.result as ArrayBuffer
      mammoth.convertToHtml({arrayBuffer: arrayBuffer}, mammothOptions)
        .then(async function (result) {
          html = result.value; // The generated HTML
          resolve(html)
          let messages = result.messages; // Any messages, such as warnings during conversion
        })
        .catch(function (error) {
          console.error(error);
        });
    }
    fileReader.readAsArrayBuffer(file);
  })
}

export const getFileBuffer = async (file: any) => {
  return new Promise(function (resolve, reject){
      const fileReader = new FileReader()
      fileReader.onload = async (event) => {
          const arrayBuffer = event.target?.result as ArrayBuffer
          resolve(arrayBuffer)
      }
      fileReader.readAsArrayBuffer(file);
  })
}
export const fileToBlob = async (file: any) => {
  return new Promise(function (resolve, reject){
      const type = file.type;
      const fileReader = new FileReader()
      fileReader.onload = async (event) => {
          const blob = new Blob([event.target?.result as ArrayBuffer], {type});
          resolve(blob)
      }
      fileReader.readAsArrayBuffer(file);
  })
}
