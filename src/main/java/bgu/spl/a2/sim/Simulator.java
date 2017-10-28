/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.FutureProduct;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.conf.Wave;
import bgu.spl.a2.sim.tasks.WaveTask;
import bgu.spl.a2.sim.tools.*;



/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	public static AtomicInteger a = new AtomicInteger(0);
	
	private static String jsonPath;
	private static WorkStealingThreadPool pool;
	private static ArrayDeque<WaveTask> waves;
	public static Warehouse warehouse;
	private static ConcurrentLinkedQueue<Product>  toReturn;
	private static CountDownLatch l;
	private static int currWave;
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
    public static ConcurrentLinkedQueue<Product> start(){
		Gson gson = new Gson();
    	JsonObject objFromFile;
    	JsonParser parser;
		BufferedReader br = null;
		try { 
			br = new BufferedReader(new FileReader(jsonPath));  
			parser = JsonParser.class.newInstance();
			StringBuilder jsonFile = new StringBuilder();
		    String line;
		    while((line = br.readLine()) != null){
		    	jsonFile.append(line);
		    }
		    objFromFile = (JsonObject) parser.parse(jsonFile.toString());
		    warehouse = new Warehouse();
		    addPlans(objFromFile,parser,gson);
		    addTools(objFromFile,parser,gson);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	currWave = 0;
    	waves = new ArrayDeque<WaveTask>();
    	addWave(objFromFile,parser,gson);
    	toReturn = new ConcurrentLinkedQueue<Product>();
    	pool.start();
    	while(!waves.isEmpty()){
    		l =  new CountDownLatch(1);
    		waves.peek().getResult().whenResolved(()->{
    			toReturn.addAll(waves.peek().getResult().get());
    			l.countDown();
    		});
    		pool.submit(waves.peek());
    		try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		waves.pop();
    		addWave(objFromFile,parser,gson);
    	}
 	   	try {
			pool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return toReturn;
    }
	
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
    	pool = myWorkStealingThreadPool;
	}

	
	public static void main(String [] args){
		Gson gson = new Gson();
		BufferedReader br = null;
		jsonPath = args[0]; //Get by prompt option
		//jsonPath = args[1]; //Default path, for check reasons
		try {
			JsonParser parser = JsonParser.class.newInstance();
			br = new BufferedReader(new FileReader(jsonPath));  
		    StringBuilder jsonFile = new StringBuilder();
		    String line;
		    while((line = br.readLine()) != null){
		    	jsonFile.append(line);
		    }
		    JsonObject objFromFile = (JsonObject) parser.parse(jsonFile.toString());
		    attachWorkStealingThreadPool(new WorkStealingThreadPool(getNumOfThreads(objFromFile,parser,gson)));
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		ConcurrentLinkedQueue<Product> SimulationResult;
		SimulationResult = start();
		
		
		//Tests
		/*
		while(!SimulationResult.isEmpty()){
			Product p = SimulationResult.remove();
					printParts(p,0);	
		}*/
		
		
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream("result.ser");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(fout);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			oos.writeObject(SimulationResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(fout != null){
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static int getNumOfThreads(JsonObject objFromFile , JsonParser parser, Gson gson){
	    return objFromFile.get("threads").getAsInt();
	    
	}
	
	
	private static void addPlans(JsonObject objFromFile , JsonParser parser, Gson gson){
	    JsonArray plansArray = objFromFile.getAsJsonArray("plans");
	    for(int i = 0 ; i < plansArray.size() ; i++)
	    	warehouse.addPlan(gson.fromJson(plansArray.get(i), ManufactoringPlan.class));
	}
	
	private static void addTools(JsonObject objFromFile , JsonParser parser, Gson gson){
	    JsonArray WarehouseToolsArray = objFromFile.getAsJsonArray("tools");
	    WarehouseTool tool;
	    for(int i = 0 ; i < WarehouseToolsArray.size() ; i++){
	    	tool = gson.fromJson(WarehouseToolsArray.get(i), WarehouseTool.class);
	    	if(tool.getTool().contains("plier"))
	    		warehouse.addTool(new RandomSumPliers(), tool.getQty());
	    	if(tool.getTool().contains("driver"))
	    		warehouse.addTool(new GcdScrewDriver(), tool.getQty());
	    	if(tool.getTool().contains("hammer"))
	    		warehouse.addTool(new NextPrimeHammer(), tool.getQty());
	    }
	}
	
	private static void addWave(JsonObject objFromFile , JsonParser parser, Gson gson){
		JsonArray jsonWave;
		JsonArray wavesArray = objFromFile.getAsJsonArray("waves");
		if(currWave < wavesArray.size()){
			jsonWave = wavesArray.get(currWave).getAsJsonArray();
	    	FutureProduct[] wave = new FutureProduct[jsonWave.size()];
	    	for(int i = 0 ; i < jsonWave.size() ; i++){
	    		wave[i] = gson.fromJson(jsonWave.get(i),FutureProduct.class);;
	    	}
	    	waves.add(new WaveTask(new Wave(wave)));
	    	currWave++;
		}
	}
	
	//Printing for check reasons
	/*private static void printParts(Product p, int numOfTabs){
		for(int i = 0 ; i < numOfTabs ; i++){
			System.out.print("  ");
		}
		System.out.println(p.getName() + " startId: " + p.getStartId()+" finalId: " + p.getFinalId());
		for(Product p1 : p.getParts()){
			printParts(p1,numOfTabs+1);
			System.out.println();
			}
	}*/
}
