
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

import io.devnori.flink.statefun.examples.greeter.generated.GreetRequest;
import org.apache.flink.statefun.sdk.io.Router;

/**
 * GreetRouter는 Ingress에서 각 메시지를 가져와서 사용자 ID에 따라 더 greeter function으로 라우트합니다.
 */
public class GreetRouter implements Router<GreetRequest> {

    @Override
    public void route(GreetRequest message, Downstream<GreetRequest> downstream) {
        downstream.forward(GreetStatefulFunction.TYPE, message.getWho(), message);
    }
}
