# 项目技术点总结

### 1. 基于Spring Security的权限管理

### 2. 短信免密登录(阿里云)

### 3. 文件上传服务(七牛云)

### 4. 搜索引擎搭建:(ES+Kafka+MySQL)
#### a. 利用MQ进行异步索引构建
#### b. ES聚合功能(基本类型聚合以及地理位置聚合)
#### c. ES+百度地图
#### d. search-as-you-type:利用es的suggest实现搜索框提示

### 5. redis-session

### 6. 监控报警:
#### a. Spring Schedule定时任务
#### b. Spring Mail邮件告警


### 7. 数据可视化:(ELK)
#### a. Kibana(前端可视化组件)
#### b. Logstash(日志采集中间件)
配合Nginx access日志

logstash.conf:(简单收集)

```
input {
    file {
        path => ["/Users/elk/nginx/logs/access.log"]
        type => "nginx_access"
        start_position => "beginning"        
    }
}

filter {
    if [type] == "nginx_access" {
        grok {
            pattern_dir => "/patterns"
            match => {
                "message" => "%{NGINXACCESS}"
            }
        }
        
        date {
            match => ["timestamp","dd/MM/YY"]
        }
        
        if [param] {
            ruby {
                init => "@kname = ['quote','url_args']"
                code => "
                    new_event = LogStash::Event.new(Hash[@kname.zip(event.get('param').split('?'))])
                    new_event.remove('@timestamp')
                    event.append(new_event)
                "
            }
            
            if [url_args] {
                ruby {
                    init => "@kname = ['key','value']"
                    code => "event.set('nested_args',event.get('url_args').split('&').collect {|i| Hash[@kname.zip(i.split('='))]})"
                    remove_field =? ["url_args","param","quote"]
                }
            }
        }
        
        
        mutate {
            convert => ["response","integer"]
            remove_field => "timestamp"
        }
    }
}

output {

    stdout {
        codec => rubydebug
    }
    
    elasticsearch {
        hosts => ["http://localhost:9200"]
        index => "logstash-%{type}-%{+YYYY.MM.dd}"
        document_type => "%{type}"
        flush_size => 200
        idle_flush_time => 1
        sniffing => true
        user = "xxx"
        password => "xxxx"
    }
}
```
自定义解析可以自己搞个pattern

### 8. 单元测试覆盖报告

```
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <executions>
            <execution>
                <id>default-prepare-agent</id>
                <goals>
                    <goal>prepare-agent</goal>
                </goals>
            <execution>
            <execution>
                <id>default-report</id>
                <phase>prepare-package</phase>
                <goals>
                    <goal>report</goal>
                </goals>
            <execution>
            <execution>
                <id>default-check</id>
                <phase>prepare-package</phase>
                <goals>
                    <goal>check</goal>
                </goals>
                <configuration>
                    <rules>
                        <rule>
                            <element>CLASS</element>
                             <includes>
                                <include>com.xdCao.service.*.*</include>
                                <include>com.xdCao.mapper.*.*</include>
                            </includes>
                            <limits>
                                <limit>
                                    <counter>LINE</counter>
                                    <value>COVEREDRATIO</value>
                                    <minimum>0.8</minimum>
                                </limit>
                            <limits>
                        </rule>
                    </rules>
                </configuration>
            <execution>
        <executions>
    </plugin>
```
