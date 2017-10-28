package bgu.spl.a2.sim.tools;

import java.util.Random;

import bgu.spl.a2.sim.Product;

public class RandomSumPliers implements Tool {
	
	/**
	* @return tool name as string
	*/
    public String getType(){
    	return "rs-pliers";
    }
    
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public long useOn(Product p){
    	//Random rand = new Random(p.getStartId());
    	
    	Random rand = new Random(p.getFinalId());
    	
    	long toReturn = 0;
    	for(int i = 0 ; i < p.getFinalId() % 10000 ; i ++){
    		toReturn += rand.nextInt(); 
    	}
    	return Math.abs(toReturn);
    }
}
