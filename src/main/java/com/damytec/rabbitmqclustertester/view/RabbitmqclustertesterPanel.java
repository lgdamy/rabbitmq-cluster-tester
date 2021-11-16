package com.damytec.rabbitmqclustertester.view;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import com.damytec.rabbitmqclustertester.ui.BaseWindow;
import com.damytec.rabbitmqclustertester.ui.EmptySlot;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
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
    private JLabel qtdEnviadasLabel;
    private JLabel qtdRecebidasLabel;

    private AtomicInteger pubCount = new AtomicInteger(0);
    private AtomicInteger lisCount = new AtomicInteger(0);

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
        criarPublisherButton.addActionListener(this::createConnection);
        criarListenerButton.addActionListener(this::createConnection);
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
        AtomicInteger i = new AtomicInteger(0);
        publishers.forEach(pub -> {
            if (pub.isDestroyed()) {
                pub.deleteObservers();
            }
        });
        listeners.forEach(lis -> {
            if (lis.isDestroyed()) {
                lis.deleteObservers();
            }
        });
        publishers.removeIf(PublishingConnection::isDestroyed);
        listeners.removeIf(ListeningConnection::isDestroyed);
        publishers.forEach(pub -> {
            connectionsPannel.add(pub.getPanel());
            i.incrementAndGet();
        });
        listeners.forEach(pub -> {
            connectionsPannel.add(pub.getPanel());
            i.incrementAndGet();
        });
        while (i.incrementAndGet() <= 8) {
            connectionsPannel.add(new EmptySlot());
        }
        listenerCountLabel.setText(String.format("%d", listeners.size()));
        publisherCountLabel.setText(String.format("%d", publishers.size()));
        connectionsPannel.revalidate();
        connectionsPannel.repaint();
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

    public void createConnection(ActionEvent e) {
        if (publishers.size() + listeners.size() >= 8) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        CompletableFuture.supplyAsync(() -> {
            Observable connection = null;
            if (e.getSource() == criarListenerButton) {
                connection = context.getBean(ListeningConnection.class, buildConnectionPojo());
                listeners.add((ListeningConnection) connection);
            } else if (e.getSource() == criarPublisherButton) {
                connection = context.getBean(PublishingConnection.class, buildConnectionPojo());
                publishers.add((PublishingConnection) connection);
            }
            return connection;
        }).whenComplete((connection, throwable) -> {
            if (throwable != null) {
                Toolkit.getDefaultToolkit().beep();
                Throwable rootCause = ExceptionUtils.getRootCause(throwable);
                JOptionPane.showMessageDialog(root, rootCause.getMessage(), rootCause.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (connection != null) {
                connection.addObserver((o, arg) -> {
                    if (arg instanceof Throwable) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(root, ((Throwable) arg).getMessage(), arg.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if ("increment".equals(arg)) {
                        if (connection instanceof PublishingConnection) {
                            qtdEnviadasLabel.setText(String.format("%d", pubCount.incrementAndGet()));
                        }
                        if (connection instanceof ListeningConnection) {
                            qtdRecebidasLabel.setText(String.format("%d", lisCount.incrementAndGet()));
                        }
                    }
                    if ("destroy".equals(arg)) {
                        addConnections();
                    }
                });
                addConnections();
            }
        });
    }

//    Sobrescreva esse metodo apenas se sua janela vai mudar de titulo
//    @Override
//    public String title() {
//        return "Meu titulo especial";
//    }
}
