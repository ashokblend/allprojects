package com.aryaka.test.sort;

public interface RecordStack {

	public void close() throws Exception;

    public boolean empty();

    public String peek();

    public String pop() throws Exception;
}
