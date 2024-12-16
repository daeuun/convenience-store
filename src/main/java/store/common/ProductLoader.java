package store.common;

import java.util.List;
import store.product.Product;
import store.product.SaleProducts;
import store.promotion.Promotion;

public class ProductLoader {
    private final static String SPLIT_DELIMITER = ",";
    private static final SaleProducts saleProducts = new SaleProducts();

    public SaleProducts registerSaleProducts() {
        List<String> loadData = DataLoader.loadData("products.md");
        List<String> productData = loadData.subList(1, loadData.size());
        for (String line : productData) {
            int idx = saleProducts.getSaleProducts().size();
            parseSaleProduct(line, idx + 1);
        }
        return saleProducts;
    }

    private void parseSaleProduct(String productData, long saleProductId) {
        String[] split = productData.split(SPLIT_DELIMITER);
        String productName = split[0];
        int price = Integer.parseInt(split[1]);
        Product product = generateProduct(productName, price);
        int quantity = Integer.parseInt(split[2]);
        Promotion promotion = generatePromotion(split[3]);
        saleProducts.addSaleProduct(saleProductId, product, quantity, promotion);
    }

    private Product generateProduct(String productName, int price) {
        return Product.of(productName, price);
    }

    private Promotion generatePromotion(String promotionName) {
        return Promotion.getByName(promotionName);
    }

}
