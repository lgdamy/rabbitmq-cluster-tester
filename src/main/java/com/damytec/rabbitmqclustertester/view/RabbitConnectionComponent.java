package com.damytec.rabbitmqclustertester.view;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.DisposableBean;

import javax.swing.*;
import java.util.Observable;

/**
 * @author lgdamy@raiadrogasil.com on 12/12/2021
 */
public abstract class RabbitConnectionComponent extends Observable implements DisposableBean {

    protected CachingConnectionFactory connectionFactory;

    protected RabbitConnectionComponent(ConnectionPojo pojo) {
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
        this.connectionFactory = connection;
    }

    @Override
    public void destroy() {
        connectionFactory.clearConnectionListeners();
        setChanged();
        notifyObservers("destroy");
    }

    public abstract void health();

    public abstract JPanel getPanel();
}
