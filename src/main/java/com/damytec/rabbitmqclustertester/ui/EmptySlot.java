package com.damytec.rabbitmqclustertester.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * @author lgdamy@raiadrogasil.com on 15/11/2021
 */
public class EmptySlot extends JPanel {

    public EmptySlot() {
        this.setLayout(new BorderLayout());
        JLabel texto = new JLabel("(vazio)", SwingConstants.CENTER);
        texto.setEnabled(false);
        this.add(texto, BorderLayout.CENTER);
        this.setBorder(new EtchedBorder());
    }
}
