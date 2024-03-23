export function getIdentityChinese(identity: string){
    const identityMap = new Map([
        ['CONTRIBUTOR','学生作者'],
        ['TEACHER','教师'],
        ['ADMINISTRATOR','学校管理员'],
        ['EXPERT','专家'],
        ['HUNTER','报刊专员'],
        ['VOLUNTEER','审稿志愿者']
    ])
    return identityMap.get(identity)
}