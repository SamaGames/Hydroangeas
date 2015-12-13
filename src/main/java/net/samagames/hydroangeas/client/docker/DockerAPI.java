package net.samagames.hydroangeas.client.docker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    }

    public String createContainer(String name, String image, String command, File directory, int port, int memory)
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


        ContainerConfig.Builder req = ContainerConfig.builder();
        req.attachStdin(false);
        req.attachStdout(false);
        req.attachStderr(true);
        req.portSpecs(Arrays.asList(port + "/tcp", port+"/udp"));
        req.tty(false);
        req.openStdin(false);
        req.cmd(Arrays.asList(command));
        req.image(image);
        req.workingDir(directory.getAbsolutePath());
        req.networkDisabled(false);
        req.memory((long) memory);
        req.cpuset("0-7");
        req.cpuShares(512L);

        HostConfig.Builder hostconf =  HostConfig.builder();
        hostconf.binds(Arrays.asList(directory.getAbsolutePath() + ":" + directory.getAbsolutePath()));
        hostconf.lxcConf(Arrays.asList(new HostConfig.LxcConfParameter("lxc.utsname", "docker")));
        HashMap<String, List<PortBinding>> map = new HashMap<>();
        map.put(port + "/tcp", Arrays.asList(PortBinding.of("0.0.0.0", port)));
        map.put(port + "/udp", Arrays.asList(PortBinding.of("0.0.0.0", port)));
        hostconf.portBindings(map);
        hostconf.cpusetCpus("0-7");

        hostconf.publishAllPorts(false);
        hostconf.dns(Arrays.asList("8.8.8.8"));

        req.hostConfig(hostconf.build());
        try {
            ContainerCreation container = docker.createContainer(req.build(), name);
            if(container.id() == null)
            {
                container.getWarnings().forEach(System.err::print);
            }
            return container.id();
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean isRunning(String id)
    {
        try {
            return docker.inspectContainer(id).state().running();
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void stopContainer(String id)
    {
        try {
            docker.stopContainer(id, 10);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void killContainer(String id)
    {
        try {
            docker.killContainer(id);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeContainer(String id)
    {
        try {
            docker.removeContainer(id);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
