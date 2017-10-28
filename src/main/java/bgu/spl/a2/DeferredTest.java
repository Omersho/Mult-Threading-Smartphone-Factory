package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeferredTest {
	
	private int callbackCounter;
	
	@Test
	public void testGet(){
		boolean success = true;
		Deferred<Integer> d = new Deferred<Integer>();
			Integer toAssign = new Integer(9889234);
			Thread assignThread = new Thread(new Runnable(){
				public void run(){
					d.resolve(toAssign);
				}
			});
			assignThread.start();
			Integer returnedVlaue = d.get();
			if(d.isResolved() == false){
				success = false;
			}
			if(success && returnedVlaue != toAssign){
				success = false;
			}
			try {
				assignThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		assertEquals(true,success);
	}
	
	@Test
	public void testIsResolved(){
		boolean success = true;
		Deferred<Integer> d = new Deferred<Integer>();
		Integer toAssign = new Integer(5);
		if(d.isResolved() != false) //Check if isResolved return false before resolve.
			success = false;
		d.resolve(toAssign);
		if(success && d.isResolved() != true) //Check if isResolved return true after resolve.
			success = false;
		assertEquals(true, success);
	}
	
	@Test
	public void testResolve(){
		boolean success = true;
		callbackCounter = 0;
		Deferred<Integer> d = new Deferred<Integer>();
		Integer toAssign = new Integer(5);
		for(int i=0; i<7; i++){
		d.whenResolved(new Runnable(){
			public void run(){
				callbackCounter++;
			}
		});
		}
		d.resolve(toAssign);
		if(!d.isResolved())
			success = false;
		if(success && d.get()!=toAssign)
			success = false;
		if(success && callbackCounter != 7)
			success = false;
		assertEquals(true, success);
	}
	
	@Test
	public void testWhenResolved(){
		boolean success = true;
		callbackCounter = 0;
		Deferred<Integer> d = new Deferred<Integer>();
		Integer toAssign = new Integer(5);
		d.whenResolved(new Runnable(){
			public void run(){
				callbackCounter++;
			}
		});
		if(callbackCounter != 0){
			success = false;
		}
		d.whenResolved(new Runnable(){
			public void run(){
				callbackCounter++;
			}
		});
		if(success && callbackCounter != 0){
			success = false;
		}
		d.resolve(toAssign);
		if(success && callbackCounter != 2){
			success = false;
		}
		d.whenResolved(new Runnable(){
			public void run(){
				callbackCounter++;
			}
		});
		if(success && callbackCounter != 3){
			success = false;
		}
		assertEquals(true,success);
	}
}
