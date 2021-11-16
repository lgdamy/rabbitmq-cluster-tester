package com.damytec.rabbitmqclustertester;

import com.damytec.rabbitmqclustertester.pojo.ConnectionPojo;
import com.damytec.rabbitmqclustertester.ui.BaseWindow;
import com.damytec.rabbitmqclustertester.view.PublishingConnection;
import com.damytec.rabbitmqclustertester.view.RabbitmqclustertesterPanel;
import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author lgdamy@ on 22/01/2021
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        FlatLightLaf.install();
        ConfigurableApplicationContext context = new SpringApplicationBuilder(App.class)
                .headless(false).run(args);
        EventQueue.invokeLater(() -> {
            RabbitmqclustertesterPanel panel = context.getBean(RabbitmqclustertesterPanel.class);
            panel.setApplicationContext(context);
        });
    }
}
