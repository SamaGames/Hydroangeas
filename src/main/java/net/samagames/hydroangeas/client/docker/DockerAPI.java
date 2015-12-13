package net.samagames.hydroangeas.client.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

/**
 * Created by Silva on 13/12/2015.
 */
public class DockerAPI {

    private Gson gson;
    private String url;
    private DockerClient docker;

    public DockerAPI()
    {
        gson = new GsonBuilder().create();

        docker = DockerClientBuilder.getInstance("http://127.0.0.1:2376/").build();
    }

    public String createContainer(String name, String image, String[] command, File directory, int port, long memory)
    {
        /*JsonObject request = new JsonObject();

        JsonObject exposedPorts = new JsonObject();

        JsonObject hostConfig = new JsonObject();
        hostConfig.addProperty("CpuShares", 512);
        hostConfig.addProperty("CpuPeriod", 100000);
        hostConfig.addProperty("CpuQuota", 200000);
        hostConfig.addProperty("CpusetCpus", "0-7");
        hostConfig.addProperty("CpusetMems", "0-7");
        hostConfig.addProperty("BlkioWeight", 1000);
        hostConfig.addProperty("MemorySwappiness", 60);
        hostConfig.addProperty("OomKillDisable", false);

        hostConfig.addProperty("PublishAllPorts", false);
        hostConfig.addProperty("Privileged", false);
        hostConfig.addProperty("ReadonlyRootfs", false);
        hostConfig.add("Dns", getOneElement("8.8.8.8"));
        hostConfig.add("DnsOptions", new JsonArray());
        hostConfig.add("DnsSearch", new JsonArray());
        hostConfig.add("ExtraHosts", null);
        hostConfig.add("VolumesFrom", new JsonArray());
        hostConfig.add("CapAdd", getOneElement("NET_ADMIN"));
        hostConfig.add("CapDrop", getOneElement("MKNOD"));
        hostConfig.add("RestartPolicy", new JsonObject());
        hostConfig.addProperty("NetworkMode", "bridge");
        hostConfig.add("Devices", new JsonArray());
        hostConfig.add("Ulimits", new JsonArray());
        request.add("HostConfig", exposedPorts);*/

        CreateContainerCmd req = docker.createContainerCmd(image);
        req.withAttachStdin(false);
        req.withAttachStdout(true);
        req.withAttachStderr(true);

        req.withTty(true);
        req.withStdinOpen(false);
        req.withCmd(command);
        req.withWorkingDir(directory.getAbsolutePath());
        req.withMemoryLimit(memory);
        req.withCpuset("0-7");
        req.withCpuPeriod(100000);
        req.withCpuShares(512);
        req.withOomKillDisable(false);
        Volume volume = new Volume(directory.getAbsolutePath());
        req.withVolumes(volume);

        req.withBinds(new Bind(directory.getAbsolutePath(), volume));

        ExposedPort tcpPort = ExposedPort.tcp(port);
        Ports portBindings = new Ports();
        portBindings.bind(tcpPort, Ports.Binding(port));
        req.withExposedPorts(tcpPort);
        req.withPortBindings(portBindings);
        req.withNetworkMode("host");
        req.withPublishAllPorts(true);
        req.withCapAdd(new Capability[]{Capability.ALL});
        req.withName(name);

        CreateContainerResponse containerResponse = req.exec();
        if(containerResponse.getId() == null)
        {
            for(String s : containerResponse.getWarnings())
            {
                System.err.print(s);
            }
        }
        return containerResponse.getId();
    }

    public void startContainer(String id)
    {
        docker.startContainerCmd(id).exec();
    }

    public boolean isRunning(String id)
    {
        return docker.inspectContainerCmd(id).exec().getState().isRunning();
    }

    public void stopContainer(String id)
    {
        docker.stopContainerCmd(id).exec();
    }

    public void killContainer(String id)
    {
        docker.killContainerCmd(id).exec();
    }

    public void removeContainer(String id)
    {
        docker.removeContainerCmd(id).exec();
    }

}
