package net.samagames.hydroangeas.client.schedulers;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;

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
public class ServerThread implements Runnable {

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
        }
    }

    @Override
    public void run()
    {
        try {
            server.waitFor();
            isServerProcessAlive = false;
            Hydroangeas.getInstance().getAsClient().getServerManager().onServerStop(instance);
        } catch (InterruptedException e) {
            e.printStackTrace();
            isServerProcessAlive = false;
        }
    }

    public void stop()
    {
        isServerProcessAlive = false;
        directory.delete();
        errorThread.interrupt();
        server.destroy();
    }

    public void forceStop()
    {
        isServerProcessAlive = false;
        directory.delete();
        errorThread.interrupt();
        server.destroyForcibly();
    }
}
