package com.example.thread;

public class Consumer extends Thread {

	private Data data;

	public Consumer(Data data) {
		this.data = data;
	}

	public void run() {
		try {
			while (true) {
				int no = this.data.getNo();
				System.out.println(no);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
