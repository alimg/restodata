package com.restodata.webapp;

import com.google.gson.Gson;
import com.restodata.webapp.model.ApiRequest;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.service.MenuService;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        Map<String, String> headers = session.getHeaders();

        int contentLength = Integer.parseInt(headers.get("content-length"));
        byte[] body = new byte[0];
        try {
            DataInputStream dataInputStream = new DataInputStream(session.getInputStream());
            body = new byte[contentLength];
            dataInputStream.readFully(body, 0, contentLength);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = new String(body);
        System.out.println("req: "+text);
        ApiRequest req = gson.fromJson(text, ApiRequest.class);
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
