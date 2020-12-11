# Flink Learning

- 다양한 Flink 예제를 하나의 Repo로 관리합니다.
- 새로운 Flink Application 개발 시, 예제를 참고하여 빠르게 구현 하고자 합니다.

## Modules
### Maven 기본 구조로 Module 추가

```
[ Maven Default ]
mvn archetype:generate -DgroupId=io.devnori  -DartifactId=flink-frauddetection

[ Flink QuickStart ]
mvn archetype:generate                               \
  -DarchetypeGroupId=org.apache.flink              \
  -DarchetypeArtifactId=flink-quickstart-java      \
  -DarchetypeVersion=1.11.2
```

### 현재 추가된 Module 목록
- flink-frauddetection : Flink 공식문서의 DataStream API 예제(Key + State + Time)
- flink-table-walkthrough : Table API를 이용하여 지출 보고서를 발행하는 예제(Kafka + MySQL)
- kafka-data-generator : Transaction 데이터를 지속적으로 생성하는 예제(table-walkthrough 연계)
- flink-clickcount : 클릭 이벤트 데이터를 지속적으로 생성하고, 체크포인트로 복구 및 스케일링 시도할 수 있는 예제
- flink-training : 가상의 뉴욕시 택시 데이터를 생성하고 처리 및 분석까지 맛볼 수 있는 예제 

## Reference

- [Flink Official Document v1.11](https://ci.apache.org/projects/flink/flink-docs-release-1.11)
- [zhisheng17/flink-learning](https://github.com/zhisheng17/flink-learning)