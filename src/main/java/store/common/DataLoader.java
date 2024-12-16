package store.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataLoader {
    public static List<String> loadData(String name) {
        try {
            List<String> data = new ArrayList<>();
            InputStream inputStream = Objects.requireNonNull(
                    DataLoader.class.getClassLoader().getResource(name)).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedReader.lines().forEach(data::add);
            inputStream.close();
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

}
