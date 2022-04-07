package com.mjbaucas.android2pythontcp;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PublicChain {
    private final Block genBlock;
    private Integer difficulty;
    private ArrayList<Block> chain;

    public PublicChain(Integer difficulty) throws UnsupportedEncodingException {
        this.genBlock = this.genGenBlock();
        this.difficulty = difficulty;
        this.chain = new ArrayList<Block>();
        this.chain.add(this.genBlock);
    }

    public void genNextBlock(String publicKey, ArrayList<String> transactions) throws UnsupportedEncodingException {
        Block previousBlock = this.chain.get(this.chain.size() - 1);
        Integer index = previousBlock.getIndex() + 1;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Long timestamp = ts.getTime();
        String hashedBlock = previousBlock.generateHashBlock();
        this.chain.add(new Block(index, timestamp, transactions, hashedBlock, publicKey, 0));
    }

    public String proofOfWork(Block block) throws UnsupportedEncodingException {
        block.setNonce(0);
        String computedHash = block.computeHash();
        String prefixString = new String(new char
                [this.difficulty]).replace('\0', '0');
        while(!computedHash.substring(0, this.difficulty).equals(prefixString)){
            block.setNonce(block.getNonce() + 1);
            computedHash = block.computeHash();
        }
        return computedHash;
    }

    public boolean verifyProof(Block block, String proof){
        String prefixString = new String(new char
                [this.difficulty]).replace('\0', '0');
        return (proof.substring(0, this.difficulty).equals(prefixString) && proof == block.computeHash());
    }

    public Block searchLedger(String key){
        for(int i = chain.size() - 1; i >= 0; i--){
            if (this.chain.get(i).validatePrivateKey(key)){
                return this.chain.get(i);
            }
        }
        return null;
    }

    public Block genGenBlock() throws UnsupportedEncodingException {
        ArrayList<String> transaction = new ArrayList<String>();
        transaction.add("XX:XX:XX:XX:XX");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return new Block(0, ts.getTime(), transaction, "0", "0", 0);
    }

    public Block getBlock(Integer index){
        return this.chain.get(index);
    }
}
