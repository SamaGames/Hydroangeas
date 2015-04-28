package net.samagames.hydroangeas.utils;

import java.io.File;
import java.net.URISyntaxException;

public class MiscUtils
{
    public static File getJarFolder()
    {
        try
        {
            return new File(MiscUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
