package store.product;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private static final Map<String, Product> PRODUCT = new HashMap<>();

    private final String name;
    private final int price;

    private Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public static Product of(String productName, int price) {
        if (PRODUCT.get(productName) != null) {
            return PRODUCT.get(productName);
        } else {
            Product product = new Product(productName, price);
            PRODUCT.put(productName, product);
            return product;
        }
    }

    public ProductDTO toDTO() {
        return new ProductDTO(this.name, this.price);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
