package com.example.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {


	public static void main(String[] args) {

		//Note: Mutex is semaphore with semaphore count as 1
		// max 2 people
		Semaphore semaphore = new Semaphore(2);
		System.out.println("Total available Semaphore permits : " + semaphore.availablePermits());
		
		MyATMThread t1 = new MyATMThread(semaphore, "A");
		t1.start();

		MyATMThread t2 = new MyATMThread(semaphore, "B");
		t2.start();

		MyATMThread t3 = new MyATMThread(semaphore, "C");
		t3.start();

		MyATMThread t4 = new MyATMThread(semaphore, "D");
		t4.start();

		MyATMThread t5 = new MyATMThread(semaphore, "E");
		t5.start();

		MyATMThread t6 = new MyATMThread(semaphore, "F");
		t6.start();

	}
}

class MyATMThread extends Thread {

	String name = "";

	private Semaphore semaphore;

	MyATMThread(Semaphore semaphore, String name) {
		this.name = name;
		this.semaphore = semaphore;
	}

	public void run() {

		try {

			System.out.println(name + " : acquiring lock...");
			System.out.println(name + " : available Semaphore permits now: " + semaphore.availablePermits());

			semaphore.acquire();
			System.out.println(name + " : got the permit!");

			try {

				for (int i = 1; i <= 5; i++) {

					System.out.println(name + " : is performing operation " + i + ", available Semaphore permits : "
							+ semaphore.availablePermits());

					// sleep 1 second
					Thread.sleep(1000);

				}

			} finally {

				// calling release() after a successful acquire()
				System.out.println(name + " : releasing lock...");
				semaphore.release();
				System.out.println(name + " : available Semaphore permits now: " + semaphore.availablePermits());

			}

		} catch (InterruptedException e) {

			e.printStackTrace();

		}

	}

}