# httpjson
Java library for parse http json more easy.

Url과 파라미터를 넘기고 콜백 함수로 Json 결과를 받는 라이브러리 입니다.

현재 Post 방식으로 String 형만 가능합니다. (2017-11-23)



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
```


#### POST JSON Parsing without params
파라미터 없이 POST JSON 파싱
```java
String url = "http://echo.jsontest.com/float-test/3.14/key/value";

new HttpJson(url, params, new HttpJsonTask() {
    @Override
    public void done(HttpJsonObject json)
    {
        if (json == null)
        {
            return;
        }
        
        // {
        //  "key": "value",
        //  "float-test": "3.14"
        // }
        
        // Check json string
        System.out.println("JSON: " + json.toString());
        
        String keyValue       = json.getString("key");
        String floatTestValue = json.getFloat("float-test");
        
        System.out.println("key: " + keyValue);
        System.out.println("float-test: " + floatTestValue);
    }
}).post();
```

#### Access JSON value with method chaining
메서드 체인을 이용한 JSON 값 가져오기
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

#### POST JSON Parsing with params
파라미터 포함하여 POST JSON 파싱
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

#### GET JSON Parsing with params
파라미터 포함하여 POST JSON 파싱
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

#### Just Call without params
파라미터 포함하지 않고 단순 호출
```java
String url = "http://p.udp.cc/jsonPostTest.php";
new HttpJson(url, null, null).post();
```

#### Just Call with params
파라미터 포함하여 단순 호출
```java
String url = "http://p.udp.cc/jsonPostTest.php";

HashMap<String, String> params = new HashMap<String, String>();
params.put("test-key", "A123456789B");

new HttpJson(url, params, null).post();
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
- [X] GET 지원
- [ ] PUT, PATCH, DELETE 추가
- [ ] 파라미터 HashMap Bytes 형 추가
- [ ] HTTP 예외처리
- [ ] 자원 최적화