package com.mjbaucas.android2pythontcp;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Block {
    private String publicKey;
    private final Integer index;
    private final Long timestamp;
    private ArrayList<String> transactions;
    private final String previousHash;
    private Integer nonce;

    public Block(Integer index, Long timestamp, ArrayList<String> transactions, String previousHash, String publicKey, Integer nonce) throws UnsupportedEncodingException {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = nonce;
        String hash = generateHashBlock();
    }

    public String generateHashBlock() throws UnsupportedEncodingException {
        String dataToHash = this.index.toString() + this.timestamp.toString() + this.transactions.toString() + this.previousHash.toString();
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            Log.i("BlockHashing", "error:" + ex);
        }
        StringBuilder buffer = new StringBuilder();
        assert bytes != null;
        for (byte b: bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String computeHash(){
        String blockString = new Gson().toJson(this);
        //Log.i("Blockstring", "string:" + blockString);
        //Log.i("Blocklength", "length:" + Integer.toString(blockString.length() * 2));
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(blockString.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            Log.i("BlockHashing", "error:" + ex);
        }
        StringBuilder buffer = new StringBuilder();
        assert bytes != null;
        for (byte b: bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public Boolean validatePrivateKey(String privateKey){
        String publicKey = this.publicKey;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(privateKey.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            Log.i("BlockHashing", "error:" + ex);
        }
        StringBuilder buffer = new StringBuilder();
        assert bytes != null;
        for (byte b: bytes) {
            buffer.append(String.format("%02x", b));
        }
        return (publicKey.equals(privateKey));
    }

    public Integer getIndex(){
        return this.index;
    }

    public ArrayList<String> getTransactions(){
        return this.transactions;
    }

    public void setTransactions(String transaction){
        this.transactions.add(transaction);
    }

    public void setNonce(Integer value) {
        this.nonce = value;
    }

    public Integer getNonce() {
        return this.nonce;
    }
}
