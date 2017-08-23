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
