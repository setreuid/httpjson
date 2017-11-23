# httpjson
Java module for parse http json more easy.

Url과 파라미터를 넘기고 콜백 함수로 Json 결과를 받는 모듈입니다.

현재 Post 방식으로 String 형만 가능합니다. (2017-11-23)

## Usage
##### POST JSON Parsing without params
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
        system.out.println("JSON: " + json.toString());
        
        String keyValue       = json.getString("key");
        String floatTestValue = json.getFloat("float-test");
        
        system.out.println("key: " + keyValue);
        system.out.println("float-test: " + floatTestValue);
    }
}).post();
```

##### POST JSON Parsing with params
파라미터 포함하여 POST JSON 파싱
```java
String url = "http://echo.jsontest.com/float-test/3.14/key/value";

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
        system.out.println("JSON: " + json.toString());
        
        String value = json.getString("test-key");
        system.out.println("test-key: " + value);
    }
}).post();
```

## Todo
- [X] GET 지원
- [ ] PUT, PATCH, DELETE 추가
- [ ] 파라미터 HashMap Bytes 형 추가
- [ ] HTTP 예외처리
- [ ] 자원 최적화