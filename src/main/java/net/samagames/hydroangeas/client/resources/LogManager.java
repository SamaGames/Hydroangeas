package net.samagames.hydroangeas.client.resources;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Silva on 11/11/2015.
 */
public class LogManager {

    private File logFolder;
    private File serverFolder;

    public LogManager(File rootFolder)
    {
        logFolder = new File(rootFolder, "serverlogs");
        logFolder.mkdir();

        serverFolder = rootFolder;
    }

    public boolean saveLog(String serverName, String template)
    {
        File logFile = new File(serverFolder, "/servers/"+serverName+"/logs/latest.log");
        if(logFile.exists())
        {
            try {
                FileUtils.copyFile(logFile, new File(serverFolder, "/"+template + "/" + serverName + "-" + new Date().toString() + ".txt"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
