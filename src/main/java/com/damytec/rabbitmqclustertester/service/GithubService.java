package com.damytec.rabbitmqclustertester.service;

import java.awt.*;
import java.net.URI;

/**
 * @author lgdamy on 22/01/2021
 */
public class GithubService {

    private static final URI URL = URI.create("https://github.com/lgdamy/rabbitmq-cluster-tester");

    private static GithubService INSTANCE;

    private GithubService() {}

    public static GithubService getInstance() {
        return INSTANCE = INSTANCE == null ? new GithubService() : INSTANCE;
    }

    public void openGithub() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
