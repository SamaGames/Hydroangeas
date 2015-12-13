package net.samagames.hydroangeas.client.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.docker.DockerContainer;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.log.StackTraceData;
import net.samagames.hydroangeas.utils.ping.MinecraftPing;
import net.samagames.hydroangeas.utils.ping.MinecraftPingOptions;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    private static final Pattern LOG_PATTERN = Pattern.compile("\\[\\d{1,2}:\\d{2}:\\d{2} (INFO|WARN|ERROR)\\]: (.*)");
    private static final Pattern BEGIN_OF_STACKTRACE_PATTERN = Pattern.compile("([a-zA-Z\\.]*Exception)");
    private static final Pattern CONTENT_OF_STACKTRACE_PATTERN = Pattern.compile("((\\tat|Caused by).*)");
    private static final Pattern END_OF_STACKTRACE_PATTERN = Pattern.compile("((\\t\\.\\.\\.).*)");
    public boolean isServerProcessAlive;
    public File directory;
    private long lastHeartbeat = System.currentTimeMillis();
    private ScheduledExecutorService executor;
    private MinecraftServerC instance;
    private StackTraceData stackTraceData;

    private DockerContainer container;

    public ServerThread(MinecraftServerC instance, String[] command, String ram, File directory)
    {
        this.instance = instance;

        this.executor = Executors.newScheduledThreadPool(5);
        try
        {
            this.directory = directory;

            Thread.sleep(10);

            isServerProcessAlive = true;

            container = new DockerContainer(
                    instance.getServerName(),
                    directory,
                    instance.getPort(),
                    command,
                    ram
            );
            executor.scheduleAtFixedRate(() -> {
                /*if (!instance.isHub() && System.currentTimeMillis() - lastHeartbeat > 120000) {
                    instance.stopServer();
                }*/

                try {
                    String ip = HydroangeasClient.getInstance().getAsClient().getIP();
                    new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(instance.getPort()).setTimeout(100));
                } catch (IOException e) {
                    Hydroangeas.getInstance().getLogger().info("Can't ping server: " + instance.getServerName() + " shutting down");
                    instance.stopServer();
                }

            }, 60, 15, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        Hydroangeas.getInstance().getLogger().info(container.createContainer());

        while (container.isRunning())
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        normalStop();
        container.removeContainer();
    }

    public void normalStop()
    {
        Hydroangeas.getInstance().getAsClient().getServerManager().onServerStop(instance);
        isServerProcessAlive = false;
        Hydroangeas.getInstance().getAsClient().getLogManager().saveLog(instance.getServerName(), instance.getTemplateID());
        instance.getInstance().getScheduler().execute(() -> {
            try
            {
                FileDeleteStrategy.FORCE.delete(directory);
                FileUtils.deleteDirectory(directory);
            } catch (IOException e)
            {
                //Don't care if we cannot delete files
                //e.printStackTrace();
            }
        });
        executor.shutdownNow();
    }

    public void forceStop()
    {
        isServerProcessAlive = false;
        normalStop();
        container.removeContainer();
    }

    public DockerContainer getContainer()
    {
        return container;
    }
}
