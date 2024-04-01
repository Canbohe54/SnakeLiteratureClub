export interface AttributeAddableObject<VT = any> {
  [key: string | number]: VT
}

export interface ArticleInfo {
  attr: string,
  [key: string]: any
}

function containsKey (articleAttr: AttributeAddableObject<AttributeAddableObject>, key: string) {
  return articleAttr[key] !== undefined;
}

function containsValue<VT> (arrayLikeItem: VT[], value: VT) {
  for (const vt of arrayLikeItem) {
    if (vt === value) {
      return true
    }
  }
  return false
}

function articleTagCheck (articleAttr: AttributeAddableObject, filterSelection: AttributeAddableObject<Array<string>>): boolean {
  for (const groupKey in filterSelection) {
    if (!isSelectionGroupEmpty(filterSelection[groupKey]) && !containsKey(articleAttr, groupKey) && !isSelectionEmpty(filterSelection)) {
      return false
    }
    for (const tagName of filterSelection[groupKey]) {
      if (!containsValue(articleAttr[groupKey], tagName)) {
        return false
      }
    }
  }
  return true
}

export function articleTagFilter (articleList: ArticleInfo[], filterSelection: AttributeAddableObject) {
  let conformArticle: ArticleInfo[] = []
  for (const article of articleList) {
    let tags = article.tags
    if (articleTagCheck(tags, filterSelection)) {
      conformArticle.push(article)
    }
  }
  return conformArticle
}

function isSelectionEmpty (filterSelection: AttributeAddableObject) {
  for (const groupKey in filterSelection) {
    if (filterSelection[groupKey].length !== 0) {
      return false
    }
  }
  return true
}

function isSelectionGroupEmpty (filterSelectionGroup: Array<any>) {
  return filterSelectionGroup.length === 0
}
