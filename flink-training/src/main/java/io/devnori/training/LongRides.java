/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.devnori.training;

import io.devnori.training.datatypes.TaxiRide;
import io.devnori.training.sources.TaxiRideGenerator;
import io.devnori.training.utils.ExerciseBase;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 승차 후, 2시간 동안 END 이벤트가 발생하지 않은 TaxiRide 이벤트를 내보냅니다.
 */
public class LongRides extends ExerciseBase {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(ExerciseBase.parallelism);

        DataStream<TaxiRide> rides = env.addSource(rideSourceOrTest(new TaxiRideGenerator()));

        DataStream<TaxiRide> longRides = rides
                .keyBy((TaxiRide ride) -> ride.rideId)
                .process(new MatchFunction());

        printOrTest(longRides);

        env.execute("Long Taxi Rides");
    }

    private static class MatchFunction extends KeyedProcessFunction<Long, TaxiRide, TaxiRide> {

        private ValueState<TaxiRide> rideState;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<TaxiRide> stateDescriptor =
                    new ValueStateDescriptor<>("ride event", TaxiRide.class);
            rideState = getRuntimeContext().getState(stateDescriptor);
        }

        @Override
        public void processElement(TaxiRide ride, Context context, Collector<TaxiRide> out) throws Exception {
            TaxiRide previousRideEvent = rideState.value();

            if (previousRideEvent == null) {
                rideState.update(ride);
                if (ride.isStart) {
                    context.timerService().registerEventTimeTimer(getTimerTime(ride));
                }
            } else {
                if (!ride.isStart) {
                    // END 이벤트이므로 저장된 이벤트가 START 이벤트이고 타이머가 있습니다.
                    // 타이머는 아직 작동(fire)하지 않았고, 타이머를 안전하게 해제할 수 있습니다.
                    context.timerService().deleteEventTimeTimer(getTimerTime(previousRideEvent));
                }
                rideState.clear();
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext context, Collector<TaxiRide> out) throws Exception {

            // 두 시간 전에 탑승이 사직되었으나 END 처리가 되지 않은 경우, onTimer()가 호출됩니다.
            out.collect(rideState.value());
            rideState.clear();
        }

        private long getTimerTime(TaxiRide ride) {

            // 택시 탑승 시작 시간에 2시간을 더하여 타이머로 등록합니다.
            return ride.startTime.plusSeconds(120 * 60).toEpochMilli();
        }
    }
}
