package bgu.spl.a2.sim.conf;

public class Wave {
	FutureProduct[] products;
	
	public Wave(FutureProduct[] products){
		this.products = products;
	}
	
	public FutureProduct[] getProducts(){
		return products;
	}
	
	public void setProducts(FutureProduct[] products){
		this.products = products;
	}
	
	public FutureProduct getProduct(int index){
		return products[index];
	}
	
}
