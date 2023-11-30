export interface AttributeAddableObject<VT = any> {
  [key: string | number]: VT
}

interface ArticleInfo {
  attr: AttributeAddableObject<AttributeAddableObject>,
  [key: string]: any
}

export function articleTagFilter (articleList: ArticleInfo[], filterSelection: AttributeAddableObject) {
  // TODO: tag filter
}
