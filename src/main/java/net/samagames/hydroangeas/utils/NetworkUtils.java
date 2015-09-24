package net.samagames.hydroangeas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtils
{
    public static String readURL(String url)
    {
        try
        {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine = in.readLine();
            in.close();

            return inputLine;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String readFullURL(String url)
    {
        try
        {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = in.readLine()) != null)
                builder.append(line);

            in.close();

            return builder.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
