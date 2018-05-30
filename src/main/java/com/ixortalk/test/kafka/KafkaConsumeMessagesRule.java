/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.test.kafka;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.rules.ExternalResource;
import org.springframework.kafka.annotation.KafkaListener;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.fail;

@Named
public class KafkaConsumeMessagesRule<K, V> extends ExternalResource {

    @Inject
    private ObjectMapper objectMapper;

    private BlockingQueue<ConsumerRecord<K, V>> messages;

    @KafkaListener(topics = {"${ixortalk.test.topic}", "${ixortalk.test.second-topic:empty}"})
    public void consume(ConsumerRecord<K, V> message) {
        this.messages.add(message);
    }

    public V pollMessage() throws InterruptedException {
        ConsumerRecord<K, V> consumerRecord = pollConsumerRecord();
        if (consumerRecord == null) {
            fail("No consumer record polled!");
        }
        return consumerRecord.value();
    }

    public List<V> pollMessages(int times) throws InterruptedException {
        return pollConsumerRecords(times).stream().map(ConsumerRecord::value).collect(toList());
    }

    public String pollMessageAsJson() throws InterruptedException, JsonProcessingException {
        return objectMapper.writeValueAsString(pollMessage());
    }

    public ConsumerRecord<K, V> pollConsumerRecord() throws InterruptedException {
        return pollConsumerRecords(1).stream().findFirst().orElse(null);
    }

    public List<ConsumerRecord<K, V>> pollConsumerRecords(int times) throws InterruptedException {
        return range(0, times)
                .mapToObj(i-> {
                    try {
                        return messages.poll(10, SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error while polling: " + e.getMessage(), e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        this.messages = new LinkedBlockingQueue<>();
    }

    @Override
    protected void after() {
        super.after();
        this.messages = null;
    }
}
