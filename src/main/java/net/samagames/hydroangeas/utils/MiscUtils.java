package net.samagames.hydroangeas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MiscUtils
{
    public static File getJarFolder()
    {
        URL url;
        String extURL;

        try
        {
            url = MiscUtils.class.getProtectionDomain().getCodeSource().getLocation();
        }
        catch (SecurityException ex)
        {
            url = MiscUtils.class.getResource(MiscUtils.class.getSimpleName() + ".class");
        }

        extURL = url.toExternalForm();

        if (extURL.endsWith(".jar"))
            extURL = extURL.substring(0, extURL.lastIndexOf("/"));
        else
        {
            String suffix = "/" + (MiscUtils.class.getName()).replace(".", "/") + ".class";
            extURL = extURL.replace(suffix, "");

            if (extURL.startsWith("jar:") && extURL.endsWith(".jar!"))
                extURL = extURL.substring(4, extURL.lastIndexOf("/"));
        }

        try
        {
            url = new URL(extURL);
        }
        catch (MalformedURLException ignored) {}

        try
        {
            return new File(url.toURI());
        }
        catch(URISyntaxException ex)
        {
            return new File(url.getPath());
        }
    }

    public static String getApplicationDirectory()
    {
        String jarDir = null;

        try
        {
            CodeSource codeSource = MiscUtils.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8"));
            jarDir = jarFile.getParentFile().getPath();
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }

        return jarDir + "/";
    }

    public static String getSHA1(File f) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(f);

        byte[] data = new byte[1024];
        int read = 0;
        while ((read = fis.read(data)) != -1) {
            sha1.update(data, 0, read);
        };
        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static int calculServerWeight(String game, int maxSlot, boolean isCoupaing)
    {
        int weight = 0;

        //GameType
        switch (game)
        {
            case "uhc":
                weight += 40;
                break;
            case "uhcrun":
                weight += 60;
                break;
            case "quake":
                weight += 20;
                break;
            case "uppervoid":
                weight += 25;
                break;
            case "herobattle":
                weight += 30;
                break;
            case "dimension":
                weight += 30;
                break;
            default:
            break;
        }

        //SlotNumber
        weight += maxSlot * 1;

        //Is coupaing
        if(isCoupaing)
        {
            weight += 50;
        }

        return weight;
    }
}
