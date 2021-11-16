package com.damytec.rabbitmqclustertester.view;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import com.damytec.rabbitmqclustertester.ui.CustomButton;
import org.codehaus.plexus.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.UUID;

/**
 * @author lgdamy@raiadrogasil.com on 15/11/2021
 */
@Component
@Scope("prototype")
public class PublishingConnection extends Observable implements DisposableBean {
    private static Logger log = LoggerFactory.getLogger(PublishingConnection.class);
    private JLabel hostLabel;
    private JLabel vhostLabel;
    private JCheckBox confirmsCheckbox;
    private JCheckBox returnsCheckbox;
    private JButton publicarButton;
    private JLabel closeConnectionButton;
    private JLabel filaLabel;
    private JPanel panel;
    private JLabel healthLabel;

    private boolean destroyed;

    private final ConnectionFactory connection;
    private final RabbitTemplate template;

    public PublishingConnection(ConnectionPojo pojo) {
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
        connection.setPublisherConfirms(pojo.isConfirms());
        connection.setPublisherReturns(pojo.isReturns());
        this.connection = connection;
        template = new RabbitTemplate(connection);
        template.setMandatory(pojo.isReturns());
        template.setExchange(DirectExchange.DEFAULT.getName());
        template.setRoutingKey(pojo.getFila());
        hostLabel.setText(pojo.getHost());
        vhostLabel.setText(pojo.getVhost());
        confirmsCheckbox.setSelected(pojo.isConfirms());
        returnsCheckbox.setSelected(pojo.isReturns());
        publicarButton.addActionListener(e -> {
            try {
                template.invoke(op -> {
                    CorrelationData correlation = new CorrelationData(UUID.randomUUID().toString());
                    op.convertAndSend("teste", (message -> {
                        log.info("enviando mensagem");
                        return message;
                    }), correlation);
                    if (pojo.isConfirms()) {
                        op.waitForConfirmsOrDie(5000L);
                    }
                    if (pojo.isReturns()) {
                        Assert.state(correlation.getReturnedMessage() == null, () -> "Message returned");
                    }
                    return op;
                });
                broadcast();
            } catch (Throwable ex) {
                log.error("[{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
                broadcast(ex);
            }
        });
        if (pojo.isReturns()) {
            template.setReturnCallback((message, i, s, s1, s2) -> log.error("[{}] {}", i, s));
        }
        filaLabel.setText(pojo.getFila());
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

    @Override
    public void destroy() {
        destroyed = true;
        this.template.stop();
        setChanged();
        this.notifyObservers("destroy");
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void health() {
        try {
            Assert.notNull(template.execute(channel -> channel.getConnection().getServerProperties().get("version")));
            healthLabel.setText("UP");
            healthLabel.setForeground(Color.BLUE);
        } catch ( Exception e) {
            healthLabel.setText("DOWN");
            healthLabel.setForeground(Color.RED);
        }
    }

    private void broadcast(Throwable error) {
        this.setChanged();
        this.notifyObservers(error);
    }

    private void broadcast() {
        this.setChanged();
        this.notifyObservers("increment");
    }
}
