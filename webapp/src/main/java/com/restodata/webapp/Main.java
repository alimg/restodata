package com.restodata.webapp;

import com.google.gson.Gson;
import com.restodata.webapp.model.ApiRequest;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.service.MenuService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

public class Main extends NanoHTTPD{

    private Gson gson = new Gson();
    public Main() {
        super(8080);
    }
    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        System.out.println(method + " '" + uri + "' ");

        ApiRequest req = gson.fromJson(new BufferedReader(new InputStreamReader(session.getInputStream())), ApiRequest.class);
        ApiResponse response = null;
        if (req.action.equals("getMenu")) {
            response = MenuService.getMenu();
        } else if (req.action.equals("addOrder")) {
            response = MenuService.addOrder(req.order);
        } else if (req.action.equals("getPredictions")) {
            response = MenuService.getPrediction(req.predict);
        }

        return new NanoHTTPD.Response(gson.toJson(response));
    }


    public static void main(String[] args) {

        ServerRunner.run(Main.class);
    }
}
