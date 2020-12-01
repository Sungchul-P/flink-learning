# Real Time Reporting with the Table API

- 플링크(Flink)의 Table API 를 사용하여 실시간 보고서를 발행하는 방법을 알아봅니다.

## Business Logic

- 하루 중 각 시간 동안 각 계정(accountId)의 총 지출(amount)을 조회합니다.

## Architecture
```
Transaction-Data Generator -> Kafka -> Flink Job -> MySQL <- Grafana Visualization
```

## Run Streaming Application
- Data Generator와 Flink Job(SpendReport)은 이미지를 빌드해서 사용합니다.
- 아래 docker-compose 명령으로 빌드하고, Architecture를 local에 구성할 수 있습니다.

```bash
docker-compose build
docker-compose up -d
docker-compose ps

                  Name                                Command               State                         Ports                       
--------------------------------------------------------------------------------------------------------------------------------------
flink-table-walkthrough_data-generator_1   /docker-entrypoint.sh            Up                                                        
flink-table-walkthrough_grafana_1          /run.sh                          Up      0.0.0.0:3000->3000/tcp                            
flink-table-walkthrough_jobmanager_1       /docker-entrypoint.sh stan ...   Up      6123/tcp, 0.0.0.0:8082->8081/tcp                  
flink-table-walkthrough_kafka_1            start-kafka.sh                   Up      0.0.0.0:9092->9092/tcp                            
flink-table-walkthrough_mysql_1            docker-entrypoint.sh --def ...   Up      3306/tcp, 33060/tcp                               
flink-table-walkthrough_taskmanager_1      /docker-entrypoint.sh task ...   Up      6121/tcp, 6122/tcp, 6123/tcp, 8081/tcp            
flink-table-walkthrough_zookeeper_1        /bin/sh -c /usr/sbin/sshd  ...   Up      0.0.0.0:2181->2181/tcp, 22/tcp, 2888/tcp, 3888/tcp
```

- 모두 정상적으로 실행 되면, [Flink 대시보드](http://localhost:8082)에서 Flink Job에 상태를 확인할 수 있습니다.
- MySQL에 접속하는 방법은 다음과 같은 명령을 사용하면 됩니다.

```bash
docker-compose exec mysql mysql -Dsql-demo -usql-demo -pdemo-sql

mysql> use sql-demo;

mysql> select count(*) from spend_report;
+----------+
| count(*) |
+----------+
|     1609 |
+----------+
1 row in set (0.01 sec)
```
- MySQL에 쌓이는 지출 정보는 [Grafana](http://localhost:3000/d/FOe0PbmGk/walkthrough?viewPanel=2&orgId=1&refresh=5s) 에서 계정별 누적 금액을 실시간으로 볼 수 있습니다.
 
![grafana](./grafana.png)