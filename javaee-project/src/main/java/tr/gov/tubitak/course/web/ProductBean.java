package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import tr.gov.tubitak.course.entity.Product;

@Named
@SessionScoped
public class ProductBean implements Serializable {

	private Product product = new Product();

	private List<Product> productList;

	@PostConstruct
	public void updateList() {
		this.setProductList(entityManager.createQuery("from Product").getResultList());
	}

	@PersistenceContext
	EntityManager entityManager;

	@Inject
	UserTransaction utx;

	public void save() throws Exception {
		utx.begin();
		entityManager.persist(product);
		utx.commit();
		updateList();
		this.product = new Product();
		System.out.println("Kayit yapildi");

	}

	public void remove(Product selectedProduct) throws Exception {
		utx.begin();
		selectedProduct = entityManager.merge(selectedProduct);
		entityManager.remove(selectedProduct);
		utx.commit();
		updateList();

	}

	public void select(Product selectedProduct) throws Exception {
		this.product = selectedProduct;

	}

	public void update() throws Exception {
		utx.begin();
		entityManager.merge(this.product);
		utx.commit();
		updateList();

	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

}
