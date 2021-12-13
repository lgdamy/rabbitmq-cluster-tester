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

    private List<RabbitConnectionComponent> connections = new ArrayList<>();

    @PostConstruct
    public void setup() {
        new BaseWindow(this, 800, 600);
        new Timer(5000, e -> {
            connections.stream().forEach(RabbitConnectionComponent::health);
        }).start();
    }

    public RabbitmqclustertesterPanel() {
        root.add(connectionsPannel, BorderLayout.CENTER);
        criarPublisherButton.addActionListener(this::createConnection);
        criarListenerButton.addActionListener(this::createConnection);
        drawConnections();
    }

    @Override
    public JPanel root() {
        return this.root;
    }

    private void createUIComponents() {
        connectionsPannel = new JPanel(new GridLayout(2, 4));
        connectionsPannel.setVisible(true);
    }

    private void drawConnections() {
        connectionsPannel.removeAll();
        connections.stream().map(RabbitConnectionComponent::getPanel).forEach(connectionsPannel::add);
        int i = 8 - connections.size();
        while (i-- > 0) {
            connectionsPannel.add(new EmptySlot());
        }
        listenerCountLabel.setText(String.format("%d", connections.stream().filter(c -> c instanceof ListeningConnection).count()));
        publisherCountLabel.setText(String.format("%d", connections.stream().filter(c -> c instanceof PublishingConnection).count()));
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
        if (connections.size() >= 8) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        CompletableFuture.supplyAsync(() -> {
            RabbitConnectionComponent connection = null;
            if (e.getSource() == criarListenerButton) {
                connection = context.getBean(ListeningConnection.class, buildConnectionPojo());
                connections.add(connection);
            } else if (e.getSource() == criarPublisherButton) {
                connection = context.getBean(PublishingConnection.class, buildConnectionPojo());
                connections.add( connection);
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
                        connections.remove(connection);
                        drawConnections();
                    }
                });
                drawConnections();
            }
        });
    }

//    Sobrescreva esse metodo apenas se sua janela vai mudar de titulo
//    @Override
//    public String title() {
//        return "Meu titulo especial";
//    }
}
