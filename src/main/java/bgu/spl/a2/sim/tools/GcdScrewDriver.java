package bgu.spl.a2.sim.tools;

import java.math.BigInteger;

import bgu.spl.a2.sim.Product;

public class GcdScrewDriver implements Tool {

	/**
	* @return tool name as string
	*/
	
    public String getType(){
    	return "gs-driver";
    }
    
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public long useOn(Product p){
    	/*long toReverse = Long.valueOf((new StringBuilder(Long.toString(p.getStartId()))).reverse().toString());
    	BigInteger id = java.math.BigInteger.valueOf(p.getStartId());*/
    	
    	long toReverse = Long.valueOf((new StringBuilder(Long.toString(p.getFinalId()))).reverse().toString());
    	BigInteger id = java.math.BigInteger.valueOf(p.getFinalId());
    	
    	BigInteger idReverse = java.math.BigInteger.valueOf(Long.valueOf(toReverse));
    	return id.gcd(idReverse).longValue();
    }
}
