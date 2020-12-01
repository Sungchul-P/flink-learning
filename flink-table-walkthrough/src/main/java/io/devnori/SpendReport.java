package io.devnori;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.expressions.TimeIntervalUnit;

import static org.apache.flink.table.api.Expressions.*;

public class SpendReport {

    /**
     * 비즈니스 로직 : 하루 중 각 시간 동안 각 계정의 총 지출을 조회한다.
     * 방법1 : 내장 함수(Built-in Function) 이용
     * 방법2 : 사용자 정의 함수(User-Defined Function)로 확장하여 구현
     * 방법3 : Window로 시간 기반 그룹화
     *
     * @param transactions
     * @return
     */
    public static Table report(Table transactions) {
        return transactions
//            .select(
//                $("account_id"),
//                $("transaction_time").floor(TimeIntervalUnit.HOUR).as("log_ts"), // Built-in
//                call(MyFloor.class, $("transaction_time")).as("log_ts"), // User-Defined
//                $("amount"))
            .window(Tumble.over(lit(1).hour()).on($("transaction_time")).as("log_ts")) // Tumble Window
            .groupBy($("account_id"), $("log_ts"))
            .select(
                $("account_id"),
//                $("log_ts"),
                $("log_ts").start().as("log_ts"),
                $("amount").sum().as("amount"));
    }

    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettings.newInstance().build();
        TableEnvironment tEnv = TableEnvironment.create(settings);

        tEnv.executeSql("CREATE TABLE transactions (" +
                "account_id BIGINT," +
                "amount     BIGINT," +
                "transaction_time TIMESTAMP(3)," +
                "WATERMARK FOR transaction_time AS transaction_time - INTERVAL '5' SECOND" +
                ") WITH (" +
                "'connector'    = 'kafka'," +
                "'topic'        = 'transactions'," +
                "'properties.bootstrap.servers' = 'kafka:9092'," +
                "'format'       = 'csv'" +
                ")");

        tEnv.executeSql("CREATE TABLE spend_report (" +
                "account_id BIGINT," +
                "log_ts     TIMESTAMP(3)," +
                "amount     BIGINT," +
                "PRIMARY KEY (account_id, log_ts) NOT ENFORCED" +
                ") WITH (" +
                "'connector'    = 'jdbc'," +
                "'url'          = 'jdbc:mysql://mysql:3306/sql-demo'," +
                "'table-name'   = 'spend_report'," +
                "'driver'       = 'com.mysql.jdbc.Driver'," +
                "'username'     = 'sql-demo'," +
                "'password'     = 'demo-sql'" +
                ")");

        Table transactions = tEnv.from("transactions");
        report(transactions).executeInsert("spend_report");
    }
}
