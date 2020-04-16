# Coupon System

## 주요 사용 라이브러리 및 프레임워크
* PlayFramework, Guice
* Kafka, DynamoDB, ElasticSearch, Memcached
 

## 개발 방향 및 문제 해결 전략
coupon-simple 프로젝트에서 hibernate를 사용해서 심플한 구현을 했다면, 이곳에서는 대용량 처리를 고민한 구조를 보여드리고 prototype 수준의 정도만 구현했습니다. 아키텍처 설계 시 몇 가지 가정을 하고 구현을 했고 이에 대한 사항은 아래에서 이야기합니다. (아키텍처는 trade-off를 생각해야하고 비즈니스의 완성도를 생각하면 너무 복잡해져서 단순하게 API의 기능정도 제공한다는 생각으로 설계/구현했습니다.)

<img width="550" alt="coupon1" src="https://user-images.githubusercontent.com/650390/79407351-322fa900-7fd4-11ea-9e7c-8772f0c35406.png">
_그림1. 기본 시스템 구성_

기본 방향
* 대부분의 쿠폰 관련 비즈니스는 (1) 요청을 받아 유효성 체크 등의 간단한 로직이 있는 REST API 서버 (2) 고성능 메시지 큐 용도의 Kafka (3) 메인 비즈니스 처리를 위한 Consumer 어플리케이션 (4) 대량의 데이터를 안정적으로 저장/처리하기 위한 key, value 저장소인 DynamoDB, 그리고 (1) REST API 서버에서 빠르 처리를 돕기 위한 Memcache를 통해서 수행합니다.
* 각종 조건에 대한 조회/검색은 별도의 조회용 데이터 저장소를 이용해서 수행합니다. 여기서는 ElasticSearch를 사용했고 MongoDB도 사용에 적합해보입니다.
* Kafka에 저장하기 전까지 비즈니스는 최대한 가볍게하고 (대부부 validation), 쿠폰 처리에 대하 메인 비즈니스는 Consuming 하면서 수행합니다. 처리 량/시간에 따라 Consumer를 늘리고, 필요시 재처리 합니다.

주요 구성 요소
* PlayFramework: 많은 요청 처리에 조금 더 적합한 non-blocking REST API 서버
* Kafka: 메시지 큐 역할로 Redis를 사용할 수도 있습니다. 쿠폰 처리는 돈에 관계된 부분이라 Data Persistence에 점수를 더 줘서 Kafka를 사용했습니다.
* DynamoDB: 쿠폰 생성, 발행, 사용 처리를 위한 메인 DB로 쿠폰 코드를 key로 하는 대용량 데이터 저장소 입니다. 빠르고 가용서 높은, 데이터가 많이 늘어나도 write, read 성능 영향도가 낮은 KeyValue 저장소 입니다.
* ElasticSearch: 각종 조건에 따른 조회를 위한 조회/검색 시스템 입니다. Kafka에 저장된 메시지를 처리하면서 (필요하다면 적절히 가공해) ElasticSearch 쪽으로 데이터를 저장합니다. MongoDB도 이 역할에 적합합니다.
* Memcached: Kafka에 저장하기 전에 validation을 위한 데이터 조회 등의 목적으로 사용합니다. 여기에 데이터가 없을 경우에는 DynamoDB에서 읽어옵니다.  

참고
* 사람이 일반적인 형태로 쿠폰 관련된 일을 수행 한다고 가정했습니다. 요청을 consuming 해서 처리하기 전에, 문제가 될 만한 요청이 들어오는 것을 막으 필요가 있다면 Memcached 에도 해당 사항을 함께 업데이트 할 수 있습니다.
* 요구사항에 기술된 대용량 트래픽이라면, 쿠폰발행/쿠폰사용 등이 각각 특정 시간에 (순간적으로) 집중되는 상황이라고 가정해서, 이를 좀 더 유연하게 운영 할 수 있도록  쿠폰발행/쿠폰사용 등의 요청 타입을 각각 별도의 topic으로 구성했습니다. - topic 별로 partition을 조정하고 consumer를 늘려서 backpresure 가 발생하지 않도록 
* 만약 쿠폰 이벤트 종류에 따라 topic으 나눠 운영하는 것 보다 타이밍 이슈가 중요하다면, 쿠폰 업데이트 관련 이벤트를 하나의 topic으로 구성하고 각 이벤트의 타입을 명시적으로 표현해서 메시지르 처리할 수 있습니다. 이렇게 구성하면, 쿠폰 코드를 partition key로 사용하기 때문에 쿠폰 코드가 같은 이벤트에 대해서는 처리 순서가 보장되기 때문에, 타이밍 문제에 덜 민감하게 되고 또한 특정 topic이 밀리면서 발생하는 문제(예를들어 쿠폰 발급은 많이 지연되면서 쿠폰 취소를 먼저 처리하 수 있는 상황 등)를 피할 수 있습니다.   
* CVS Import는 쿠폰을 발행하는 상황이라고 가정했습니다. 
 
 
<img width="800" alt="coupon2" src="https://user-images.githubusercontent.com/650390/79407316-280daa80-7fd4-11ea-9c45-203758f6c440.png">
_그림2. Big Picture_

## 환경 설정
아래에 대한 환경을 준비한다.
* elasticsearch 설치 및 실행 후 index 생성
* dynamodb local 버전 받아서 실행 후 table 생성
* memcached 설치 후 실행
* kafka 설치 후 실행

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
Dynamodb Local 버전은 프로세스를 내리면 데이터가 날아감 

```
# memcached 설치 후 실행
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
java -cp $(echo lib/*.jar | tr ' ' ':') com.github.prorhap.coupon.play.app.CvsImporter <file-path> 


```
cvs importer의 cvs 파일 포멧은 couponCode, validFrom, expireAt 이고, 예를들면 아래와 같은 형태이다.
``` 
26DWGQ9JRO20N55I8ZC, 20200401, 20200420
1W5E5OFG0B16B9CCBG3, 20200401, 20200420
```
