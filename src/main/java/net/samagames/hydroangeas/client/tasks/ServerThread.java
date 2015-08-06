package net.samagames.hydroangeas.client.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 05/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerThread extends Thread {

    public boolean isServerProcessAlive;
    public Process server;
    public Thread errorThread;
    public File directory;
    private MinecraftServerC instance;

    public ServerThread(MinecraftServerC instance, String[] command, String[] env, File directory)
    {
        this.instance = instance;
        try {
            this.directory = directory;

            Thread.sleep(5);

            server = Runtime.getRuntime().exec(command, env, directory);
            isServerProcessAlive = true;

            errorThread = new Thread() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(server.getErrorStream()));
                        String line = null;
                        try {
                            while(isServerProcessAlive && (line = reader.readLine()) != null) {
                                //TODO handle errors
                            }
                        } finally {
                            reader.close();
                        }
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            };
            errorThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try {
            server.waitFor();

        } catch (InterruptedException e) {
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
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        errorThread.interrupt();
    }

    public void forceStop()
    {
        normalStop();
        server.destroy();
        errorThread.interrupt();
    }
}
