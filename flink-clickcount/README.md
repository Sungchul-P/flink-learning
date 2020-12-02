# Flink Operations Playground

- 플링크의 자동 복원(Recovery) 기능과 자연스러운 스케일(Scale) 방법을 알아봅니다. (체크포인트, 세이브포인트 활용)

## Business Logic

- 6개의 페이지에 대해 1000개의 클릭 이벤트가 발생합니다.(Window 15초 간격)

## DataStream Architecture
```
Click Event Data Generator -> Kafka -> Click Event Count -> Kafka
```

## Run Streaming Application
- clickevent-generator : 클릭 이벤트를 지속적으로 생성합니다.
- client : 플링크 클러스터에 Job을 제출(Submit) 합니다. (이 컨테이너는 Job 제출 후, 종료)
- jobmanager & taskmanager : 플링크 클러스터를 구성합니다.
- kafka & zookeeper : input & output 토픽에 데이터가 쌓입니다.(consumer로 조회 가능)

```bash
docker-compose build
docker-compose up -d
docker-compose ps

                 Name                                Command               State                  Ports                
-----------------------------------------------------------------------------------------------------------------------
flink-clickcount_clickevent-generator_1   /docker-entrypoint.sh java ...   Up      6123/tcp, 8081/tcp                  
flink-clickcount_client_1                 /docker-entrypoint.sh flin ...   Exit 0                  
flink-clickcount_jobmanager_1             /docker-entrypoint.sh jobm ...   Up      6123/tcp, 0.0.0.0:8081->8081/tcp    
flink-clickcount_kafka_1                  start-kafka.sh                   Up      0.0.0.0:9094->9094/tcp              
flink-clickcount_taskmanager_1            /docker-entrypoint.sh task ...   Up      6123/tcp, 8081/tcp                  
flink-clickcount_zookeeper_1              /bin/sh -c /usr/sbin/sshd  ...   Up      2181/tcp, 22/tcp, 2888/tcp, 3888/tcp

```

## Kafka Topics
```bash
//input topic (1000 records/s)
docker-compose exec kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic input

{"timestamp":"01-01-1970 12:07:31:320","page":"/news"}
{"timestamp":"01-01-1970 12:07:31:335","page":"/help"}
{"timestamp":"01-01-1970 12:07:31:335","page":"/index"}
{"timestamp":"01-01-1970 12:07:31:335","page":"/shop"}
{"timestamp":"01-01-1970 12:07:31:335","page":"/jobs"}
{"timestamp":"01-01-1970 12:07:31:335","page":"/about"}
```

```bash
//output topic (24 records/min)
docker-compose exec kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic output

{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/help","count":1000}
{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/about","count":1000}
{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/shop","count":1000}
{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/jobs","count":1000}
{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/news","count":1000}
{"windowStart":"01-01-1970 12:06:30:000","windowEnd":"01-01-1970 12:06:45:000","page":"/index","count":1000}

```

## Flink CLI
### Listing Running Jobs
```
docker-compose run --no-deps client flink list

Creating flink-clickcount_client_run ... done
Waiting for response...
------------------ Running/Restarting Jobs -------------------
02.12.2020 09:19:14 : eeee2322724cc9da7542b2d9893a181b : Click Event Count (RUNNING)
--------------------------------------------------------------
No scheduled jobs.
```

