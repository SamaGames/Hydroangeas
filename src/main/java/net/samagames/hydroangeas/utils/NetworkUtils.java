package net.samagames.hydroangeas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtils
{
    public static String readURL(String rawURL)
    {
        try
        {
            URL url = new URL(rawURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

            String inputLine = in.readLine();
            in.close();

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
