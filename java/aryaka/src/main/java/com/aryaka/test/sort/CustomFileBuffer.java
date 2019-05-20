package com.aryaka.test.sort;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This is wrapper on top of a BufferedReader... which keeps
 * the last line in memory.
 *
 */
public final class CustomFileBuffer {

    private BufferedReader fbr;

    private String cache;
    
    public CustomFileBuffer(BufferedReader r) throws IOException {
        this.fbr = r;
        reload();
    }
    public void close() throws IOException {
        this.fbr.close();
    }

    public boolean empty() {
        return this.cache == null;
    }

    public String peek() {
        return this.cache;
    }

    public String pop() throws IOException {
        String answer = peek().toString();// make a copy
        reload();
        return answer;
    }

    private void reload() throws IOException {
        this.cache = this.fbr.readLine();
    }


}