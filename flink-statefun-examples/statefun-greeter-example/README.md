
# Greeter Example

Stateful Function을 실행하여 카프카 Ingress의 요청을 수락한 다음 카프카 Egress에 인사말을 보내 응답하는 간단한 예제입니다. 
Ingress, 특정 함수로 메시지 라우팅, 함수 상태 처리 및 Egress로 메시지 전송과 같은 Stateful Function 애플리케이션의 기본 구성 요소를 체험할 수 있습니다.

## 예제 실행

### 애플리케이션 빌드

Dockerfile에서 target 디렉터리의 JAR 파일이 필요하므로 빌드가 선행되어야 합니다.

```bash
mvn clean package
```

### 도커 이미지 빌드

```bash
docker-compose build
docker-compose up
```

### 카프카 메시지 확인

'names' 토픽에 메시지를 전송하는 프로듀서와 'greetings' 토픽에 전송된 메시지를 수신하는 컨슈머를 두 개의 터미널에 각각 띄워 결과를 확인합니다.

```bash
docker-compose exec kafka-broker kafka-console-producer.sh \
     --broker-list localhost:9092 \
     --topic names
```
```bash
docker-compose exec kafka-broker kafka-console-consumer.sh \
     --bootstrap-server localhost:9092 \
     --isolation-level read_committed \
     --from-beginning \
     --topic greetings
```

같은 이름을 여러번 입력하면 다음과 같이 메시지가 변경됩니다.  
중간에 다른 이름을 섞어도 기존의 상태가 유지되는 것을 확인할 수 있습니다.

```
Hello Devnori ! 😎
Hello again Devnori ! 🤗
Third time is a charm! Devnori! 🥳
Happy to see you once again Devnori ! 😲
Hello at the 5-th time Devnori 🙌
Hello Sean ! 😎
Hello at the 6-th time Devnori 🙌
```

### Protocol Buffer(Optional)

빌드할 때 컴파일 되어 생성되지만, 수동으로 생성해서 보고 싶으면 아래 명령을 사용하면 됩니다.

```bash
protoc -I=src/main/protobuf --java_out=src/main/java src/main/protobuf/greeter.proto
```