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
package io.devnori.flink.statefun.examples.shoppingcart;

import io.devnori.flink.statefun.examples.shoppingcart.generated.ProtobufMessages;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.io.EgressIdentifier;
import org.apache.flink.statefun.sdk.io.EgressSpec;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressSpec;
import org.apache.flink.statefun.sdk.kafka.KafkaEgressBuilder;
import org.apache.flink.statefun.sdk.kafka.KafkaEgressSerializer;
import org.apache.flink.statefun.sdk.kafka.KafkaIngressBuilder;
import org.apache.flink.statefun.sdk.kafka.KafkaIngressDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class Identifiers {

    static final FunctionType USER = new FunctionType("shopping-cart", "user");

    static final FunctionType INVENTORY = new FunctionType("shopping-cart", "inventory");

    static final IngressIdentifier<ProtobufMessages.AddToCart> ADD_TO_CART =
            new IngressIdentifier<>(ProtobufMessages.AddToCart.class, "shopping-cart", "add-to-cart");

    static final IngressIdentifier<ProtobufMessages.RestockItem> RESTOCK =
            new IngressIdentifier<>(ProtobufMessages.RestockItem.class, "shopping-cart", "restock-item");

    static final IngressIdentifier<ProtobufMessages.Checkout> CHECKOUT =
            new IngressIdentifier<>(ProtobufMessages.Checkout.class, "shopping-cart", "checkout-item");

    static final EgressIdentifier<ProtobufMessages.Receipt> RECEIPT =
            new EgressIdentifier<>("shopping-cart", "receipt", ProtobufMessages.Receipt.class);

    private final String kafkaAddress;

    Identifiers(String kafkaAddress) {this.kafkaAddress = Objects.requireNonNull(kafkaAddress);}

    IngressSpec<ProtobufMessages.AddToCart> getIngressSpecForCart() {
        return KafkaIngressBuilder.forIdentifier(ADD_TO_CART)
                .withKafkaAddress(kafkaAddress)
                .withTopic("add-to-cart")
                .withDeserializer(CartKafkaDeserializer.class)
                .withProperty(ConsumerConfig.GROUP_ID_CONFIG, "cart-receipt")
                .build();
    }

    IngressSpec<ProtobufMessages.RestockItem> getIngressSpecForRestock() {
        return KafkaIngressBuilder.forIdentifier(RESTOCK)
                .withKafkaAddress(kafkaAddress)
                .withTopic("restock-item")
                .withDeserializer(RestockKafkaDeserializer.class)
                .withProperty(ConsumerConfig.GROUP_ID_CONFIG, "restock-item")
                .build();
    }

    IngressSpec<ProtobufMessages.Checkout> getIngressSpecForCheckOut() {
        return KafkaIngressBuilder.forIdentifier(CHECKOUT)
                .withKafkaAddress(kafkaAddress)
                .withTopic("checkout-item")
                .withDeserializer(CheckOutKafkaDeserializer.class)
                .withProperty(ConsumerConfig.GROUP_ID_CONFIG, "checkout-item")
                .build();
    }

    EgressSpec<ProtobufMessages.Receipt> getEgressSpec() {
        return KafkaEgressBuilder.forIdentifier(RECEIPT)
                .withKafkaAddress(kafkaAddress)
                .withSerializer(ReceiptKafkaSerializer.class)
                .build();
    }

    private static final class CartKafkaDeserializer implements KafkaIngressDeserializer<ProtobufMessages.AddToCart> {

        private static final long serialVersionUID = 1L;

        @Override
        public ProtobufMessages.AddToCart deserialize(ConsumerRecord<byte[], byte[]> input) {
            String record = new String(input.value(), StandardCharsets.UTF_8);
            String[] split = record.split(",");
            String user_id = split[0];
            String item_id = split[1];
            String quantity = split[2];

            return ProtobufMessages.AddToCart.newBuilder()
                    .setUserId(user_id)
                    .setItemId(item_id)
                    .setQuantity(new Integer(quantity))
                    .build();
        }
    }

    private static final class RestockKafkaDeserializer implements KafkaIngressDeserializer<ProtobufMessages.RestockItem> {

        private static final long serialVersionUID = 1L;

        @Override
        public ProtobufMessages.RestockItem deserialize(ConsumerRecord<byte[], byte[]> input) {
            String record = new String(input.value(), StandardCharsets.UTF_8);
            String[] split = record.split(",");
            String user_id = split[0];
            String quantity = split[1];

            return ProtobufMessages.RestockItem.newBuilder()
                    .setItemId(user_id)
                    .setQuantity(new Integer(quantity))
                    .build();
        }
    }

    private static final class CheckOutKafkaDeserializer implements KafkaIngressDeserializer<ProtobufMessages.Checkout> {

        private static final long serialVersionUID = 1L;

        @Override
        public ProtobufMessages.Checkout deserialize(ConsumerRecord<byte[], byte[]> input) {
            String user_id = new String(input.value(), StandardCharsets.UTF_8);

            return ProtobufMessages.Checkout.newBuilder()
                    .setUserId(user_id).build();
        }
    }

    private static final class ReceiptKafkaSerializer implements KafkaEgressSerializer<ProtobufMessages.Receipt> {

        private static final long serialVersionUID = 1L;

        @Override
        public ProducerRecord<byte[], byte[]> serialize(ProtobufMessages.Receipt receipt) {
            byte[] key = receipt.getUserId().getBytes(StandardCharsets.UTF_8);
            byte[] value = receipt.getDetails().getBytes(StandardCharsets.UTF_8);

            return new ProducerRecord<>("receipts", key, value);
        }
    }
}
