import javax.persistence.*;

import org.koiroha.jyrobot.model.Product;


public class JPATwst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpatest");
		EntityManager manager = factory.createEntityManager();
//		System.out.println("-------");
//		TypedQuery<Product> query = manager.createQuery("select product from Product product", Product.class);
//		for(Product p: query.getResultList()){
//			System.out.printf("[%d] %s: %d円%n", p.getId(), p.getName(), p.getPrice());
//		}
		manager.find(null, null, LockModeType.)
		Product product = manager.find(Product.class, 2);
		System.out.printf("[%d] %s: %d円%n",
			product.getId(), product.getName(), product.getPrice());
		manager.close();
		factory.close();
	}

}
