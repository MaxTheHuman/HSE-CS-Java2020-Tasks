package ru.hse.cs.java2020.task03.trackerApiHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackerApiHandler {
    public static String searchByKey(String token, String orgId, String taskKey) throws IOException {
        final String commonUrl = "https://api.tracker.yandex.net/v2/";
        URL url;
        try {
            url = new URL(commonUrl + "issues/_search");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "url is invalid";
        }

        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "OAuth " + token);
            con.setRequestProperty("X-Org-Id", orgId);

            String jsonQueryString = "{\"query\": \"key: " + taskKey + "\"}";
            StringBuilder content = new StringBuilder();

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonQueryString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return content.toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "exception was catched";
        }
    }
}
