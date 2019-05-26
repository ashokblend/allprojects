package com.aryaka.test.sort;

import java.io.BufferedReader;

/**
 * This is wrapper on top of a BufferedReader... which keeps
 * the last line in memory.
 *
 */
public final class CustomFileBuffer implements RecordStack {

    private BufferedReader fbr;

    private String cache;
    
    public CustomFileBuffer(BufferedReader r) throws Exception {
        this.fbr = r;
        reload();
    }
    public void close() throws Exception {
        this.fbr.close();
    }

    public boolean empty() {
        return this.cache == null;
    }

    public String peek() {
        return this.cache;
    }

    public String pop() throws Exception {
        String answer = peek().toString();// make a copy
        reload();
        return answer;
    }

    private void reload() throws Exception {
        this.cache = this.fbr.readLine();
    }


}