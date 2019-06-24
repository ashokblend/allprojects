package com.example.thread;

public class Producer extends Thread {

	private Data data;

	public Producer(Data data) {
		this.data = data;
	}

	public void run() {
		try {
			while (true) {
				for (int i = 0; i < 100; i++) {
					this.data.setNo(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
