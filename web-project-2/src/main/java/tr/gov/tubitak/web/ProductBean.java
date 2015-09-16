package tr.gov.tubitak.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import tr.gov.tubitak.entity.Product;

@ManagedBean
@SessionScoped
public class ProductBean implements Serializable{

	private List<Product> productList = new ArrayList<>();
	private Product product = new Product();
	
	private List<String> brandList = new ArrayList<String>();
	
	@PostConstruct
	public void init(){
		brandList.add("LG");
		brandList.add("Nokia");
		brandList.add("General Mobile");
	}
	
	public void save(){
		productList.add(product);
		this.product = new Product();
	}

	public void update(){
		this.product = new Product();
	}

	
	public Integer productStock(){
		return 100;
	}
	
	public void remove(Product product){
		this.productList.remove(product);
	}
	
	public void select(Product product){
		this.product = product;
	}
	
	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<String> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<String> brandList) {
		this.brandList = brandList;
	}
}
