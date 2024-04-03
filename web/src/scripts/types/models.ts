export interface User {

}

export interface Article extends ArticleFile {
  id: string,
  time: object,
  textBy: string,
  title: string,
  description: string,
  authorName: string,
  authorOrganization: string,
  authorGrade: string,
  auditStatus: string,
  publishStatus: string,

}

export interface ArticleFile {
  raw: object,
  fileType: string
}
