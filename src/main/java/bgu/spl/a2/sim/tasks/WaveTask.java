package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.conf.Wave;

public class WaveTask extends Task<ArrayList<Product>> {
	
	Wave wave;
	
	public WaveTask(Wave wave){
		this.wave = wave;
	}
		
	
	protected void start() {
		ManuTask task;
		ArrayList<ManuTask> tasks = new ArrayList<ManuTask>();
		for(int i = 0 ; i < wave.getProducts().length ; i++){
			for(int j = 0 ; j < wave.getProduct(i).getQty() ; j++){
				task = new ManuTask(Simulator.warehouse.getPlan(wave.getProduct(i).getProduct()), wave.getProduct(i).getStartId()+j);
				tasks.add(task);
				spawn(task);
			}
		}
			whenResolved(tasks,()->{
				ArrayList<Product> products = new ArrayList<Product>(tasks.size());
				for ( int i = 0 ; i < tasks.size() ; i ++){
					products.add(tasks.get(i).getResult().get());
				}
	        	this.complete(products);
			});
	}
}

