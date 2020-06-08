package ru.hse.cs.java2020.task03.trackerApiHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackerApiHandler {

    public static String getMySelf(String token, String orgId) {
        final String commonUrl = "https://api.tracker.yandex.net/v2/myself";
        URL url;
        try {
            url = new URL(commonUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "url is invalid";
        }

        HttpURLConnection con = null;
        String myId = "";

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "OAuth " + token);
            con.setRequestProperty("X-Org-Id", orgId);


            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;

                while ((line = in.readLine()) != null) {
                    JSONObject jsonobject = new JSONObject(line);

                    try {
                        myId =  Integer.toString(jsonobject.getInt("uid"));
                    } catch (JSONException e) {
                        myId = "-2 " + line;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            myId = "-3";
        }
        return myId;
    }

    public static String getComments(String token, String orgId, String key) {
        final String commonUrl = "https://api.tracker.yandex.net/v2/issues/" + key + "/comments";
        URL url;
        try {
            url = new URL(commonUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "url is invalid";
        }

        HttpURLConnection con = null;
        String comments = "";

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "OAuth " + token);
            con.setRequestProperty("X-Org-Id", orgId);


            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                String text = "";
                String author = "";

                while ((line = in.readLine()) != null) {
                    JSONArray jsonarray = new JSONArray(line);
                    if (jsonarray.length() == 0) {
                        comments = "\nThere are no comments for this task";
                        return comments;
                    }
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        try {
                            text = jsonobject.getString("text");
                        } catch (JSONException e) {
                            text = "";
                        }
                        try {
                            author = jsonobject.getJSONObject("createdBy").getString("display");
                        } catch (JSONException e) {
                            author = "There is no information about author for this comment";
                        }
                        comments += "\n- " + author + " has commented: " + text;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                comments = "-2";
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            comments = "-3";
        }
        return comments;
    }


    public static String searchByKey(String token, String orgId, String taskKey) {
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
            String answer = "";

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonQueryString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                String summary = "";
                String description = "";
                String author = "";
                String assignee = "";
                String key = "";
                JSONArray followersJSONArray = new JSONArray();
                String followers = "";
                String comments = "";

                while ((line = in.readLine()) != null) {

                    JSONArray jsonarray = new JSONArray(line);
                    if (jsonarray.length() == 0) {
                        answer = "There is no task with given key\n";
                        return answer;
                    }
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        try {
                            summary = jsonobject.getString("summary");
                        } catch (JSONException e) {
                            summary = "There is no summary for this task";
                        }
                        try {
                            description = jsonobject.getString("description");
                        } catch (JSONException e) {
                            description = "There is no description for this task";
                        }
                        try {
                            author = jsonobject.getJSONObject("createdBy").getString("display");
                        } catch (JSONException e) {
                            author = "There is no information about author for this task";
                        }
                        try {
                            assignee = jsonobject.getJSONObject("assignee").getString("display");
                        } catch (JSONException e) {
                            assignee = "There is no information about assignee for this task";
                        }
                        try {
                            key = jsonobject.getString("key");
                        } catch (JSONException e) {
                            key = "There is no key";
                        }
                        try {
                            followersJSONArray = jsonobject.getJSONArray("followers");
                            for (int j = 0; j < followersJSONArray.length(); j++) {
                                followers += "\n- " + followersJSONArray.getJSONObject(j).getString("display");
                            }
                        } catch (JSONException e) {
                            followers = "There are no followers of this task";
                        }
                    }
                }

                comments = getComments(token, orgId, key);
                answer = "key: " + key + "\n" +
                        "summary: " + summary + "\n" +
                        "description: " + description + "\n" +
                        "author: " + author + "\n" +
                        "assignee: " + assignee + "\n" +
                        "comments: " + comments + "\n" +
                        "followers: " + followers + "\n";
            } catch (IOException e) {
                e.printStackTrace();
                answer = "organisation ID or Token invalid\n";
            }

            return answer.toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "exception was catched";
        }
    }

    public static String searchMyTasks(String token, String orgId, String perPage, String page) {
        final String commonUrl = "https://api.tracker.yandex.net/v2/issues/_search" +
                "?perPage=" + perPage + "&page=" + page;
        URL url;
        try {
            url = new URL(commonUrl);
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

            String jsonQueryString = "{\"query\": \"\\\"assignee\\\": me() \\\"Sort By\\\": Updated DESC \"}";
            String answer = "";

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonQueryString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                String summary = "";
                String description = "";
                String author = "";
                JSONArray followersJSONArray = new JSONArray();
                String followers = "";
                String key = "";
                String comments = "";

                while ((line = in.readLine()) != null) {
                    JSONArray jsonarray = new JSONArray(line);
                    if (jsonarray.length() == 0) {
                        answer = "There is no more tasks\n";
                        return answer;
                    }
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        try {
                            summary = jsonobject.getString("summary");
                        } catch (JSONException e) {
                            summary = "There is no summary for this task";
                        }
                        try {
                            description = jsonobject.getString("description");
                        } catch (JSONException e) {
                            description = "There is no description for this task";
                        }
                        try {
                            author = jsonobject.getJSONObject("createdBy").getString("display");
                        } catch (JSONException e) {
                            author = "There is no information about author for this task";
                        }
                        try {
                            key = jsonobject.getString("key");
                        } catch (JSONException e) {
                            key = "There is no key";
                        }
                        try {
                            followersJSONArray = jsonobject.getJSONArray("followers");
                            for (int j = 0; j < followersJSONArray.length(); j++) {
                                followers += "\n- " + followersJSONArray.getJSONObject(j).getString("display");
                            }
                        } catch (JSONException e) {
                            followers = "There are no followers of this task";
                        }

                        comments = getComments(token, orgId, key);
                        answer += "key: " + key + "\n" +
                                "summary: " + summary + "\n" +
                                "description: " + description + "\n" +
                                "author: " + author + "\n" +
                                "comments: " + comments + "\n" +
                                "followers: " + followers + "\n" +
                                "\n";
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                answer = "organisation ID or Token invalid\n";
            }

            String nextPageLink = con.getHeaderField("Link");
            answer += "For next page click here:\n" + nextPageLink;
            return answer;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "exception was catched";
        }
    }

    public static String createTask(String token, String orgId,
                                    String queue, String summary,
                                    String description, String assignToMe) {
        final String commonUrl = "https://api.tracker.yandex.net/v2/issues";
        URL url;
        try {
            url = new URL(commonUrl);
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

            String jsonBodyString = "" +
                    " { " +
                        " \"summary\": \"" + summary + "\", " +
                        " \"queue\": \"" + queue + "\", "+
                        " \"description\": \"" + description + "\" ";
            if (assignToMe.equals("1")) {
                String myId = getMySelf(token, orgId);
                jsonBodyString += ", \"assignee\": \"" + myId + "\"";
            }
            jsonBodyString += " } ";
            String answer = "";

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBodyString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Thread.sleep(10);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                if ((con.getResponseCode() / 100) != 2) {
                    answer = "Input is incorrect\n";
                    return answer;
                }
                String line;
                String summaryIn = "";
                String descriptionIn = "";
                String author = "";
                String keyIn = "";

                while ((line = in.readLine()) != null) {
                    JSONObject jsonobject = new JSONObject(line);

                    try {
                        summaryIn = jsonobject.getString("summary");
                    } catch (JSONException e) {
                        summaryIn = "There is no summary for this task";
                    }
                    try {
                        descriptionIn = jsonobject.getString("description");
                    } catch (JSONException e) {
                        descriptionIn = "There is no description for this task";
                    }
                    try {
                        author = jsonobject.getJSONObject("createdBy").getString("display");
                    } catch (JSONException e) {
                        author = "There is no information about author for this task";
                    }
                    try {
                        keyIn = jsonobject.getString("key");
                    } catch (JSONException e) {
                        keyIn = "There is no key for this task";
                    }

                    answer += "key: " + keyIn + "\n" +
                            "summary: " + summaryIn + "\n" +
                            "description: " + descriptionIn + "\n" +
                            "author: " + author + "\n" +
                            "\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
                answer = "organisation ID or Token invalid\n";
            }

            return answer;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "exception was catched";
        }
    }
}
