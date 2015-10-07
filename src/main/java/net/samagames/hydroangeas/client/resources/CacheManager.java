package net.samagames.hydroangeas.client.resources;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.ServerDependency;
import net.samagames.hydroangeas.utils.MiscUtils;
import net.samagames.hydroangeas.utils.NetworkUtils;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 08/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CacheManager
{

    private HydroangeasClient instance;

    public CacheManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public File getServerFiles(String game)
    {

        String checksumURL = this.instance.getTemplatesDomain() + "servers/checksum.php?file=" + game;
        String wgetURL = this.instance.getTemplatesDomain() + "servers/" + game + ".tar.gz";
        File cache = new File(this.instance.getServerFolder(), "cache/servers/" + game + ".tar.gz");

        return getCache(wgetURL, checksumURL, cache);
    }

    public File getMapFiles(String game, String map)
    {
        String fileName = game + "_" + map;

        String checksumURL = this.instance.getTemplatesDomain() + "maps/checksum.php?file=" + fileName;
        String wgetURL = this.instance.getTemplatesDomain() + "maps/" + fileName + ".tar.gz";
        File cache = new File(this.instance.getServerFolder(), "cache/maps/" + fileName + ".tar.gz");

        return getCache(wgetURL, checksumURL, cache);
    }

    public File getDebFiles(ServerDependency dependency)
    {
        String fileName = dependency.getName() + "-" + dependency.getVersion();

        String checksumURL = this.instance.getTemplatesDomain() + "dependencies/checksum.php?file=" + fileName + "." + dependency.getExt();
        String wgetURL = this.instance.getTemplatesDomain() + "dependencies/" + fileName + "." + dependency.getExt();
        File cache = new File(this.instance.getServerFolder(), "cache/dependencies/" + fileName + "." + dependency.getExt());

        return getCache(wgetURL, checksumURL, cache);
    }

    public File getCache(String wgetURL, String checksumURL, File cache)
    {
        if (!cache.exists())
        {
            try
            {
                FileUtils.copyURLToFile(new URL(wgetURL), cache);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } else
        {
            String remoteChecksum = NetworkUtils.readURL(checksumURL);

            try
            {
                if (!remoteChecksum.equals(MiscUtils.getSHA1(cache)))
                {
                    try
                    {
                        FileUtils.copyURLToFile(new URL(wgetURL), cache);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchAlgorithmException | IOException e)
            {
                e.printStackTrace();
            }
        }
        return cache;
    }

    public long getChecksum(File file) throws IOException
    {
        return FileUtils.checksumCRC32(file);
    }

    public void downloader(URL remoteFile, File archive, File dest) throws IOException
    {
        FileUtils.copyURLToFile(remoteFile, archive);
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(archive, dest);
    }
}
