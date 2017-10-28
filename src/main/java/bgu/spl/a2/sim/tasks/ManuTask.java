package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

public class ManuTask extends Task<Product> {
	
	private AtomicInteger toolUseCount;
	private Product p;
    private final ManufactoringPlan plan;
    
    public ManuTask(ManufactoringPlan plan, long startId){
    	this.plan = plan;
    	p = new Product(startId, plan.getProductName());
    	toolUseCount = new AtomicInteger(plan.getTools().length);
    }
    
    
	
	protected void start() {
		ArrayList<ManuTask> tasks = new ArrayList<ManuTask>();
		for(int i = 0; i < plan.getParts().length; i++){
			ManuTask task = new ManuTask(Simulator.warehouse.getPlan(plan.getParts()[i]), p.getStartId()+1);
	    	tasks.add(task);
	    	spawn(task);
		}
    	whenResolved(tasks, ()->{
    		for(int j = 0; j<tasks.size(); j++)
				p.addPart(tasks.get(j).getResult().get());
			if(plan.getTools().length == 0)
				this.complete(p);
			for(int i = 0; i < plan.getTools().length; i++){
				Deferred<Tool> toolDef = Simulator.warehouse.acquireTool(plan.getTools()[i]);
				toolDef.whenResolved(()->{
					Tool tool = toolDef.get();
					for(int j = 0; j<tasks.size(); j++){
						p.sumUseOnId(tool.useOn(tasks.get(j).getResult().get()));
					}
					spawn(new ReleaseTask(tool));
					if(toolUseCount.decrementAndGet() == 0){
						this.complete(p);
					}
				});
			}
		}); 
    }
}

