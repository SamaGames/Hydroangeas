package net.samagames.hydroangeas.client.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.log.StackTraceData;
import net.samagames.restfull.LogLevel;
import net.samagames.restfull.RestAPI;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 05/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerThread extends Thread
{

    public boolean isServerProcessAlive;
    public Process server;
    public File directory;
    private long lastHeartbeat = System.currentTimeMillis();
    private ExecutorService executor;
    private MinecraftServerC instance;
    private static final Pattern LOG_PATTERN = Pattern.compile("\\[\\d{1,2}:\\d{2}:\\d{2} (INFO|WARN|ERROR)\\]: (.*)");
    private static final Pattern BEGIN_OF_STACKTRACE_PATTERN = Pattern.compile("([a-zA-Z\\.]*Exception)");
    private static final Pattern CONTENT_OF_STACKTRACE_PATTERN = Pattern.compile("((\\tat|Caused by).*)");
    private static final Pattern END_OF_STACKTRACE_PATTERN = Pattern.compile("((\\t\\.\\.\\.).*)");
    private StackTraceData stackTraceData;

    public ServerThread(MinecraftServerC instance, String[] command, String[] env, File directory)
    {
        this.instance = instance;
        this.executor = Executors.newFixedThreadPool(5);
        try
        {
            this.directory = directory;

            Thread.sleep(10);

            server = Runtime.getRuntime().exec(command, env, directory);
            isServerProcessAlive = true;

            executor.execute(() -> {
                try
                {
                    String line = null;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(server.getErrorStream())))
                    {
                        while (isServerProcessAlive && (line = reader.readLine()) != null)
                        {
                            RestAPI.getInstance().log(LogLevel.ERROR, instance.getServerName(), line);
                            System.err.println(instance.getServerName() + " > " + line);
                            //TODO handle errors
                        }
                    }
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            });

            executor.execute(() -> {
                try
                {
                    String line = null;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream())))
                    {
                        while (isServerProcessAlive && (line = reader.readLine()) != null)
                        {
                            lastHeartbeat = System.currentTimeMillis();
                            Matcher matcherLog = LOG_PATTERN.matcher(line);
                            Matcher matcherContentStack = CONTENT_OF_STACKTRACE_PATTERN.matcher(line);
                            Matcher matcherBeginStack = BEGIN_OF_STACKTRACE_PATTERN.matcher(line);
                            Matcher matcherEndStack = END_OF_STACKTRACE_PATTERN.matcher(line);
                            // Correct logs
                            if (matcherLog.matches())
                            {
                                String logType = matcherLog.group(1);
                                String log = matcherLog.group(2);
                                switch (logType)
                                {
                                    default:
                                        break;
                                    case "SEVERE":
                                        RestAPI.getInstance().log(LogLevel.ERROR, "Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName(), log);
                                        break;
                                    case "ERROR":
                                        RestAPI.getInstance().log(LogLevel.WARINING, "Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName(), log);
                                        break;
                                }

                                // This should be impossible
                                if (this.stackTraceData != null)
                                {
                                    this.stackTraceData.end("Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName());
                                    this.stackTraceData = null;
                                }

                            } else if (matcherContentStack.matches() && this.stackTraceData != null)
                            {
                                this.stackTraceData.addData(line);
                            } else if (matcherBeginStack.matches())
                            {
                                // This should be impossible
                                if (this.stackTraceData != null)
                                {
                                    this.stackTraceData.end("Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName());
                                }
                                this.stackTraceData = new StackTraceData(line);

                            } else if (matcherEndStack.matches())
                            {
                                if (this.stackTraceData != null)
                                {
                                    this.stackTraceData.addData(line);
                                    this.stackTraceData.end("Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName());
                                    this.stackTraceData = null;
                                }
                            } else if (!line.equals("Loading libraries, please wait..."))
                            {
                                // Unknow content, JVM related?!
                                RestAPI.getInstance().log(LogLevel.ERROR, "Client_" + instance.getInstance().getClientUUID() + "/" + instance.getServerName(), line);
                            }
                            //TODO: best crash detection
                        }
                    }
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            });

            executor.execute(() -> {
                while (true)
                {
                    if (!instance.isHub() && System.currentTimeMillis() - lastHeartbeat > 120000)
                    {
                        instance.stopServer();
                    }
                    try
                    {
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e)
                    {
                        break;
                    }
                }
            });
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            server.waitFor();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        normalStop();
        Hydroangeas.getInstance().getAsClient().getServerManager().onServerStop(instance);
        server.destroy();
    }

    public void normalStop()
    {
        isServerProcessAlive = false;
        instance.getInstance().getScheduler().execute(() -> {
            try
            {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        executor.shutdownNow();
    }

    public void forceStop()
    {
        isServerProcessAlive = false;
        normalStop();
        server.destroy();
        executor.shutdownNow();
    }
}
