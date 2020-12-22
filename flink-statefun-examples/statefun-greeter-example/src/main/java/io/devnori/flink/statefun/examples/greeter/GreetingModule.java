
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
package io.devnori.flink.statefun.examples.greeter;

import com.google.auto.service.AutoService;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;

import java.util.Map;

@AutoService(StatefulFunctionModule.class)
public class GreetingModule implements StatefulFunctionModule {

    private static final String KAFKA_KEY = "kafka-address";

    private static final String DEFAULT_KAFKA_ADDRESS = "kafka-broker:9092";

    @Override
    public void configure(Map<String, String> globalConfiguration, Binder binder) {

        String kafkaAddress = globalConfiguration.getOrDefault(KAFKA_KEY, DEFAULT_KAFKA_ADDRESS);
        GreetingIO ioModule = new GreetingIO(kafkaAddress);

        binder.bindIngress(ioModule.getIngressSpec());
        binder.bindIngressRouter(GreetingIO.GREETING_INGRESS_ID, new GreetRouter());

        binder.bindEgress(ioModule.getEgressSpec());

        binder.bindFunctionProvider(GreetStatefulFunction.TYPE, unused -> new GreetStatefulFunction());
    }
}
