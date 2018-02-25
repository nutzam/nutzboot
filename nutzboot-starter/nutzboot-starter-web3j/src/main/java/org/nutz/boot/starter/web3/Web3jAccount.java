package org.nutz.boot.starter.web3;

import java.math.BigInteger;

import org.web3j.crypto.Credentials;

public class Web3jAccount {

    private String name;
    private String address;
    private String password;
    private Credentials credentials;
    private BigInteger banlance;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    public BigInteger getBanlance() {
        return banlance;
    }
    public void setBanlance(BigInteger banlance) {
        this.banlance = banlance;
    }
}
