package com.damytec.rabbitmqclustertester.view;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import com.damytec.rabbitmqclustertester.ui.BaseWindow;
import com.damytec.rabbitmqclustertester.ui.EmptySlot;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lgdamy on 25/01/2021
 */
@Component
public class RabbitmqclustertesterPanel implements BaseWindow.ContentForm, ApplicationContextAware {
    private JPanel root;
    private JTextField hostField;
    private JTextField vhostField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JPanel rabbitCommonForm;
    private JTextField filaField;
    private JCheckBox confirmsCheckbox;
    private JCheckBox returnsCheckbox;
    private JCheckBox exclusiveCheckbox;
    private JLabel publisherCountLabel;
    private JLabel listenerCountLabel;
    private JButton criarPublisherButton;
    private JButton criarListenerButton;
    private JPanel connectionsPannel;

    private int pubCount = 0;

    private ApplicationContext context;

    private List<PublishingConnection> publishers = new ArrayList<>();
    private List<ListeningConnection> listeners = new ArrayList<>();


    @PostConstruct
    public void setup() {
        new BaseWindow(this, 800, 600);
        new Timer(5000, e -> {
            publishers.stream().forEach(PublishingConnection::health);
            listeners.stream().forEach(ListeningConnection::health);
        }).start();
    }

    public RabbitmqclustertesterPanel() {
        root.add(connectionsPannel, BorderLayout.CENTER);
        criarPublisherButton.addActionListener(e -> {
            if (publishers.size() + listeners.size() >= 8) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            PublishingConnection publisher = context.getBean(PublishingConnection.class, buildConnectionPojo());
            publishers.add(publisher);
            publisher.addObserver((o, arg) -> addConnections());
            addConnections();
        });
        criarListenerButton.addActionListener(e -> {
            if (publishers.size() + listeners.size() >= 8) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            ListeningConnection listener = context.getBean(ListeningConnection.class, buildConnectionPojo());
            listeners.add(listener);
            listener.addObserver((o, arg) -> addConnections());
            addConnections();
        });
        addConnections();
    }

    @Override
    public JPanel root() {
        return this.root;
    }

    private void createUIComponents() {
        connectionsPannel = new JPanel(new GridLayout(2, 4));
        connectionsPannel.setVisible(true);
    }

    private void addConnections() {
        connectionsPannel.removeAll();
        connectionsPannel.revalidate();
        connectionsPannel.repaint();
        AtomicInteger i = new AtomicInteger(0);
        publishers.removeIf(PublishingConnection::isDestroyed);
        listeners.removeIf(ListeningConnection::isDestroyed);
        publishers.stream().forEach(pub -> {
            connectionsPannel.add(pub.getPanel());
            i.incrementAndGet();
        });
        listeners.stream().forEach(pub -> {
            connectionsPannel.add(pub.getPanel());
            i.incrementAndGet();
        });
        while (i.incrementAndGet() <= 8) {
            connectionsPannel.add(new EmptySlot());
        }
        listenerCountLabel.setText(String.format("%d", listeners.size()));
        publisherCountLabel.setText(String.format("%d", publishers.size()));
    }

    @Bean
    @Scope("prototype")
    public ConnectionPojo buildConnectionPojo() {
        return new ConnectionPojo(
                this.hostField.getText(),
                this.vhostField.getText(),
                this.usernameField.getText(),
                this.passwordField.getText(),
                this.filaField.getText(),
                this.confirmsCheckbox.isSelected(),
                this.returnsCheckbox.isSelected(),
                this.exclusiveCheckbox.isSelected());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

//    Sobrescreva esse metodo apenas se sua janela vai mudar de titulo
//    @Override
//    public String title() {
//        return "Meu titulo especial";
//    }
}
