package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	private ConcurrentLinkedDeque<?>[] pshHolders; //Array for holders orderd by PSH
	private ConcurrentLinkedDeque<?>[] pshQueues; //Array for queues orderd by PSH
	
	private ConcurrentHashMap<String,ManufactoringPlan> plans;
	
	
	/**
	* Constructor
	*/
    public Warehouse(){
    	pshHolders = new ConcurrentLinkedDeque<?>[3];
    	pshQueues = new ConcurrentLinkedDeque<?>[3];
    	for(int i=0; i<3; i++){
    		pshHolders[i] = new ConcurrentLinkedDeque<Tool>();
    		pshQueues[i] = new ConcurrentLinkedDeque<Deferred<Tool>>();
    	}
    	plans = new ConcurrentHashMap<String,ManufactoringPlan>();
    }

	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediately
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
	@SuppressWarnings("unchecked")
	public synchronized Deferred<Tool> acquireTool(String type){
    	Deferred<Tool> toReturn = new Deferred<Tool>();
    	int index = getIndex(type);
	    if(!pshHolders[index].isEmpty())
	    	toReturn.resolve((Tool)pshHolders[index].pop());
	    else
	    	((ConcurrentLinkedDeque<Deferred<Tool>>)pshQueues[index]).add(toReturn);
    	return toReturn;
    }

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    @SuppressWarnings("unchecked")
	public synchronized void releaseTool(Tool tool){
    	int index = getIndex(tool.getType());
	    if(!pshQueues[index].isEmpty())
	    	((Deferred<Tool>)pshQueues[index].pop()).resolve(tool);
    	else 
    		((ConcurrentLinkedDeque<Tool>)pshHolders[index]).add(tool);
    }

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product){
    		return plans.get(product);
    }
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan){
    		plans.put(plan.getProductName(), plan);
    }
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    @SuppressWarnings("unchecked")
	public void addTool(Tool tool, int qty){
    	switch(tool.getType()){
    		case ("rs-pliers"):
    			for(int i = 0 ; i < qty ; i ++){((ConcurrentLinkedDeque<Tool>)pshHolders[0]).add(new RandomSumPliers());}
    			break;
    		case ("gs-driver"): 
    			for(int i = 0 ; i < qty ; i ++){((ConcurrentLinkedDeque<Tool>)pshHolders[1]).add(new GcdScrewDriver());}
    			break;
    		case ("np-hammer"): 
    			for(int i = 0 ; i < qty ; i ++){((ConcurrentLinkedDeque<Tool>)pshHolders[2]).add(new NextPrimeHammer());}
    			break;
    	}
    }
    
   private int getIndex(String type){
	   int index = -1;
	   switch(type){
		case ("rs-pliers"):
			index = 0;
			break;
		case ("gs-driver"): 
			index = 1;
			break;
		case ("np-hammer"): 
			index = 2;
			break; 	
	}
	   return index;
   }
}

