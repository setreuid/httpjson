
# httpjson
- (en) Java library for parse http json more easy.
- (ko) Url과 파라미터를 넘기고 콜백 함수로 Json 결과를 받는 라이브러리 입니다.

[![](https://jitpack.io/v/setreuid/httpjson.svg)](https://jitpack.io/#setreuid/httpjson)



## Setup

### Gradle

Edit `root/app/build.gradle` like below.

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.setreuid:httpjson:1.0.6'
}
```



## Usage

#### Imports

```java
import java.util.HashMap;

import cc.udp.httpjson.HttpJson;
import cc.udp.httpjson.HttpJsonObject;
import cc.udp.httpjson.HttpJsonTask;  
import cc.udp.httpjson.HttpBytesTask;
```

***

#### JSON Parsing without params
> 파라미터 없이 JSON 파싱
```java
String url = "https://jsonplaceholder.typicode.com/posts/1";

new HttpJson(url, null, new HttpJsonTask() {
    @Override
    public void done(HttpJsonObject json)
    {
        if (json == null)
        {
            return;
        }
        
        // {
		//   "userId": 1,
		//   "id": 1,
		//   "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
		//   "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
		// }
        
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String id    = json.getInt("id");
        String title = json.getString("title");
        
        System.out.println("id: " + id);
        System.out.println("title: " + title);
    }
}).get();
```
###### lambda
```java
new HttpJson("https://jsonplaceholder.typicode.com/posts/1")
    .then((HttpJsonObject json) -> {
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String id    = json.getInt("id");
        String title = json.getString("title");
        
        System.out.println("id: " + id);
        System.out.println("title: " + title);
    })
    .except((e) -> {
        System.out.println(e.getMessage());
    })
    .get();
```

***

#### Access JSON value with method chaining
> 메서드 체인을 이용한 JSON 값 가져오기
```java
String url = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA";

new HttpJson(url, null, new HttpJsonTask() {
    @Override
    public void done(HttpJsonObject json)
    {
        if (json == null)
        {
            return;
        }
        
        // {
        //     "results" : [
        //     {
        //         "address_components" : [
        //         {
        //            "long_name"  : "Google Building 41",
        //            "short_name" : "Google Bldg 41",
        //            "types"      : [ "premise" ]
        //         },
        //         ...
        //     ],
        //     "status" : "OK"
        // }
        
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String status          = json.getString("status");
        String addressLongName = json.getArray("results").get(0)
                                     .getArray("address_components").get(0)
                                     .getString("long_name");
        
        System.out.println("status: " + status);
        System.out.println("addressLongName: " + addressLongName);
    }
}).get();
```

***

#### POST JSON Parsing with params
> 파라미터 포함하여 POST JSON 파싱
```java
String url = "http://p.udp.cc/jsonPostTest.php";

HashMap<String, String> params = new HashMap<String, String>();
params.put("test-key", "A123456789B");

new HttpJson(url, params, new HttpJsonTask() {
    @Override
    public void done(HttpJsonObject json)
    {
        if (json == null)
        {
            return;
        }
        
        // {
        //  "test-key": "A123456789B"
        // }
        
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        System.out.println("test-key: " + value);
    }
}).post();
```
###### lambda
```java
new HttpJson("http://p.udp.cc/jsonPostTest.php")
    .addField("test-key", "A123456789B")
    .then((HttpJsonObject json) -> {
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        System.out.println("test-key: " + value);
    })
    .except((e) -> {
        System.out.println(e.getMessage());
    })
    .post();
```


***

#### GET JSON Parsing with params
> 파라미터 포함하여 GET JSON 파싱
```java
String url = "http://p.udp.cc/jsonGetTest.php";

// http://p.udp.cc/jsonGetTest.php?test-key=A123456789B
HashMap<String, String> params = new HashMap<String, String>();
params.put("test-key", "A123456789B");

new HttpJson(url, params, new HttpJsonTask() {
    @Override
    public void done(HttpJsonObject json)
    {
        if (json == null)
        {
            return;
        }
        
        // {
        //  "test-key": "A123456789B"
        // }
        
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        System.out.println("test-key: " + value);
    }
}).get();
```
###### lambda
```java
new HttpJson("http://p.udp.cc/jsonGetTest.php")
    .addField("test-key", "A123456789B")
    .then((HttpJsonObject json) -> {
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        System.out.println("test-key: " + value);
    })
    .except((e) -> {
        System.out.println(e.getMessage());
    })
    .get();
```

***

#### Just Call without params
> 파라미터 포함하지 않고 단순 호출
```java
String url = "http://p.udp.cc/jsonPostTest.php";
new HttpJson(url, null, null).post();
```

***

#### Just Call with params
> 파라미터 포함하여 단순 호출
```java
String url = "http://p.udp.cc/jsonPostTest.php";

HashMap<String, String> params = new HashMap<String, String>();
params.put("test-key", "A123456789B");

new HttpJson(url, params, null).post();
```

***

#### With headers
> 헤더 추가하기

* (en) If `Content-Type` is specified as` application/json`, parameters are sent in JSON format.
* (en)  Unless otherwise specified, `application/x-www-form-urlencoded` is the default value of`Content-Type`.
* (ko) `Content-Type`을 `application/json`으로 지정하면 JSON 형태로 파라미터를 전송합니다.
* (ko) 특별히 지정하지 않을때에는 `application/x-www-form-urlencoded`이 `Content-Type`의 기본값입니다.

```java
new HttpJson("http://p.udp.cc/jsonPostTest.php")
    .addHeader("Content-Type", "application/json")
    .addField("test-key", "A123456789B")
    .then((HttpJsonObject json) -> {
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        System.out.println("test-key: " + value);
    })
    .except((e) -> {
        System.out.println(e.getMessage());
    })
    .post();
```

***

#### Binary
> 바이너리 값 가져오기

```java
public static String byteArrayToHexString(byte[] bytes){  
    StringBuilder sb = new StringBuilder();  
	for(byte b : bytes){  
        sb.append(String.format("%02X", b&0xff));  
  }  
    return sb.toString();  
}
```
```java
new HttpJson("https://s.yimg.com/rz/p/yahoo_frontpage_en-US_s_f_p_205x58_frontpage_2x.png")
    .then((byte[] binary) -> {
        System.out.println("binary: " + byteArrayToHexString(binary));
        // binary: 89504E470D0A1A0A0000000D494844520000019A0000007408030...
    })
    .except((e) -> {
        System.out.println(e.getMessage());
    })
    .get();
```



## Class

### HttpJsonObject

| Return type                 | Method name | Parameters |
| --------------------------- | ----------- | ---------- |
| HttpJsonObject              | getObject   | String key |
| ArrayList\<HttpJsonObject\> | getArray    | String key |
| String                      | getString   | String key |
| int                         | getInt      | String key |
| float                       | getFloat    | String key |
| double                      | getDouble   | String key |
| byte[]                      | getBytes    | String key |
| Object                      | get         | String key |



## Todo
- [X] GET
- [X] POST
- [X] Return 결과를 byte[]로 받기
- [ ] PUT, PATCH, DELETE
- [ ] byte[]형 필드 추가