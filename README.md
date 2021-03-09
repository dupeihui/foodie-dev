后端框架：SpringBoot、Spring、Mybatis + Redis（结合Cookie实现分布式会话，存储用户和购物车信息） + Elasticsearch（优化商品搜索）  

本仓库为网上商城后端代码，实现整个项目需要结合前端代码[foodie-frontend-code](https://github.com/dupeihui/foodie-frontend-code)

## 可用功能

1.首页轮播图、商品分类、商品详情、商品搜索、商品评价   
2.购物车、订单、支付  
3.用户中心、订单管理、评价管理

## 项目演示图
![image](https://github.com/dupeihui/foodie-dev/blob/master/doc/image/foodie-shop.png)

![image](https://github.com/dupeihui/foodie-dev/blob/master/doc/image/foodie-center.png)

![image](https://github.com/dupeihui/foodie-dev/blob/master/doc/image/swagger-api.png)


## 如何开始使用？
1.安装redis  
2.安装Elasticsearch6.4.3  
3.配置elasticsearch-analysis-ik  
### 本地运行
1.在本地Mysql中新建数据库`foodie-shop-dev`，运行doc/foodie-shop.sql  
2.IDEA中运行foodie-dev-api模块下的Application.java  
3.访问http://localhost:8088/swagger-ui.html