package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.tools.Tool;

public class ReleaseTask extends Task<Tool> {
	
	private Tool tool;
	
	public ReleaseTask(Tool tool){
		this.tool = tool;
	}

	@Override
	protected void start() {
		Simulator.warehouse.releaseTool(tool);
	}
}
