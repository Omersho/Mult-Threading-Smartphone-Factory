package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionMonitorTest {
	
	@Test
	public void testGetVersion(){
		VersionMonitor monitor = new VersionMonitor();
		boolean ans = true;
		if(monitor.getVersion() != 0){
			ans = false;
		}
		monitor.inc();
		monitor.inc();
		if(monitor.getVersion() != 2){
			ans = false;
		}
		assertEquals(true,ans);
	}
	
	@Test
	public void testInc(){
		VersionMonitor monitor = new VersionMonitor();
		int prevVersion = monitor.getVersion();
		for(int i = 0 ; i < 13 ; i++){
			monitor.inc();
		}
		assertEquals(monitor.getVersion(), prevVersion+13);
	}
	
	@Test
	public void testAwait(){
		VersionMonitor versionMonitor = new VersionMonitor();
		int prevVersion = versionMonitor.getVersion();
		Thread incThread = new Thread(new Runnable(){
			public void run(){
				versionMonitor.inc();
			}
		});
		incThread.start();
		try {
			versionMonitor.await(prevVersion);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			incThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(versionMonitor.getVersion(), prevVersion+1);
	}

}
