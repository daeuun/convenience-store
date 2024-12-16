package store.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import store.promotion.Promotion;

public class PromotionLoader {
    private final static String SPLIT_DELIMITER = ",";

    public void registerPromotions() {
        List<String> loadData = DataLoader.loadData("promotions.md");
        List<String> promotionData = loadData.subList(1, loadData.size());
        for (String line : promotionData) {
            parsePromotion(line);
        }
    }

    private void parsePromotion(String promotionData) {
        String[] split = promotionData.split(SPLIT_DELIMITER);
        String name = split[0];
        int buy = Integer.parseInt(split[1]);
        int get = Integer.parseInt(split[2]);
        LocalDateTime startDate = formatLocalDate(split[3]);
        LocalDateTime endDate = formatLocalDate(split[4]);
        Promotion.of(name, buy, get, startDate, endDate);
    }

    private LocalDateTime formatLocalDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(stringDate, formatter).atStartOfDay();
    }
}
