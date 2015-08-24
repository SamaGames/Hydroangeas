package net.samagames.hydroangeas.client.resources;

import net.samagames.hydroangeas.client.HydroangeasClient;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 08/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CacheManager {

    private HydroangeasClient instance;

    public CacheManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public boolean getServerFiles(String game)
    {
        String checksumURL = this.instance.getTemplatesDomain() + "servers/?file=" + game;
        String wgetURL = this.instance.getTemplatesDomain() + "servers/" + game + ".tar.gz";

        return true;
    }

    public long getChecksum(File file) throws IOException {
        return FileUtils.checksumCRC32(file);
    }

    public void downloader(URL remoteFile, File archive, File dest) throws IOException {
        FileUtils.copyURLToFile(remoteFile, archive);
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(archive, dest);
    }
}
