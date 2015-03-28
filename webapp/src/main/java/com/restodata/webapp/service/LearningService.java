package com.restodata.webapp.service;

import com.restodata.webapp.model.MenuItem;
import com.restodata.webapp.model.PredictRequest;
import com.restodata.webapp.model.PredictResult;
import com.restodata.webapp.model.machine.OrderFeature;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class LearningService {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static {
        init();
    }

    private static DataStore store;

    private static void init() {
        try {
            store = new DataStore("data.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addOrder(int restId, MenuItem item, String dateStr) {
        try {
            Calendar date = Calendar.getInstance();
            date.setTime(sdf.parse(dateStr));

            OrderFeature f = new OrderFeature(item.id, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.DAY_OF_WEEK),  date.get(Calendar.HOUR), 1);
            store.addOrder(f);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static PredictResult predict(int restId, PredictRequest predict) {
        //train
        Problem problem = new Problem();
        Model model;
        List<Feature[]> features;
        List<Double> targets;

        synchronized (store) {
            features = new ArrayList<Feature[]>(store.getCount());
            targets = new ArrayList<Double>(store.getCount());
            for (OrderFeature f : store.getFeatures()) {
                features.add(f.toFeatures());
                targets.add((double) f.count);
            }

        }
        problem.bias = -1;
        problem.n = OrderFeature.FEATURE_COUNT;
        problem.l = 1;

        problem.x = features.toArray(new Feature[features.size()][]);
        problem.y = new double[targets.size()];

        int i=0;
        for (Double d: targets) {
            problem.y[i++] = d;
        }

        SolverType solver = SolverType.L2R_LR; // -s 0
        double C = 1;    // cost of constraints violation
        double eps = 0.001; // stopping criteria
        Parameter parameter = new Parameter(solver, C, eps);
        System.out.println("l:"+problem.l+" n:"+problem.n);
        model = Linear.train(problem, parameter);
        double[] dv = new double[model.getNrClass()];
        System.out.println("labels: " + Arrays.toString(model.getLabels()));

        // --------------------------------------------
        // predict
        // --------------------------------------------
        List<MenuItem> items = MenuService.getMenuItems(restId);
        PredictResult result = new PredictResult(predict);

        for (MenuItem item : items) {
            int hour;
            for (hour = 0; hour < 24; hour++) {
                Feature[] predictFeatures = new OrderFeature(item.id, predict.year, predict.month, predict.dayOfMonth, predict.getDayOfWeek(), hour, 0).toFeatures();
                double count = Linear.predictProbability(model, predictFeatures, dv);
                result.add(item, hour, count);
            }
        }
        return result;
    }
}
