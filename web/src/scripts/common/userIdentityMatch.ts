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


export function getIndentityTagType(userIdentity: string) {
    switch (userIdentity) {
        case 'CONTRIBUTOR':
            return 'success'
        case 'TEACHER':
            return 'primary'
        case 'ADMINISTRATOR':
            return 'primary'
        case 'EXPERT':
            return 'warning'
        case 'HUNTER':
            return 'danger'
        case 'VOLUTEER':
            return 'warning'
        default:
            return 'info'
    }
}

export function getIndentityTagEffect(userIdentity: string) {
    switch (userIdentity) {
        case 'CONTRIBUTOR':
            return 'light'
        case 'TEACHER':
            return 'plain'
        case 'ADMINISTRATOR':
            return 'light'
        case 'EXPERT':
            return 'light'
        case 'HUNTER':
            return 'light'
        case 'VOLUTEER':
            return 'plain'
        default:
            return 'light'
    }
}
