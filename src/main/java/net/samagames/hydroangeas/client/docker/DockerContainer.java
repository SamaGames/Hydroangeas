package net.samagames.hydroangeas.client.docker;

import net.samagames.hydroangeas.Hydroangeas;

import java.io.File;

/**
 * Created by Silva on 12/12/2015.
 */
public class DockerContainer {

    private String name;
    private String id;
    private String image;

    private String[] command;

    private int port;
    private long allowedRam;

    private File source;

    private DockerAPI dockerAPI;

    public DockerContainer(String name, File source, int port, String[] command, String allowedRam)
    {

        this.name = name;
        this.source = source;
        this.port = port;
        this.command = command;
        this.image = "frolvlad/alpine-oraclejdk8";
        int coef = allowedRam.endsWith("M") ? 1024*1024 : 1024*1024*1024;
        this.allowedRam = Long.valueOf(allowedRam.substring(0, allowedRam.length()-1))*coef;

        dockerAPI = Hydroangeas.getInstance().getAsClient().getDockerAPI();
    }

    public String createContainer() {

        dockerAPI.deleteContainerWithName(name);

        this.id = dockerAPI.createContainer(this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dockerAPI.startContainer(id);
        return this.id;
    }

    public void stopContainer()
    {
        dockerAPI.stopContainer(id);
    }

    public void killContainer()
    {
        dockerAPI.killContainer(id);
    }

    public void removeContainer()
    {
        try{
            killContainer();
        }catch (Exception e)
        {
        }

        dockerAPI.removeContainer(id);
    }

    public boolean isRunning()
    {
        return dockerAPI.isRunning(id);
    }

    public String getMapPort()
    {
        return "";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public File getSource() {
        return source;
    }

    public int getPort() {
        return port;
    }

    public String[] getCommand() {
        return command;
    }


    public long getAllowedRam() {
        return allowedRam;
    }

    public String getImage() {
        return image;
    }
}
