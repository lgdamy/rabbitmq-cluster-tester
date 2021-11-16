package com.damytec.rabbitmqclustertester.view;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import com.damytec.rabbitmqclustertester.ui.CustomButton;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Observable;

/**
 * @author lgdamy@raiadrogasil.com on 16/11/2021
 */
@Component
@Scope("prototype")
public class ListeningConnection extends Observable implements DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ListeningConnection.class);
    private JPanel panel;
    private JLabel hostLabel;
    private JLabel vhostLabel;
    private JCheckBox exclusiveCheckbox;
    private JLabel filaLabel;
    private JLabel amountLabel;
    private JLabel closeConnectionButton;
    private JLabel healthLabel;

    private boolean destroyed = false;

    private boolean exclusive;
    private String fila;
    private Integer mensagens = 0;

    private final ConnectionFactory connection;

    private SimpleMessageListenerContainer container;

    public ListeningConnection(ConnectionPojo pojo) {
        CachingConnectionFactory connection = new CachingConnectionFactory();
        if (pojo.getHost().contains(":")) {
            connection.setHost(pojo.getHost().split(":")[0]);
            connection.setPort(Integer.parseInt(pojo.getHost().split(":")[1]));
        } else {
            connection.setHost(pojo.getHost());
        }
        connection.setVirtualHost(pojo.getVhost());
        connection.setUsername(pojo.getUsername());
        connection.setPassword(pojo.getPassword());
        this.connection = connection;
        hostLabel.setText(pojo.getHost());
        vhostLabel.setText(pojo.getVhost());
        exclusiveCheckbox.setSelected(pojo.isExclusive());
        filaLabel.setText(pojo.getFila());
        exclusive = pojo.isExclusive();
        fila = pojo.getFila();
    }

    @PostConstruct
    public void setup() {
        SimpleMessageListenerContainer container = containerFactory().createListenerContainer();
        container.setQueueNames(fila);
        container.setExclusive(exclusive);
        container.setAutoDeclare(true);
        container.setAutoStartup(true);
        container.setMessageListener((message -> {
            log.info("recebendo mensagem");
            mensagens ++;
            broadcast();
            amountLabel.setText(String.format("%d",mensagens));
        }));
        try {
            new RabbitAdmin(connection).declareQueue(QueueBuilder.nonDurable(fila).build());
        } catch (Exception ignored){}
        container.start();
        this.container = container;
    }

    public SimpleRabbitListenerContainerFactory containerFactory() {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connection);
        containerFactory.setConcurrentConsumers(5);
        containerFactory.setMaxConcurrentConsumers(10);
        if (exclusive) {
            containerFactory.setConcurrentConsumers(1);
            containerFactory.setMaxConcurrentConsumers(1);
        }
        containerFactory.setPrefetchCount(1);
        containerFactory.setStartConsumerMinInterval(3_000L);
        containerFactory.setRecoveryInterval(15_000L);
        containerFactory.setChannelTransacted(true);
        return containerFactory;
    }


    @Override
    public void destroy() {
        destroyed = true;
        this.container.stop();
        setChanged();
        notifyObservers("destroy");
    }

    private void createUIComponents() {
        closeConnectionButton = new CustomButton("images/close.png", new Dimension(16,16)){
            @Override
            public void actionPerformed() {
                destroy();
            }
        };
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void health() {
        if (container.getActiveConsumerCount() > 0) {
            healthLabel.setText("UP");
            healthLabel.setForeground(Color.BLUE);
        } else {
            healthLabel.setText("DOWN");
            healthLabel.setForeground(Color.RED);
        }
    }

    public void broadcast() {
        this.setChanged();
        this.notifyObservers("increment");
    }
}
