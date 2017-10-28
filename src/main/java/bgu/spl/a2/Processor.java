package bgu.spl.a2;

import java.util.NoSuchElementException;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {
	
    private final WorkStealingThreadPool pool;
    private final int id;

    private int victim;
    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    
        victim = (id+1) % pool.getNumberOfProcs();
    }

    @Override
    public void run() {
    	while(!pool.endOfProgram){
    		try{
    			Task<?> task = pool.fetchTask(id);
    			task.handle(this);
    		} catch (NoSuchElementException ignored) {
    				steal();
    		}
    	}
    }
    
    public void steal(){
    	int version = pool.getMonitor(victim).getVersion();
    	while(pool.isEmpty(id) && !pool.stealTasks(id, victim) && !pool.endOfProgram){
    		try {	
    			pool.getMonitor(victim).await(version);
    			//pool.getMonitor(victim).await(version,id,pool.numOfTasks(id),victim,pool.numOfTasks(victim));
    		} catch (InterruptedException ignored) {
						
			}
    		version = pool.getMonitor(victim).getVersion();
    	}
		victim = (victim+1) % pool.getNumberOfProcs();
		if(victim == id ){
			victim = (victim + 1) % pool.getNumberOfProcs();
		}
    }
    
    protected void addTask(Task<?> task){
    	pool.addTask(id, task);
    }

    public int getId(){
    	return id;
    }
}
