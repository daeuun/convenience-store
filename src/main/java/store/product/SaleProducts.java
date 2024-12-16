package store.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import store.promotion.Promotion;

public class SaleProducts {
    private final List<SaleProduct> saleProducts;

    public SaleProducts() {
        this.saleProducts = new ArrayList<>();
    }

    public List<SaleProduct> getSaleProducts() {
        return Optional.of(saleProducts).orElseGet(ArrayList::new);
    }

    public SaleProduct getSaleProductByProductName(String productName) {
        for (SaleProduct saleProduct : saleProducts) {
            if (saleProduct.findByProductName(productName))  {
                return saleProduct;
            }
        }
        return null;
    }

    /**
     * 같은 객체에 재고만 다른 판매 상품 객체를 추가한다.
     * SaleProduct 리스트에 product.getName()가 있으면? 기존 객체에, promotion 여부에 따라 재고 등록
     * 없으면? 새로운 객체 생성하고, promotion 여부에 따라 재고 등록
     */
    public void addSaleProduct(Long id, Product product, int quantity, Promotion promotion) {
        Optional<SaleProduct> optionalSaleProduct = getSaleProducts().stream()
                .filter(saleProduct -> saleProduct.findByProductName(product.getName()))
                .findFirst();
        if (optionalSaleProduct.isPresent()) {
            SaleProduct saleProduct = optionalSaleProduct.get();
            if (promotion != null) {
                saleProduct.updateStock(0, quantity);
            } else {
                saleProduct.updateStock(quantity, 0);
            }
        } else {
            SaleProduct newSaleProduct;
            if (promotion != null) {
                newSaleProduct = SaleProduct.of(id, product, 0, quantity, promotion);
            } else {
                newSaleProduct = SaleProduct.of(id, product, quantity, 0, null);
            }
            getSaleProducts().add(newSaleProduct);
        }
    }

}
