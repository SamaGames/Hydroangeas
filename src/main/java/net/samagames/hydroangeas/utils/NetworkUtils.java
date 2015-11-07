package net.samagames.hydroangeas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class NetworkUtils
{
    public static String readURL(String rawURL)
    {
        try
        {
            URL url = new URL(rawURL);
            URLConnection urlConnection = url.openConnection();

            if (url.getUserInfo() != null) {
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes()));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String inputLine = in.readLine();
            in.close();
            inputStream.close();

            return inputLine;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static String readFullURL(String rawURL)
    {
        try
        {
            URL url = new URL(rawURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = in.readLine()) != null)
                builder.append(line);

            in.close();

            return builder.toString();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
