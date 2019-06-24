package com.example.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExample extends Thread {

	private Instance ins;

	private AtomicInteger inta = new AtomicInteger();
	public ThreadExample(Instance ins) {
		this.ins = ins;
	}

	public static void main(String... args) {

		Instance ins = new Instance();
		Thread t1 = new ThreadExample(ins);
		t1.setName("t1");
		Thread t2 = new ThreadExample(ins);
		t2.setName("t2");
		t1.start();
		t2.start();
	}

	@Override
	public void run() {
		while (true) {
			if (this.getName().equals("t1")) {
				ins.increment();
			} else {
				ins.decrement();
			}
		}

	}

}

class Instance {
	int counter;

	public Instance() {

	}

	public synchronized void increment() {
		counter++;
	}

	public synchronized void decrement() {
		counter--;
	}
}