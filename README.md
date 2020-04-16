# Coupon System

## 주요 사용 라이브러리 및 프레임워크
* PlayFramework, Guice
* Kafka, DynamoDB, ElasticSearch, Memcached
 

## 개발 방향
coupon-simple 프로젝트에서 hibernate를 사용해서 심플한 구현을 생각했다면, 여기서는 대용량 서비스를 고려합니다.
대용량 시스템을 구성한다면 이런 방향으로 구성할 수 있다는 모습을 보여드리고, prototype 수준 정도만 구현했습니다.   

쿠폰 조회를 제외한 요청을 먼저 queue (kafka)에 넣고 이를 consuming 하면서 필요한 일을 처리합니다. 요청이 들어오면 memcached에서 정보를 조회 후 빠르게 validation 정도만 수행하고 이를 통과하면 kafka 에 저장합니다. 
그리고 kafka 에 저장된 값을 읽어서 뒤에서 해당 처리를 위한 비즈니스를 수행합니다.

주요 구성 요소
* PlayFramework: non-blocking 서버
* Kafka: 메시지 큐 역할로 Redis를 사용할 수도 있습니다. 쿠폰은 돈에 관련된거라 data persistence의 신뢰성에 점수를 더 줘서 kafka를 사용했습니다.
* DynamoDB: 쿠폰 생성, 발행, 사용 처리를 위한 메인 DB로 쿠폰 코드를 key로 하는 대용량 데이터 저장소 입니다. 가용성이 아주 높고 데이터 양이 늘어나도 write, read 성능 영향도가 낮습니다. 
* ElasticSearch: 각종 조건에 따라 조회를 위한 데이터 검색 시스템 입니다. Kafka에 저장된 메시지를 처리하면서 (적절히 가공해) ElasticSearch 쪽으로 데이터를 저장합니다. (ElasticSearch 대신 MongoDB를 사용해도 될 것 같습니다.)
* Memcached: Kafka에 저장하기 전에 validation을 위한 데이터 조회 등의 목적으로 사용합니다. 여기에 데이터가 없을 경우에는 DynamoDB에서 읽어옵니다.  

참고
* 사람(human)이 일반적인 형태로 쿠폰 관련된 일을 수행 한다고 가정했습니다. (즉 아주 짧은 시간안에 해당 쿠폰 관련된 요청이 들어오지 않음) 이 문제는 kafka에 쓰기 전에 memcache에 먼저 쓴 후 kafka에 데이터를 저장하는 형태로 해결할 수 있습니다.  
* 대량의 트래픽을 고려하라고 해서 쿠폰발행과 쿠폰사용 등에 대해 각각을 좀 더 유연하게 고려할 수 있도록 별도의 topic으로 구성했습니다. 
* 만약 쿠폰 이벤트 종류에 따른 운영 편의성보다 타이밍 이슈가 중요하다면, 쿠폰 업데이트 관련 이벤트를 하나의 topic으로 구성하고 각 이벤트의 타입을 명시적으로 표현할 수 있습니다. 이렇게 구성하면, 쿠폰 코드를 partition key로 사용하기 때문에 타이밍 문제에 덜 민감하게되고 특정 topic이 밀리면서 발생하는 문제를 피할 수 있습니다.   
 

## 환경 설정

```bash
# elasticsearch 받기
docker pull docker.elastic.co/elasticsearch/elasticsearch:6.8.8

# elasticsearch 실행
docker run -p 9200:9200 -p 9300:9300 -e “discovery.type=single-node” docker.elastic.co/elasticsearch/elasticsearch:6.8.8

# index 생성
PUT /coupon
{
  "mappings": {
  	"_doc" : {
	    "properties": {
	      "couponCode":    { "type": "keyword" },  
	      "used":  { "type": "boolean"  }, 
	      "issued":   { "type": "boolean"  },    
	      "validFrom":   { "type": "date"  },  
	      "expireAt":   { "type": "date"  } ,
	      "userId":   { "type": "keyword"  } ,
	      "issuedAt":   { "type": "date"  }  ,
	      "usedAt":   { "type": "date"  } ,
	      "createdAt":   { "type": "date"  } ,
	      "modifiedAt":   { "type": "date"  } 
	    }
	  }
  }
}
```

```bash
# dynamodb local 받기
docker pull amazon/dynamodb-local

# dynamodb local 실행
docker run -p 8000:8000 amazon/dynamodb-local

# 테이블 생성
aws dynamodb create-table \
    --table-name Coupon \
    --attribute-definitions \
        AttributeName=couponCode,AttributeType=S \
    --key-schema \
        AttributeName=couponCode,KeyType=HASH \
	    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
		--endpoint-url http://localhost:8000

aws dynamodb list-tables --endpoint-url http://localhost:8000
```

```
# memcached 실행
```

```bash
## kafka 받은 뒤 압축 해제 후

# zookeeper 실행
bin/zookeeper-server-start.sh config/zookeeper.properties

# kafka 실행
bin/kafka-server-start.sh config/server.properties
```


### 웹 서버 빌드 및 실행
```bash
# 빌드
sbt dist

# target/universal/coupon-1.0.0.zip 압축 해제
cd target/universal
unzip coupon-1.0.0

# 실행 디렉토리 이동
cd coupon-1.0.0


# 웹 어플리케이션 실행 
./bin/coupon 

# consummer application 실행
java -cp $(echo lib/*.jar | tr ' ' ':') com.github.prorhap.coupon.play.app.CouponApplication

# cvs importer 실행
java -cp $(echo lib/*.jar | tr ' ' ':') com.github.prorhap.coupon.play.app.CvsImporter /Users/rhapsody/Dropbox/Project/coupon/conf/data.cvs com.github.prorhap.coupon.play.app.CouponApplication

```


For best results, start the gatling load test up on another machine so you do not have contending resources.  You can edit the [Gatling simulation](http://gatling.io/docs/2.3/general/simulation_structure.html#simulation-structure), and change the numbers as appropriate.

Once the test completes, you'll see an HTML file containing the load test chart, for example:

```bash
 ./play-java-rest-api-example/target/gatling/gatlingspec-1472579540405/index.html
```

That will contain your load test results.




## Best Practices for Blocking API

If you look at the controller: [PostController](app/v1/post/PostController.java)
then you can see that when calling out to a blocking API like JDBC, you should put it behind an asynchronous boundary -- in practice, this means using the CompletionStage API to make sure that you're not blocking the rendering thread while the database call is going on in the background.



There is more detail in <https://www.playframework.com/documentation/latest/ThreadPools> -- notably, you can always bump up the number of threads in the rendering thread pool rather than do this -- but it gives you an idea of best practices.

## Load Testing

The best way to see what Play can do is to run a load test.  We've included Gatling in this test project for integrated load testing.

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/latest/Deploying) and running the play script:s
