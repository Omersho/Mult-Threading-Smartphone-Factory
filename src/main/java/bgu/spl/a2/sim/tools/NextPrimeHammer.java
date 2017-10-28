package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammer implements Tool {
	
	/**
	* @return tool name as string
	*/
    public String getType(){
    	return "np-hammer";
    }
    
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public long useOn(Product p){
    	boolean isPrime = false;
    	double square;
    	//long id = p.getStartId();
    	
    	long id = p.getFinalId();
    	
    	if(id<2)
    		return 2;
    	else{
    		if(id % 2 == 0){
    			id--;
    		}
    		while(!isPrime){
    			id+=2;
    			isPrime = true;
    			square = id;
    			square = Math.sqrt(square);
    			for(int i = 3 ; i <= square && isPrime ; i += 2){
    				if(id % i == 0)
    					isPrime = false;
    			}
    		}
    	}
    	return id;
    }
}
