package net.samagames.hydroangeas.client.docker;

import java.io.File;

/**
 * Created by Silva on 12/12/2015.
 */
public class DockerContainer {

    private String name;
    private String id;

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
        int coef = (allowedRam.endsWith("M")?1024*1024:1024*1024*1024);
        this.allowedRam = Long.valueOf(allowedRam.substring(0, allowedRam.length()-1))*coef;

        dockerAPI = new DockerAPI();
    }

    public String createContainer() {

        id = dockerAPI.createContainer(name, "frolvlad/alpine-oraclejdk8",
                flatten(this.command),
                source,
                port,
                allowedRam
                );
        dockerAPI.startContainer(id);
        return id;
    }

    /*(id = execCmd(new String[]{
                "docker",
                "run",
                "-d",
                "-P",
                "--name " + name,
                "-h docker",
                "-m " + allowedRam,
                "-p " + port + ":" + port,//Map ports
                "-v " + source.getAbsolutePath() + ":" + source.getAbsolutePath(),//Volume
                ,//Image

        }))
        */

    private String flatten(String[] strings)
    {
        String result = "";
        for(String s : strings)
        {
            result += s + " ";
        }
        return result;
    }

    public void stopContainer()
    {
        dockerAPI.stopContainer(id);
    }

    public void removeContainer()
    {
        if(isRunning())
            stopContainer();

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
}
