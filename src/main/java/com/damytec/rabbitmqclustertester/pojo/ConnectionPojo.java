package com.damytec.rabbitmqclustertester.pojo;

/**
 * @author lgdamy@raiadrogasil.com on 15/11/2021
 */
public class ConnectionPojo {
    private final String host;
    private final String vhost;
    private final String username;
    private final String password;
    private final String fila;
    private final boolean confirms;
    private final boolean returns;
    private final boolean exclusive;

    public ConnectionPojo(String host, String vhost, String username, String password, String fila, boolean confirms, boolean returns, boolean exclusive) {
        this.host = host;
        this.vhost = vhost;
        this.username = username;
        this.password = password;
        this.fila = fila;
        this.confirms = confirms;
        this.returns = returns;
        this.exclusive = exclusive;
    }

    public String getHost() {
        return host;
    }

    public String getVhost() {
        return vhost;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFila() {
        return fila;
    }

    public boolean isConfirms() {
        return confirms;
    }

    public boolean isReturns() {
        return returns;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}
