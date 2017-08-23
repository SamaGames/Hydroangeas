package net.samagames.hydroangeas.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class NetworkUtils
{
    private NetworkUtils()
    {

    }

    public static String readURL(String rawURL) throws IOException {
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
            throw e;
        }
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
