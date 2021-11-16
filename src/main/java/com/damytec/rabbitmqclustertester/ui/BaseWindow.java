package com.damytec.rabbitmqclustertester.ui;

import com.damytec.rabbitmqclustertester.service.GithubService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * @author lgdamy on 25/01/2021
 */
public class BaseWindow extends JFrame {
    private JPanel basePanel;
    private CustomButton closeButton;
    private CustomButton logoButton;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private ContentForm content;

    private ImageIcon logo;

    private int windowWidth;
    private int windowHeight;
    private static final String DEFAULT_TITLE = "RabbitMQ Cluster Tester";

    public BaseWindow(ContentForm form, Integer windowWidth, Integer windowHeight) {
        this.windowWidth = windowWidth == null ? (int) this.getScreenDimension().getWidth() / 2 : windowWidth;
        this.windowHeight = windowHeight == null ? (int) this.getScreenDimension().getHeight() / 2 : windowHeight;
        this.content = form;
        this.createUI();
        this.buildImages();
        this.buildFrame();
    }

    public BaseWindow(ContentForm form) {
        this(form, null,null);
    }

    private void buildFrame() {
        this.setIconImage(logo.getImage());
        this.add(basePanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dim = this.getScreenDimension();
        this.setBounds((dim.width - windowWidth) / 2, (dim.height - windowHeight) / 2, windowWidth, windowHeight);
        this.setUndecorated(true);
        this.setVisible(true);
        this.setResizable(false);
    }

    private void buildImages() {
        this.logo = new ImageIcon(BaseWindow.class.getClassLoader().getResource("images/logo.png"));
    }

    private void createUI() {
        basePanel = new MotionPanel(this);
        closeButton = new CustomButton("images/close.png", new Dimension(32,32)) {
            @Override
            public void actionPerformed() {
                System.exit(0);
            }
        };
        logoButton = new CustomButton("images/logomini.png", new Dimension(32,32)) {
            @Override
            public void actionPerformed() {
                GithubService.getInstance().openGithub();
            }
        };
        basePanel.setLayout(new BorderLayout(0, 0));
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout(0, 0));
        headerPanel.setBackground(new Color(-4276546));
        basePanel.add(headerPanel, BorderLayout.NORTH);
        headerPanel.add(logoButton, BorderLayout.WEST);
        final JLabel label1 = new JLabel();
        Font label1Font = this.createFont(null, Font.BOLD, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setText(content.title());
        headerPanel.add(label1, BorderLayout.CENTER);
        final JSeparator separator1 = new JSeparator();
        separator1.setOrientation(0);
        headerPanel.add(separator1, BorderLayout.SOUTH);
        headerPanel.add(closeButton, BorderLayout.EAST);
        footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout(0, 0));
        footerPanel.setBackground(new Color(-16187308));
        basePanel.add(footerPanel, BorderLayout.SOUTH);
        final JLabel label2 = new JLabel();
        label2.setBackground(new Color(-859392));
        Font label2Font = this.createFont(null, -1, 10, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setForeground(new Color(-859392));
        label2.setText("Damy Tecnologia da Informa\u00e7\u00e3o LTDA");
        footerPanel.add(label2, BorderLayout.WEST);
        final JLabel label3 = new JLabel();
        Font label3Font = this.createFont(null, -1, 10, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setForeground(new Color(-859392));
        label3.setText("30.281.367/0001-91");
        footerPanel.add(label3, BorderLayout.EAST);
        basePanel.add(content.root(), BorderLayout.CENTER);
    }

    private Dimension getScreenDimension() {
        try {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } catch (HeadlessException | AWTError ex) {
            throw new RuntimeException("O ambiente n\u00e3o suporta nenhuma janela: " + ex.getClass().getSimpleName());
        }
    }

    private Font createFont(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    public interface ContentForm {
        JPanel root();
        default String title() {
            return DEFAULT_TITLE;
        }
    }
}
