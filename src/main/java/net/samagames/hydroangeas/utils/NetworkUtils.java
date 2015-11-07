package net.samagames.hydroangeas.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
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

    public static void copyURLToFile(String rawURL, File destination)
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
            FileUtils.copyInputStreamToFile(inputStream, destination);
            //Inputstream closed in finally
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void copyURLToFile(URL url, File destination)
    {
        try
        {
            URLConnection urlConnection = url.openConnection();

            if (url.getUserInfo() != null) {
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes()));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            InputStream inputStream = urlConnection.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, destination);
            //Inputstream closed in finally
        } catch (IOException e)
        {
            e.printStackTrace();
        }
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
