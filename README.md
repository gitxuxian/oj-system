# Xu-OJ 在线判题系统后端

## 项目简介

`Xu-OJ` 是一个功能完善的在线编程学习与竞赛平台（Online Judge）的后端服务。它为开发者提供了一个集题目练习、在线判题、编程竞赛、社区交流于一体的综合性平台。本项目基于 Spring Boot 构建，整合了多种主流技术栈，旨在提供一个高性能、高可用的判题解决方案。

## 核心功能

*   **用户系统**：支持用户注册、登录、个人信息管理以及出题人权限申请。
*   **题目模块**：提供题目的增删改查、题目列表分页、内容搜索等功能。
*   **在线判题**：支持多种编程语言（如 C, C++, Java, Python），用户提交代码后，系统通过消息队列异步调用判题服务进行判题，并返回结果。
*   **竞赛系统**：管理员可以创建、管理和发布编程竞赛。竞赛支持多种赛制（如 ACM/OI），可设置密码、指定题目和时间。
*   **实时排行榜**：根据竞赛规则（如解题数、罚时）为正在进行的竞赛生成实时排行榜。

## 技术栈

| 分类         | 技术                                   | 说明                                     |
|--------------|----------------------------------------|------------------------------------------|
| **核心框架** | Spring Boot 2.7.2                      | 快速构建、易于配置的 Java Web 框架         |
| **数据库**   | MySQL                                  | 关系型数据库                             |
| **ORM**      | MyBatis-Plus                           | 高效的数据库操作框架                     |
| **权限认证** | Sa-Token                               | 轻量级、功能强大的 Java 权限认证框架     |
| **消息队列** | RabbitMQ                               | 用于判题任务的异步处理，实现系统解耦       |
| **API 文档** | Knife4j                                | 美观、强大的 Swagger API 文档聚合工具      |
| **文件存储** | 腾讯云 COS (可选)                      | 用于存储用户上传的头像或其他文件         |
| **工具库**   | Hutool, Lombok, Commons Lang3, EasyExcel | 简化开发的常用工具集                     |

## 快速启动

### 环境要求

*   JDK 1.8+
*   Maven 3.6+
*   MySQL 8.0+
*   Redis
*   RabbitMQ
*   Elasticsearch (可选)

### 配置步骤

1.  **克隆项目**
    ```bash
    git clone <your-repository-url>
    cd xu-ojback
    ```

2.  **数据库初始化**
    *   创建一个名为 `my_db` 的数据库。
    *   将 `sql/_localhost-2025_06_08_13_07_01-dump.sql` 文件导入到 `my_db` 数据库中。

3.  **修改配置**
    *   在 `src/main/resources/` 目录下，将 `application.yml` 复制为 `application-dev.yml`（或 `application-local.yml`）。
    *   **重要**：将 `application.yml` 中的 `active` profile 修改为您新建的配置文件名，例如 `dev`。
    *   **将您新建的配置文件（例如 `application-dev.yml`）添加到 `.gitignore` 文件中，以避免提交敏感信息！**
    *   修改 `application-dev.yml` 中的数据库、Redis、RabbitMQ 等连接信息，替换为您的本地配置。

    ```yaml
    # application.yml
    spring:
      profiles:
        active: dev # 激活开发环境配置
    ```

### 运行项目

使用 Maven 启动 Spring Boot 应用：

```bash
mvn spring-boot:run
```

项目启动后，服务将在 `9001` 端口上运行。

## API 接口文档

项目启动后，您可以访问以下地址查看由 Knife4j 生成的 API 文档：

[http://localhost:9001/doc.html](http://localhost:9001/doc.html)

该文档详细列出了所有可用的 API 接口、请求参数和响应格式。

## 数据库设计

项目主要包含以下数据表：

*   `user`: 用户表
*   `question`: 题目信息表
*   `question_submit`: 用户题目提交记录表
*   `game`: 比赛信息表
*   `game_question`: 比赛与题目的关联表
*   `game_rank`: 比赛排行榜表
*   `creator_application`: 出题人申请表

详细的表结构请参考 `sql/_localhost-2025_06_08_13_07_01-dump.sql` 文件。
