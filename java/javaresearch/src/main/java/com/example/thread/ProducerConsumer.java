package com.example.thread;

public class ProducerConsumer {

	public static void main(String[] args) {

		Data data = new Data();
		Thread producer = new Producer(data);
		Thread consumer = new Consumer(data);
		producer.start();
		consumer.start();
	}

}

class Data {
	private int no;

	private boolean newData=false;
	public synchronized int getNo() throws InterruptedException {
		if(!newData) {
			wait();
		}
		this.newData=false;
		notify();
		return no;
	}

	public synchronized void setNo(int no) throws InterruptedException {
		if(newData) {
			wait();
		}
		this.no = no;
		newData=true;
		notify();
	}
	
}