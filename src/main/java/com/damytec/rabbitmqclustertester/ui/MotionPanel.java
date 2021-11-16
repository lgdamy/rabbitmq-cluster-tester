package com.damytec.rabbitmqclustertester.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author lgdamy on 22/01/2021
 */
public class MotionPanel extends JPanel {
    private Point initialClick;
    private JFrame parent;

    public MotionPanel(final JFrame parent){
        this.parent = parent;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                // get location of Window
                int thisX = parent.getLocation().x;
                int thisY = parent.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int x = thisX + xMoved;
                int y = thisY + yMoved;
                parent.setLocation(x, y);
            }
        });
    }
}
