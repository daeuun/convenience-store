package store.promotion;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Promotion {
    private static final Map<String, Promotion> PROMOTION = new HashMap<>();

    private final String name;
    private final int buy;
    private final int get;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private Promotion(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Promotion of(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        Promotion promotion = new Promotion(name, buy, get, startDate, endDate);
        PROMOTION.put(name, promotion);
        return promotion;
    }

    public static Promotion getByName(String name) {
        return PROMOTION.get(name);
    }

    public int getPromotionUnit() {
        return buy + get;
    }

    public int getBuyQuantity() {
        return buy;
    }

    public int getGetQuantity() {
        return get;
    }

    public boolean validatePromotionDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate.plusDays(1));
    }

    public PromotionDTO toDTO() {
        return new PromotionDTO(this.name, this.buy, this.get, this.startDate, this.endDate);
    }
}
