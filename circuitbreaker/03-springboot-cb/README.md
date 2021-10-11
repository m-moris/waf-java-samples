# Spring Boot + Spring Cloud CircutBreaker Resilience4j

Spring Boot と Spring Cloud Circuit Breaker の Spring Retry を組み合わせたサーキットブレーカーサンプルです。

## 概要

Spring Cloud Circuit Breaker によって提供されたサーキットブレーカー機能を利用します。いくつか実装を選択することができますが、本サンプルでは Rejilience4j を利用します。

## 前提

## 前提条件

- Java 11 以降
- Maven 3.6 以降
- HTTP エンドポイントを呼び出すために、`curl` を利用します。

## 依存ライブラリ

`spring-boot-starter-web` 以外に、以下のライブラリが必要です。詳細は、`pom.xml` を参照してください。

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-spring-retry</artifactId>
            <version>2.0.2</version>
        <dependency>
```

## ビルドおよび実行方法

以下のコマンドでビルドします。

```
mvn clean package 
```
以下のコマンドでSpring Boot アプリケーションを起動します。 

```
mvn spring-boot:run
```

以下のログが表示され、アプリケーションが起動します。

```log
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.5)

2021-10-11 14:03:12.082  INFO 22471 --- [           main] o.p.w.s.cb.sb.r4j.SampleApplication      : Starting SampleApplication using Java 11.0.11 on NICKEL with PID 22471 (/work/waf-java-samples/circuitbreaker/03-springboot-cb/target/classes started by moris in /work/waf-java-samples/circuitbreaker/03-springboot-cb)
2021-10-11 14:03:12.090  INFO 22471 --- [           main] o.p.w.s.cb.sb.r4j.SampleApplication      : No active profile set, falling back to default profiles: default
2021-10-11 14:03:14.859  INFO 22471 --- [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=dc13a1f0-731b-3a8e-9bc3-ef28ee6e6c54
2021-10-11 14:03:16.618  INFO 22471 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-10-11 14:03:16.647  INFO 22471 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-10-11 14:03:16.647  INFO 22471 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.53]
```

**ポートを変更したい場合は**、コマンドラインに以下の引数を付加して実行してください。

```
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8888
```

## サンプル説明

`/test1` と `/test2` のエンドポイントが定義しており、実装方法は異なりました同じ振る舞いをします。`/test1` では、プログラムによってサーキットブレーカーを構成した例、`/test2` は `@CircutBreaker` アノテーションで構成されています。

```java
    @Bean
    public Customizer<SpringRetryCircuitBreakerFactory> defaultCustomizer() {
        logger.info("defaultCustomizer");
        var p = new CircuitBreakerRetryPolicy(new SimpleRetryPolicy(2));
        p.setOpenTimeout(5000);
        p.setResetTimeout(10000);

        return factory -> factory
            .configure(builder -> builder.retryPolicy(p).build(), "myconfig1");
    }
```

##



```log
2021-10-11 16:47:51.152  INFO 9499 --- [nio-8080-exec-1] o.p.sample.waf.cb.sb.SampleController    : test2
2021-10-11 16:47:51.186 DEBUG 9499 --- [nio-8080-exec-1] s.r.i.StatefulRetryOperationsInterceptor : Executing proxied method in stateful retry: public java.lang.String org.pnop.sample.waf.cb.sb.SampleService.call2(int)(64e2a18e)
2021-10-11 16:47:51.208 TRACE 9499 --- [nio-8080-exec-1] o.s.retry.support.RetryTemplate          : RetryContext retrieved: [RetryContext: count=0, lastException=null, exhausted=false]
2021-10-11 16:47:51.208 TRACE 9499 --- [nio-8080-exec-1] o.s.r.policy.CircuitBreakerRetryPolicy   : Open: false
2021-10-11 16:47:51.208 DEBUG 9499 --- [nio-8080-exec-1] o.s.retry.support.RetryTemplate          : Retry: count=0
2021-10-11 16:47:51.221  INFO 9499 --- [nio-8080-exec-1] org.pnop.sample.waf.cb.sb.SampleService  : request : http://httpbin.org/status/500
2021-10-11 16:47:51.858 TRACE 9499 --- [nio-8080-exec-1] o.s.r.policy.CircuitBreakerRetryPolicy   : Open: false
2021-10-11 16:47:51.858 DEBUG 9499 --- [nio-8080-exec-1] o.s.retry.support.RetryTemplate          : Checking for rethrow: count=1
2021-10-11 16:47:51.858 ERROR 9499 --- [nio-8080-exec-1] org.pnop.sample.waf.cb.sb.SampleService  : Fallback for call invoked
2021-10-11 16:47:51.858 DEBUG 9499 --- [nio-8080-exec-1] s.r.i.StatefulRetryOperationsInterceptor : Exiting proxied method in stateful retry with result: (fallback)
2021-10-11 16:47:51.931  INFO 9499 --- [nio-8080-exec-2] o.p.sample.waf.cb.sb.SampleController    : test2
2021-10-11 16:47:51.931 DEBUG 9499 --- [nio-8080-exec-2] s.r.i.StatefulRetryOperationsInterceptor : Executing proxied method in stateful retry: public java.lang.String org.pnop.sample.waf.cb.sb.SampleService.call2(int)(10ab570)
2021-10-11 16:47:51.933 TRACE 9499 --- [nio-8080-exec-2] o.s.retry.support.RetryTemplate          : RetryContext retrieved: [RetryContext: count=1, lastException=org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 INTERNAL SERVER ERROR: [no body], exhausted=false]
2021-10-11 16:47:51.933 TRACE 9499 --- [nio-8080-exec-2] o.s.r.policy.CircuitBreakerRetryPolicy   : Open: false
2021-10-11 16:47:51.933 DEBUG 9499 --- [nio-8080-exec-2] o.s.retry.support.RetryTemplate          : Retry: count=1
2021-10-11 16:47:51.933  INFO 9499 --- [nio-8080-exec-2] org.pnop.sample.waf.cb.sb.SampleService  : request : http://httpbin.org/status/500
2021-10-11 16:47:52.145 TRACE 9499 --- [nio-8080-exec-2] o.s.r.policy.CircuitBreakerRetryPolicy   : Open: false
2021-10-11 16:47:52.146 DEBUG 9499 --- [nio-8080-exec-2] o.s.retry.support.RetryTemplate          : Checking for rethrow: count=2
2021-10-11 16:47:52.147 ERROR 9499 --- [nio-8080-exec-2] org.pnop.sample.waf.cb.sb.SampleService  : Fallback for call invoked
2021-10-11 16:47:52.147 DEBUG 9499 --- [nio-8080-exec-2] s.r.i.StatefulRetryOperationsInterceptor : Exiting proxied method in stateful retry with result: (fallback)
2021-10-11 16:47:52.158  INFO 9499 --- [nio-8080-exec-3] o.p.sample.waf.cb.sb.SampleController    : test2
2021-10-11 16:47:52.158 DEBUG 9499 --- [nio-8080-exec-3] s.r.i.StatefulRetryOperationsInterceptor : Executing proxied method in stateful retry: public java.lang.String org.pnop.sample.waf.cb.sb.SampleService.call2(int)(5aa297f)
2021-10-11 16:47:52.159 TRACE 9499 --- [nio-8080-exec-3] o.s.retry.support.RetryTemplate          : RetryContext retrieved: [RetryContext: count=2, lastException=org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 INTERNAL SERVER ERROR: [no body], exhausted=false]
2021-10-11 16:47:52.159 TRACE 9499 --- [nio-8080-exec-3] o.s.r.policy.CircuitBreakerRetryPolicy   : Open: false
2021-10-11 16:47:52.159 DEBUG 9499 --- [nio-8080-exec-3] o.s.retry.support.RetryTemplate          : Retry: count=2
2021-10-11 16:47:52.159  INFO 9499 --- [nio-8080-exec-3] org.pnop.sample.waf.cb.sb.SampleService  : request : http://httpbin.org/status/500
2021-10-11 16:47:52.362 TRACE 9499 --- [nio-8080-exec-3] o.s.r.policy.CircuitBreakerRetryPolicy   : Opening circuit
2021-10-11 16:47:52.362 DEBUG 9499 --- [nio-8080-exec-3] o.s.retry.support.RetryTemplate          : Checking for rethrow: count=3
2021-10-11 16:47:52.363 ERROR 9499 --- [nio-8080-exec-3] org.pnop.sample.waf.cb.sb.SampleService  : Fallback for call invoked
2021-10-11 16:47:52.363 DEBUG 9499 --- [nio-8080-exec-3] s.r.i.StatefulRetryOperationsInterceptor : Exiting proxied method in stateful retry with result: (fallback)
2021-10-11 16:47:52.375  INFO 9499 --- [nio-8080-exec-4] o.p.sample.waf.cb.sb.SampleController    : test2
2021-10-11 16:47:52.376 DEBUG 9499 --- [nio-8080-exec-4] s.r.i.StatefulRetryOperationsInterceptor : Executing proxied method in stateful retry: public java.lang.String org.pnop.sample.waf.cb.sb.SampleService.call2(int)(580ab561)
2021-10-11 16:47:52.377 TRACE 9499 --- [nio-8080-exec-4] o.s.retry.support.RetryTemplate          : RetryContext retrieved: [RetryContext: count=3, lastException=org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 INTERNAL SERVER ERROR: [no body], exhausted=false]
2021-10-11 16:47:52.377 ERROR 9499 --- [nio-8080-exec-4] org.pnop.sample.waf.cb.sb.SampleService  : Fallback for call invoked
2021-10-11 16:47:52.377 DEBUG 9499 --- [nio-8080-exec-4] s.r.i.StatefulRetryOperationsInterceptor : Exiting proxied method in stateful retry with result: (fallback)
2021-10-11 16:47:52.385  INFO 9499 --- [nio-8080-exec-5] o.p.sample.waf.cb.sb.SampleController    : test2
2021-10-11 16:47:52.385 DEBUG 9499 --- [nio-8080-exec-5] s.r.i.StatefulRetryOperationsInterceptor : Executing proxied method in stateful retry: public java.lang.String org.pnop.sample.waf.cb.sb.SampleService.call2(int)(662ffed8)
2021-10-11 16:47:52.385 TRACE 9499 --- [nio-8080-exec-5] o.s.retry.support.RetryTemplate          : RetryContext retrieved: [RetryContext: count=3, lastException=org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 INTERNAL SERVER ERROR: [no body], exhausted=false]
2021-10-11 16:47:52.385 ERROR 9499 --- [nio-8080-exec-5] org.pnop.sample.waf.cb.sb.SampleService  : Fallback for call invoked
2021-10-11 16:47:52.385 DEBUG 9499 --- [nio-8080-exec-5] s.r.i.StatefulRetryOperationsInterceptor : Exiting proxied method in stateful retry with result: (fallback)
```