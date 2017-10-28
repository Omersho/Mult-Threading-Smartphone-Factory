package bgu.spl.a2.sim.tools;


public class WarehouseTool {
		String tool;
		int qty;
		
		public WarehouseTool(String tool, int qty){
			this.tool = tool;
			this.qty = qty;
		}
		
		public String getTool(){
			return tool;
		}
		
		public int getQty(){
			return qty;
		}
		
		public void setTool(String tool){
			this.tool = tool;
		}
		
		public void setQty(int qty){
			this.qty = qty;
		}
}


