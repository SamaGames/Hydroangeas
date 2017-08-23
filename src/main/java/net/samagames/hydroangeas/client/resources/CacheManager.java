package net.samagames.hydroangeas.client.resources;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.utils.MiscUtils;
import net.samagames.hydroangeas.utils.NetworkUtils;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

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
public class CacheManager
{

    private HydroangeasClient instance;

    CacheManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    File getServerFiles(String game)
    {
        return getUrlCache("servers", game, ".tar.gz");
    }

    File getMapFiles(String game, String map)
    {
        String fileName = game + "_" + map;

        return getUrlCache("maps", fileName, ".tar.gz");
    }

    File getDebFiles(ServerDependency dependency)
    {
        String fileName = dependency.getName() + "-" + dependency.getVersion();

        return getUrlCache("dependencies", fileName, dependency.getExt());
    }

    private File getUrlCache(String type, String fileName, String fileExt)
    {
        String checksumURL = this.instance.getTemplatesDomain() + type + "/checksum.php?file=" + fileName + "." + fileExt;
        String wgetURL = this.instance.getTemplatesDomain() + type + "/" + fileName + "." + fileExt;
        File cache = new File(this.instance.getServerFolder(), "cache/" + type + "/" + fileName + "." + fileExt);

        return getCache(wgetURL, checksumURL, cache);
    }

    private File getCache(String wgetURL, String checksumURL, File cache)
    {
        if (!cache.exists())
        {
            NetworkUtils.copyURLToFile(wgetURL, cache);
        } else
        {
            try
            {
                String remoteChecksum = NetworkUtils.readURL(checksumURL);
                if (!remoteChecksum.equals(MiscUtils.getSHA1(cache)))
                {
                    NetworkUtils.copyURLToFile(wgetURL, cache);
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
        //TODO clear cache every day
        NetworkUtils.copyURLToFile(remoteFile, archive);
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(archive, dest);
    }
}
