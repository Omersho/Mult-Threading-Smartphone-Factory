package bgu.spl.a2.sim.conf;

public class FutureProduct {
	String product;
	int qty;
	long startId;
	
	public FutureProduct(String product, int qty , long startId){
		this.product = product;
		this.qty = qty;
		this.startId = startId;
	}
	
	public String getProduct(){
		return product;
	}
	
	public int getQty(){
		return qty;
	}
	
	public long getStartId(){
		return startId;
	}
	
	public void setProduct(String product){
		this.product = product;
	}
	
	public void setQty(int qty){
		this.qty = qty;
	}
	
	public void setStartId(long startId){
		this.startId = startId;
	}
}
