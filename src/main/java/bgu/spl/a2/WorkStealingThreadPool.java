package bgu.spl.a2;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */

public class WorkStealingThreadPool {
	protected boolean endOfProgram;
	private ConcurrentLinkedDeque<Task<?>>[] tasks; //tasks[i] contains the tasks of the i'th processor
	private Thread[] procs; //procs[i] is a thread running a processor which in charge of tasks in tasks[i] Queue
	private VersionMonitor[] monitors; // monitors[i] is a lock for the the i'th Queue
	/**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to thist class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
	@SuppressWarnings("unchecked")
	public WorkStealingThreadPool(int nthreads) {
        // Initialize Processors Threads and Their tasks Queues
    	tasks = new ConcurrentLinkedDeque[nthreads];
    	procs = new Thread[nthreads];
    	monitors = new VersionMonitor[nthreads];
    	
    	for(int i = 0 ; i < nthreads ; i++){
    		tasks[i] = new ConcurrentLinkedDeque<Task<?>>();
    		monitors[i] = new VersionMonitor();
    		procs[i] = new Thread(new Processor(i , this));
    	}
    	endOfProgram = false;
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
    	addTask((int)(Math.random() * tasks.length),task);
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
        /*
         * still need to think how to handle the exception that can be thrown after the interrupt method
         * and maybe to change the way of stopping them in order to make them stop gracefully
        for(int i = 0 ; i < procs.length ; i ++){
    		procs[i].interrupt();
    	}
         */
    	endOfProgram = true;
    	for(int i = 0 ; i < procs.length ; i ++){
    		procs[i].interrupt();
    	}
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
    	for(int i = 0 ; i < procs.length ; i++){
    		procs[i].start();
    	}
    	/*while(!endOfProgram){
        	System.out.println("here");
    	}*/
    }
    /*
     * As I implemented it, a processor attempts to steal half of another processors tasks, and if it manages to
     * steal one or more than the stealing process finishes and it continues to work on the tasks it has stolen
     * else, continues to try stealing from another processor in a circular way of working.
     */
    
    protected Task<?> fetchTask(int procId) throws NoSuchElementException{
    	return tasks[procId].removeFirst();
    }

    protected void addTask(int procId , Task<?> task){
    	tasks[procId].add(task);
    	monitors[procId].inc();
    	procs[procId].interrupt();
    }
    
    protected boolean stealTasks(int thief , int victim){
    	int stealAmount = tasks[victim].size() / 2; 
    	Task<?> stolen;
    	ArrayList<Task<?>> stolenList = new ArrayList<Task<?>>(stealAmount);
    	
    	/*
    	 * I'm not sure how to handle it yet because the queue can get empty and still the processor will try
    	 * to steal a task from another one, but maybe after it becomes empty the processor will wait and than 
    	 * the queue will be filled with items again... anyway a lot of shit can happen and it's probably not
    	 * explained how we should handle it in the instructions
    	 */
    	for(int i = 0 ; i < stealAmount ; i ++){
    		try{
    			stolen = tasks[victim].removeLast();
    			stolenList.add(stolen);	
    		} catch (NoSuchElementException ignored) {}
    	}
    	if(!stolenList.isEmpty()){
    		/*
    		 * I wanted to notify another thread only after adding all the tasks because if I notify him when
    		 * him when there's just one task, than half of the tasks would be 0;
    		 */
    		tasks[thief].addAll(stolenList);
    		monitors[thief].inc();
    		return true;
    	}else{
    		return false;
    	}
    }
    
    protected int getNumberOfProcs(){
    	return procs.length;
    }
    
    protected VersionMonitor getMonitor(int victim){
    	return monitors[victim];
    }
    
    protected boolean isEmpty(int procId){
    	return tasks[procId].isEmpty();
    }
    
    protected int numOfTasks(int procId){
    	return tasks[procId].size();
    }
}
