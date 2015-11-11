package net.samagames.hydroangeas.client.resources;

import org.apache.commons.io.FileUtils;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        File logFile = new File(serverFolder, "servers/"+serverName+"/logs/latest.log");
        if(logFile.exists())
        {
            try {
                File folder = new File(logFolder, template);
                folder.mkdir();
                FileUtils.copyFile(logFile, new File(folder, serverName + "-" + getDate() + ".log"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        return simpleDateFormat.format(new Date());
    }
}
