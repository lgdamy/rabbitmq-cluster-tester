package com.damytec.rabbitmqclustertester.ui;

import com.damytec.rabbitmqclustertester.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author lgdamy on 24/01/2021
 */
public abstract class CustomButton extends JLabel {

    private int width;
    private int height;
    private ImageIcon idle;
    private ImageIcon hover;
    private ImageIcon pressed;

    public CustomButton(String idleImage,
                        String hoverImage,
                        String pressedImage,
                        Dimension dimension) {
        super("");
        if (dimension == null) {
            retrieveImageDimensions(idleImage);
        } else {
            this.width = (int)dimension.getWidth();
            this.height = (int)dimension.getHeight();
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.changeIcon(getImage(idleImage));
        this.clickedAction();
        idle = getImage(idleImage);
        hover = hoverImage != null ? getImage(hoverImage) : new ImageIcon(ImageUtil.brightness(idle.getImage(),1.5f));
        this.hoverAction();
        pressed = pressedImage != null ? getImage(pressedImage) : new ImageIcon(ImageUtil.brightness(idle.getImage(), 0.75f));
        this.pressedAction();
    }

    public CustomButton(String idleImage,Dimension dimension) {
        this(idleImage, null, null, dimension);
    }

    public CustomButton(String idleImage) {
        this(idleImage, null, null, null);
    }
    public CustomButton(String idleImage, String hoverImage) {
        this(idleImage, hoverImage, null, null);
    }
    public CustomButton(String idleImage, String hoverImage, String pressedImage) {
        this(idleImage, hoverImage, pressedImage, null);
    }

    public abstract void actionPerformed();

    private void clickedAction() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actionPerformed();
            }
        });
    }

    private void hoverAction() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                changeIcon(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                changeIcon(idle);
            }
        });
    }

    private void pressedAction() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                changeIcon(pressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                changeIcon(idle);
            }
        });
    }

    private ImageIcon getImage(String resource) {
        try {
            ImageIcon icon = new ImageIcon(CustomButton.class.getClassLoader().getResource(resource));
            if (icon.getIconWidth() != width || icon.getIconHeight() != height) {
                icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
            return icon;
        } catch (Exception e) {
            return new ImageIcon(ImageUtil.createPlaceHolder(width, height));
        }
    }

    private void retrieveImageDimensions(String resource) {
        try {
            ImageIcon icon = new ImageIcon(CustomButton.class.getClassLoader().getResource(resource));
            this.width = icon.getIconWidth();
            this.height = icon.getIconHeight();
        } catch (Exception e) {
            this.width = 32;
            this.height = 32;
            e.printStackTrace();
        }
    }

    @Override
    public void setIcon(Icon icon) {
        return;
    }

    private void changeIcon(Icon icon) {
        super.setIcon(icon);
    }
}
