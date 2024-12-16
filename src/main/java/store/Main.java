package store;

import store.common.ProductLoader;
import store.common.PromotionLoader;
import store.common.Validator;
import store.product.SaleProducts;
import store.view.InputView;
import store.view.OutputView;

public class Main {
    public static void main(String[] args) {
        SaleProducts saleProducts = initializeStoreData();
        Validator validator = new Validator();
        InputView inputView = new InputView(validator);
        StoreService storeService = new StoreService(saleProducts);
        OutputView outputView = new OutputView();
        StoreController storeController = new StoreController(inputView, storeService, outputView, saleProducts);
        storeController.takeOrder();
    }

    private static SaleProducts initializeStoreData() {
        ProductLoader productLoader = new ProductLoader();
        PromotionLoader promotionLoader = new PromotionLoader();
        promotionLoader.registerPromotions();
        return productLoader.registerSaleProducts();
    }
}