# OpenFeign Normalized Logger

![](https://img.shields.io/maven-central/v/me.bvn13.openfeign.logger/feign-normalized-logger)

Standard OpenFeign logger provides the only approach to log communications - 
it logs every header in separated log entries, the body goes into another log entry.

It is very inconvenient to deal with such logs in production especially in multithreaded systems.

This 'Normalized Logger' is intended to combine all log entries related to one request-reply 
communication into one log entry.

## Old bad Logger

All parts are separated from each other:
1) Request:
   1) request headers - every header is put into separated entry
   2) request body - at separated entry
2) Response:
   1) response headers - separately
   2) response body - at separated entry as well

```
2022-07-25 14:12:43.572 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] ---> POST https://example.com/api/v1/login HTTP/1.1
2022-07-25 14:12:43.573 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] Content-Length: 23
2022-07-25 14:12:43.573 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] Content-Type: application/json
2022-07-25 14:12:43.574 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4464.5 Safari/537.36
2022-07-25 14:12:43.575 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] 
2022-07-25 14:12:43.576 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] {"login":"123456789"}
2022-07-25 14:12:43.576 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] ---> END HTTP (21-byte body)
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] <--- UNKNOWN 200  (324ms)
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] cache-control: no-cache
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] cf-cache-status: DYNAMIC
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] cf-ray: 730476518ea441ce-AMS
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] content-type: application/json
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] date: Mon, 25 Jul 2022 11:12:43 GMT
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] expect-ct: max-age=604800, report-uri="https://report-uri.cloudflare.com/cdn-cgi/beacon/expect-ct"
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] feature-policy: accelerometer 'none'; camera 'none'; geolocation 'none'; gyroscope 'none'; magnetometer 'none'; microphone 'none'; payment *; usb 'none'
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] referrer-policy: strict-origin-when-cross-origin
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] server: cloudflare
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] set-cookie: ACCESS_TOKEN=eyJh9uygCUMA659bAZ54SHpSNy_KFXQ; Max-Age=1800; Domain=.example.com; Path=/; Secure; SameSite=None
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] strict-transport-security: max-age=31536000
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] x-content-type-options: nosniff
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] x-frame-options: sameorigin
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] x-xss-protection: 1; mode=block
2022-07-25 14:12:43.901 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] 
2022-07-25 14:12:43.902 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] {"status":{"code":"OK","message":"OK"},"body":{"id":"20826"}}
2022-07-25 14:12:43.902 DEBUG 1032530 --- [Executor] feign.Logger  : [AuthApi#login] <--- END HTTP (61-byte body)
```

## New Normalized Logger

The whole communication (request and response parts) is combined into one log entry.

```
2022-07-25 14:16:06.217  INFO 1057053 --- [Executor] me.bvn13.openfeign.logger.NormalizedFeignLogger  : normalized feign request {AuthApi#login(LoginRequestDto)=[AuthApi#login] }: [
---> POST https://example.com/api/v1/login HTTP/1.1
Content-Length: 23
Content-Type: application/json
user-agent: bvn13 | Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4464.5 Safari/537.36

{"phone":"123456789"}
---> END HTTP (21-byte body)
] has response [
<--- UNKNOWN 200  (411ms)
cache-control: no-cache
cf-cache-status: DYNAMIC
cf-ray: 73047b41ffd2fa4c-AMS
content-type: application/json
date: Mon, 25 Jul 2022 11:16:06 GMT
expect-ct: max-age=604800, report-uri="https://report-uri.cloudflare.com/cdn-cgi/beacon/expect-ct"
feature-policy: accelerometer 'none'; camera 'none'; geolocation 'none'; gyroscope 'none'; magnetometer 'none'; microphone 'none'; payment *; usb 'none'
referrer-policy: strict-origin-when-cross-origin
server: cloudflare
set-cookie: ACCESS_TOKEN=eyJhboft6rzD6Be16dXY5lgQNCzOZNFe4ra_NDIdmXlXi19hlvaQ; Max-Age=1800; Domain=.example.com; Path=/; Secure; SameSite=None
strict-transport-security: max-age=31536000
x-content-type-options: nosniff
x-frame-options: sameorigin
x-xss-protection: 1; mode=block

{"status":{"code":"OK","message":"OK"},"body":{"id":"20826"}}
<--- END HTTP (61-byte body)
]
```

# How to use

In order to use Normalized Logger into the application they must the following.

## 0) Check the latest version

at [Maven Central Repo](https://repo1.maven.org/maven2/me/bvn13/openfeign/logger)

## 1) Add dependency

for Maven

```xml
<dependency>
    <groupId>me.bvn13.openfeign.logger</groupId>
    <artifactId>feign-normalized-logger</artifactId>
    <version>0.1.4</version>
</dependency>
```

for Gradle

```groovy
implementation 'me.bvn13.openfeign.logger:feign-normalized-logger:0.1.4'
```

## 2) Create Feign configuration and enable logger + specify FULL logging level

```java
import feign.Logger;

public class MyFeignConfig {
    
    @Bean
    public Logger logger() {
        return new NormalizedFeignLogger();
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }
    
}
```

### 3) Use this configuration into `@FeignClient` objects

```java
@FeignClient(name = "auth", configuration = MyFeignConfig.class)
public interface AuthApi {
/*...methods...*/
}
```

### 4) Adjust DEBUG level for Normalized Logger

for Slf4J + Logback

```yaml
logging:
  level:
    me.bvn13.openfeign.logger.NormalizedFeignLogger: INFO
```
