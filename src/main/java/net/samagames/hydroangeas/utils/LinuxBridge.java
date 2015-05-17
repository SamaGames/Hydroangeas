package net.samagames.hydroangeas.utils;

import java.io.File;
import java.io.IOException;

public class LinuxBridge
{
    public static void bash(String path)
    {
        exec(new String[] { "bash", path });
    }

    public static void mkdir(String path)
    {
        exec(new String[] { "mkdir", path });
    }

    public static void rm(String path, boolean folder)
    {
        exec(new String[] { "rm", (folder ? "-Rf" : null), path });
    }

    public static void cp(String from, String to, boolean recursively)
    {
        exec(new String[] { "cp", (recursively ? "-R" : null), from, to });
    }

    public static void mv(String from, String to)
    {
        exec(new String[] { "mv", from, to });
    }

    public static void wget(String url, String to)
    {
        exec(new String[] { "wget", "-P", to, url });
    }

    public static void gzipExtract(String archive, String to)
    {
        exec(new String[] { "tar", "-xzvf", archive, "-C", to });
    }

    public static void gzipMake(String archiveName, String what, String to)
    {
        exec(new String[] { "tar", "-czvf", to + (!to.endsWith(File.separator) ? File.separator : null) + archiveName, what });
    }

    public static void screenCreate(String name, String toExecute)
    {
        exec(new String[] { "screen", "-dmS", name, toExecute });
    }

    public static void screenKill(String name)
    {
        exec(new String[] { "screen", "-S", name, "-X", "quit" });
    }

    public static void exec(String[] commands)
    {
        try
        {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
