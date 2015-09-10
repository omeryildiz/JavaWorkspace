package tr.gov.tubitak.oneylul.thread;

import java.util.Date;

public class ThreadSample {

	public static void main(String[] args) {
		CounterThread counterThread = new CounterThread();
		 counterThread.start();
		 Thread reverseThread =  new Thread(new ReverseCounterRunnable()); 
		 reverseThread.start();
		 
		 new Thread(new DatePrinterRunnable()).start();

		 
		try {
			counterThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
//thread ve runnable olmak üzere iki adet class var thread kullanmak için

class CounterThread extends Thread {
	public CounterThread() {
		super("Counter Thread");
	}
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			System.out.println(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
	}
	
}

class ReverseCounterRunnable implements Runnable{

	@Override
	public void run() {
		for (int i = 100; i > 0; i--) {
			System.out.println(i);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
}

class DatePrinterRunnable implements Runnable {

	@Override
	public void run() {
		while(true) {
			System.out.println(new Date());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}