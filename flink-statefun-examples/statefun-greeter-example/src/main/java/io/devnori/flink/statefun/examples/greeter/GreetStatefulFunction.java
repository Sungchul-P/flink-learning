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
import io.devnori.flink.statefun.examples.greeter.generated.GreetResponse;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.StatefulFunction;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.state.PersistedValue;

/**
 * 사용자의 방문 횟수에 따라 각 사용자에 대해 고유한 인사말을 생성합니다.
 */
final class GreetStatefulFunction implements StatefulFunction {

    /**
     * FunctionType은 이 함수의 유형을 식별하는 고유 식별자입니다.
     * 유형은 식별자와 함께 라우터 및 기타 함수를 사용하여 특정 Greeter 함수 인스턴스를 참조하는 방법입니다.
     *
     * 다중 모듈 응용 프로그램인 경우 FunctionType이 서로 다른 패키지에 있을 수 있으므로
     * 다른 모듈의 함수가 이 클래스에 직접 의존하지 않고 greeter를 전송할 수 있습니다.
     */
    static final FunctionType TYPE = new FunctionType("apache", "greeter");

    /**
     * 특정 사용자에 대한 상태를 유지하기 위한 지속 값입니다.
     * 이 필드에 의해 반환되는 값은 항상 현재 사용자에게 범위가 지정됩니다. seenCount는 사용자의 방문 횟수입니다.
     */
    @Persisted
    private final PersistedValue<Integer> seenCount = PersistedValue.of("seen-count", Integer.class);

    @Override
    public void invoke(Context context, Object input) {
        GreetRequest greetMessage = (GreetRequest) input;
        GreetResponse response = computePersonalizedGreeting(greetMessage);
        context.send(GreetingIO.GREETING_EGRESS_ID, response);
    }

    private GreetResponse computePersonalizedGreeting(GreetRequest greetMessage) {
        final String name = greetMessage.getWho();
        final int seen = seenCount.getOrDefault(0);
        seenCount.set(seen + 1);

        String greeting = greetText(name, seen);

        return GreetResponse.newBuilder().setWho(name).setGreeting(greeting).build();
    }

    private static String greetText(String name, int seen) {
        switch (seen) {
            case 0:
                return String.format("Hello %s ! \uD83D\uDE0E", name);
            case 1:
                return String.format("Hello again %s ! \uD83E\uDD17", name);
            case 2:
                return String.format("Third time is a charm! %s! \uD83E\uDD73", name);
            case 3:
                return String.format("Happy to see you once again %s ! \uD83D\uDE32", name);
            default:
                return String.format("Hello at the %d-th time %s \uD83D\uDE4C", seen + 1, name);
        }
    }
}
