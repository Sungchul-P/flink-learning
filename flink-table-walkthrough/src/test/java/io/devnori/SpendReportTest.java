package io.devnori;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.types.Row;
import org.junit.Test;

import java.time.LocalDateTime;

import static io.devnori.SpendReport.report;

public class SpendReportTest {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2020, 1, 1, 0, 0);

    @Test
    public void testReport() {
        EnvironmentSettings settings = EnvironmentSettings.newInstance().build();
        TableEnvironment tEnv = TableEnvironment.create(settings);

        Table transactions =
            tEnv.fromValues(
                DataTypes.ROW(
                    DataTypes.FIELD("account_id", DataTypes.BIGINT()),
                    DataTypes.FIELD("amount", DataTypes.BIGINT()),
                    DataTypes.FIELD("transaction_time", DataTypes.TIMESTAMP(3))),
                Row.of(1, 188, DATE_TIME.plusMinutes(12)),
                Row.of(2, 374, DATE_TIME.plusMinutes(47)),
                Row.of(3, 112, DATE_TIME.plusMinutes(36)),
                Row.of(4, 478, DATE_TIME.plusMinutes(3)),
                Row.of(5, 208, DATE_TIME.plusMinutes(8)),
                Row.of(1, 379, DATE_TIME.plusMinutes(53)),
                Row.of(2, 351, DATE_TIME.plusMinutes(32)),
                Row.of(3, 320, DATE_TIME.plusMinutes(31)),
                Row.of(4, 259, DATE_TIME.plusMinutes(19)),
                Row.of(5, 273, DATE_TIME.plusMinutes(42)));

    }

}
