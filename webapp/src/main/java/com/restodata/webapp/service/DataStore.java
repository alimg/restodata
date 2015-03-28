package com.restodata.webapp.service;

import com.restodata.webapp.model.machine.OrderFeature;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private List<OrderFeature> mFeatures;
    private String mFile;

    public DataStore(String file){
        try {
            loadFile(file);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            mFeatures = new ArrayList<OrderFeature>();
        }
    }

    private void loadFile(String file) throws FileNotFoundException {
        mFile = file;
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<OrderFeature> features = new ArrayList<OrderFeature>();
        String line;
        try {
            while ((line = reader.readLine())!=null) {
                String ar[] = line.split(",");
                features.add(new OrderFeature(Integer.parseInt(ar[0]),
                        Integer.parseInt(ar[1]),
                        Integer.parseInt(ar[2]),
                        Integer.parseInt(ar[3]),
                        Integer.parseInt(ar[4]),
                        Integer.parseInt(ar[5]),
                        Integer.parseInt(ar[6])));
            }
            reader.close();
        } catch (IOException e) {
        }
        mFeatures = features;
    }

    public void addOrder(OrderFeature f) {
        synchronized (this) {
            boolean found = false;
            for (int i = mFeatures.size() - 1; i >= 0; i--) {
                if (mFeatures.get(i).matches(f)) {
                    mFeatures.get(i).count += f.count;
                    found = true;
                    break;
                }
            }
            if (!found) {
                mFeatures.add(f);
            }
        }
    }

    private void saveFile() {

        try {
            FileWriter writer = new FileWriter(mFile);
            for(OrderFeature f:mFeatures) {
                writer.write(f.toCsv());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return mFeatures.size();
    }

    public List<OrderFeature> getFeatures() {
        return mFeatures;
    }

    public void commit() {
        synchronized (this) {
            saveFile();
        }
    }
}
