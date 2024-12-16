package store.product;

import store.order.OrderItem;
import store.promotion.Promotion;
import store.promotion.PromotionDTO;

public class SaleProduct {
    private Long id;
    private final Product product;
    private int regularStock;
    private int promotionStock;
    private final Promotion promotion;

    private SaleProduct(Long id, Product product, int regularStock, int promotionStock, Promotion promotion) {
        this.id = id;
        this.product = product;
        this.regularStock = regularStock;
        this.promotionStock = promotionStock;
        this.promotion = promotion;
    }

    public static SaleProduct of(Long id, Product product, int regularStock, int promotionStock, Promotion promotion) {
        return new SaleProduct(id, product, regularStock, promotionStock, promotion);
    }

    public void updateStock(int regularStock, int promotionStock) {
        this.regularStock += regularStock;
        this.promotionStock += promotionStock;
    }

    public SaleProductDTO toDTO() {
        PromotionDTO promotionDTO = null;
        if (this.promotion != null) {
            promotionDTO = this.promotion.toDTO();
        }
        return new SaleProductDTO(
                this.product.toDTO(),
                this.regularStock,
                this.promotionStock,
                promotionDTO
        );
    }

    public boolean findByProductName(String name) {
        return product.getName().equals(name);
    }

    public boolean isPromotionPeriod() {
        if (promotion == null) {
            return false;
        }
        return promotion.validatePromotionDate();
    }

    public boolean isEnoughStockToPurchase(int purchaseQuantity, boolean isPromotion) {
        if (isPromotion) {
            int totalStock = regularStock + promotionStock;
            return totalStock >= purchaseQuantity;
        }
        return regularStock >= purchaseQuantity;
    }

    public int getPromotionBuyQuantity() {
        return promotion.getBuyQuantity();
    }

    public int getPromotionGetQuantity() {
        return promotion.getGetQuantity();
    }

    public int getPromotionStock() {
        return promotionStock;
    }

    public int insufficientPromotionStock(int orderQuantity) {
        int promotionUnit = promotion.getGetQuantity() + promotion.getBuyQuantity();
        int availableUnit = promotionStock / promotionUnit;
        int availableToOrder = promotionUnit * availableUnit;
        return orderQuantity - availableToOrder;
    }

    public void purchasePromotion(int orderQuantity) {
        this.promotionStock -= orderQuantity;
    }

    public void purchaseRegular(int orderQuantity) {
        this.regularStock -= orderQuantity;
    }

    public OrderItem createOrderItem(int quantity, int promotionQuantity) {
        return OrderItem.of(product.getName(), product.getPrice(), quantity, promotionQuantity);
    }

    public int calculateTotalGet(int orderQuantity) {
        int promotionUnit = promotion.getBuyQuantity() + promotion.getGetQuantity();
        return orderQuantity / promotionUnit;
    }
}
