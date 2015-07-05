package net.samagames.hydroangeas.client.schedulers;

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

    public boolean isServerProcessAlive = false;

    public Process server;
    public Thread errorThread;

    public File directory;

    public ServerThread(String[] command, String[] env, File directory)
    {
        try {
            this.directory = directory;

            server = Runtime.getRuntime().exec(command, env, directory);
            isServerProcessAlive = true;

            errorThread = new Thread() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(server.getErrorStream()));
                        String line = "";
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
    public void run() {
        try {
            server.waitFor();
            isServerProcessAlive = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        directory.delete();
        errorThread.interrupt();
        server.destroy();
    }

    public void forceStop()
    {
        errorThread.interrupt();
        server.destroyForcibly();
    }
}
