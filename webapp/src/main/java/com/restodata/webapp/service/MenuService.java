package com.restodata.webapp.service;

import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.MenuItem;
import com.restodata.webapp.model.MenuResponse;
import com.restodata.webapp.model.OrderRequest;
import com.restodata.webapp.model.PredictRequest;
import com.restodata.webapp.model.PredictResult;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuService {

    private static int exampleRestId;
    private static final ArrayList<MenuItem> exampleMenuItems;

    static {
        exampleRestId = 0;
        exampleMenuItems = new ArrayList<MenuItem>();
        exampleMenuItems.add(new MenuItem(0, "Yemek", "Peynirli Pizza", 11));
        exampleMenuItems.add(new MenuItem(1, "Yemek", "Bazlama Ayvalik", 5));
        exampleMenuItems.add(new MenuItem(2, "Tatli", "Cheesecake", 7));
        exampleMenuItems.add(new MenuItem(3, "Icecek", "Ayran", 2.5f));
    }

    public static ApiResponse getMenu() {
        ApiResponse response = new ApiResponse("success", new MenuResponse(exampleMenuItems));
        return response;
    }

    public static ApiResponse addOrder(OrderRequest order) {
        ApiResponse response;
        try {
            MenuItem item = matchKeywords(order.keywords, exampleMenuItems);
            response = new ApiResponse("success", item);

            LearningService.addOrder(exampleRestId, item, order.date);
        } catch (ItemNotMatchedException e) {
            e.printStackTrace();
            response = new ApiResponse("fail");
        }
        return response;
    }


    private static MenuItem matchKeywords(List<String> keywords, List<MenuItem> items) throws ItemNotMatchedException {
        MenuItem bestMatch = null;
        double maxPoint=0.1;
        for (String val: keywords) {
            for(MenuItem item: items) {
                double point = StringUtils.getJaroWinklerDistance(item.name, val);
                if (maxPoint>point) {
                    maxPoint = point;
                    bestMatch = item;
                }
            }
        }
        if (bestMatch != null)
            return bestMatch;
        throw new ItemNotMatchedException();
    }

    public static ApiResponse getPrediction(PredictRequest predict) {
        PredictResult predictResult = LearningService.predict(exampleRestId, predict);
        return new ApiResponse("success", predictResult);
    }

    public static List<MenuItem> getMenuItems(int restId) {
        return exampleMenuItems;
    }
}
