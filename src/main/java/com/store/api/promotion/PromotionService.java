package com.store.api.promotion;

import com.store.api.order.model.OrderDetail;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import com.store.api.promotion.model.Promotion;
import com.store.api.promotion.resource.PromotionRepository;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    private final SaleProductRepository saleProductRepository;
    private final PromotionRepository promotionRepository;

    public PromotionService(SaleProductRepository saleProductRepository, PromotionRepository promotionRepository) {
        this.saleProductRepository = saleProductRepository;
        this.promotionRepository = promotionRepository;
    }

    public boolean validatePromotionPeriod(Long now) {
        return promotionRepository.isPromotionPeriod(now);
    }

    public int calculatePromotionGetQuantity(Long productId, int orderQuantity) {
        return getPromotionGetQuantity(productId, orderQuantity);
    }

    public int calculateInsufficientPromotionStock(OrderDetail orderDetail, AtomicInteger orderQuantity, boolean acceptInsufficientStockOrder) {
        int insufficientStock = getInsufficientPromotionStock(orderDetail.productId(), orderDetail.quantity());
        if (acceptInsufficientStockOrder) {
            return getPromotionGetQuantity(orderDetail.productId(), orderDetail.quantity());
        }
        orderQuantity.updateAndGet(current -> current - insufficientStock);
        return 0;
    }

    private int getInsufficientPromotionStock(Long productId, int orderQuantity) {
        SaleProduct saleProduct = saleProductRepository.findByProductId(productId);
        Promotion promotion = promotionRepository.findById(saleProduct.getPromotionId());
        int promotionUnit = promotion.getGet() + promotion.getBuy();
        int availableUnit = saleProduct.getPromotionStock() / promotionUnit;
        int availableToOrder = promotionUnit * availableUnit;
        return orderQuantity - availableToOrder;
    }

    public int calculatePromotionWithComplimentary(OrderDetail orderDetail, AtomicInteger orderQuantity, boolean acceptComplimentary) {
        SaleProduct saleProduct = saleProductRepository.findByProductId(orderDetail.productId());
        Promotion promotion = promotionRepository.findById(saleProduct.getPromotionId());
        int complimentaryQuantity = promotion.getGet();
        if (acceptComplimentary) {
            int updatedQuantity = orderDetail.quantity() + complimentaryQuantity;
            orderQuantity.addAndGet(complimentaryQuantity);
            return getPromotionGetQuantity(orderDetail.productId(), updatedQuantity);
        }
        return 0;
    }

    public int getPromotionGetQuantity(Long productId, int orderQuantity) {
        SaleProduct saleProduct = saleProductRepository.findByProductId(productId);
        int availableQuantity = Math.min(orderQuantity, saleProduct.getPromotionStock());
        Promotion promotion = promotionRepository.findById(saleProduct.getPromotionId());
        int promotionUnit = promotion.getBuy() + promotion.getGet();
        return availableQuantity / promotionUnit;
    }

}
