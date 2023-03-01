### 1、项目背景
后端接口遇到问题，如何快速复现，然后定位定位呢、首先可以想的是，去查看项目的输出日志，然后重日志中中定位到代码行。如果在项目中使用这个，就省去了翻日志的过程了。
这个starter组件可以实现接口监控，出现异常会发送详细的报错信息到你的邮箱。

### 2、安装
```
<dependency>
    <groupId>io.github.zglgithubx</groupId>
    <artifactId>apinotice-spring-boot-starter</artifactId>
    <version>0.0.2-RELEASE</version>
</dependency>
```
### 3、例子
#### 3.1、配置
在项目的配置文件yml中，配置邮箱：
注：使用Java发送邮箱，需要自行学习如何开启
```
spring:
    mail:
        host: 邮箱服务器 //smtp.163.com
        username: 账号
        password: 密码（第三方应用密码，非平台的登录密码）
        default-encoding: UTF-8
        port: 465
        properties:
          mail:
            smtp:
              auth: true
              starttls:
                enable: true
                required: true
              socketFactory:
                class: javax.net.ssl.SSLSocketFactory
                port: 465
                fallback: false
abnormal:
    sender: 发件人，默认为：API助手
    # 线程池配置
    thread-pool:
        # 核心线程数
        core-pool-size: 10
        # 最大线程数
        max-pool-size: 15
        # 工作队列容量
        queue-capacity: 500
        # 线程池维护线程所允许的空闲时间
        keep-alive-seconds: 300
        # 拒绝策略
        rejected-execution-handler: CallerRunsPolicy
```
#### 3.2、使用
此功能的核心是使用注解@Notice，包含两个属性，author:作者，email:接收提醒的邮箱地址
```Java
@RestController
public class TestController {
	@GetMapping("/hello")
	@Notice(author = "张三",email = "xxx@gmail.com")
	public void test(){
		throw new RuntimeException();
	}
}
```

### 版本信息  
#### 0.0.2-RELEASE  
- 新增：使用Spring线程池，实现异步通知，降低方法耗时。

#### 0.0.1-RELEASE  

- 新增：邮箱通知。


