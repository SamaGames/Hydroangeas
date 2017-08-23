package net.samagames.hydroangeas.client.docker;

import net.samagames.hydroangeas.Hydroangeas;

import java.io.File;

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
