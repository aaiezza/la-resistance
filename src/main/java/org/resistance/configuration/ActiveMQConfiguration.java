package org.resistance.configuration;

import java.net.URI;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import com.google.common.collect.Lists;

@EnableJms
@Configuration
public class ActiveMQConfiguration {
  @Bean
  public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL("tcp://0.0.0.0:61616");
    return activeMQConnectionFactory;
  }

  @Bean
  public Broker activeMQBroker() throws Exception {
    BrokerService b = new BrokerService();

    b.setSchedulePeriodForDestinationPurge(10000);
    b.setPersistent(false);
    b.setUseJmx(true);
    final PolicyMap policyMap = new PolicyMap();
    final PolicyEntry policyEntry = new PolicyEntry();
    policyEntry.setTopic(">");
    policyEntry.setGcInactiveDestinations(true);
    policyEntry.setInactiveTimeoutBeforeGC(30000);
    policyMap.setPolicyEntries(Lists.newArrayList(policyEntry));

    b.setDestinationPolicy(policyMap);

    final TransportConnector tc1 = new TransportConnector();
    tc1.setName("openwire");
    tc1.setUri(URI.create("tcp://0.0.0.0:61616"));
    final TransportConnector tc2 = new TransportConnector();
    tc2.setUri(URI.create("stomp://127.0.0.1:61613"));

    b.setTransportConnectors(Lists.newArrayList(tc1, tc2));

    b.start();

    return b.getBroker();
  }

//  public JmsListenerContainerFactory<?> queueListenerFactory() {
//    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//    factory.factory.setMessageConverter(messageConverter());
//    return factory;
//  }
//
//  @Bean
//  public MessageConverter messageConverter() {
//    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//    converter.setTargetType(MessageType.TEXT);
//    converter.setTypeIdPropertyName("_type");
//    return converter;
//  }

}