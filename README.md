# 软件部署及配置方法

### 部署前置软件要求

- Java Development Kit 17及以上
- MySQL 8.0及以上
- Redis
- 搭配npm或yarn的Node.js 16.0及以上，推荐20

### 部署方法

#### MySQL

启动MySQL服务

在服务器上创建数据库snake_db
运行根目录下snake_db.sql语句集合

#### Redis

能在服务器上运行即可，需要启动Redis

#### SpringBoot

在项目\application\literatureClub\src\main\resources\config下创建文件“application.yaml”，内容如下：

```yaml
spring:
  datasource:
    username: # 这里填mysql用户名，一般为root
    password: # 这里填mysql密码
    url: jdbc:mysql://localhost:3306/snake_db
 
snach:
  common:
    redisKeyOfArticleTags: "ARTICLE_TAGS"
    redisKeyOfCurrentUserId: "CURRENT_USER_ID"
  datasource:
    redis:
      port: 6379
      host: localhost
      pool:
        max-idle: 10
        max-active: 20
        min-idle: 2
      timeout: 2000
```

配置完成后，通过application/literatureClub/src/main/java/com/snach/literatureclub/LiteratureClubApplication.java启动

#### Node.js

进入前端工程根目录web，输入`npm install`或`yarn install`来下载相关依赖

使用npm或yarn启动或构建项目，启动命令如下：

```
npm run serve
```

```
yarn serve
```