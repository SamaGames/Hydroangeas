package net.samagames.hydroangeas.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LinuxBridge
{
    private final ExecutorService executor;

    public LinuxBridge()
    {
        this.executor = Executors.newFixedThreadPool(1);
    }

    public void bash(String path)
    {
        this.exec(new String[]{"bash", path});
    }

    public void mkdir(String path)
    {
        this.exec(new String[]{"mkdir", path});
    }

    public void rm(String path, boolean folder)
    {
        this.exec(new String[]{"rm", (folder ? "-Rf" : null), path});
    }

    public void cp(String from, String to, boolean recursively)
    {
        this.exec(new String[]{"cp", (recursively ? "-R" : null), from, to});
    }

    public void mv(String from, String to)
    {
        this.exec(new String[]{"mv", from, to});
    }

    public void wget(String url, String to)
    {
        this.exec(new String[]{"wget", "-P", to, url});
    }

    public void gzipExtract(String archive, String to)
    {
        this.exec(new String[]{"tar", "-xzvf", archive, "-C", to});
    }

    public void gzipMake(String archiveName, String what, String to)
    {
        this.exec(new String[]{"tar", "-czvf", to + (!to.endsWith(File.separator) ? File.separator : null) + archiveName, what});
    }

    public void grantBash(String path)
    {
        this.exec(new String[]{"chmod", "+x", path});
    }

    public void chmod(int number, String path)
    {
        this.exec(new String[]{"chmod", String.valueOf(number), path});
    }

    public void mark2Start(String serverPath)
    {
        this.exec(new String[]{"mark2", "start", serverPath});
    }

    public void mark2Stop(String serverName)
    {
        this.exec(new String[]{"mark2", "kill", "-n", serverName});
    }

    public void sed(String before, String after, String path)
    {
        this.exec(new String[]{"sed", "-i", "s/" + before + "/" + after + "/", path});
    }

    public void exec(String[] commands)
    {
        this.executor.submit(() ->
        {
            try
            {
                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.redirectErrorStream(true);

                Process p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        });
    }
}
