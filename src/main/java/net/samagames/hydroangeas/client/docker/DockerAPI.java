package net.samagames.hydroangeas.client.docker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Silva on 13/12/2015.
 */
public class DockerAPI {

    private Gson gson;
    private DockerClient docker;

    public DockerAPI()
    {
        gson = new GsonBuilder().create();

        docker = DefaultDockerClient.builder().uri("http://127.0.0.1:2376/").build();
    }

    public String createContainer(DockerContainer container)
    {

        final String[] ports = {""+container.getPort()};
        final Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<PortBinding>();
            hostPorts.add(PortBinding.of("0.0.0.0", port));
            portBindings.put(port, hostPorts);
        }


        HostConfig hostConfig = HostConfig.builder()
                .portBindings(portBindings)
                .binds(container.getSource().getAbsolutePath())
                .cpuQuota(5000L)
                .publishAllPorts(true)
                .networkMode("host")
                .build();

        ContainerConfig config = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(container.getImage())
                .attachStdin(false)
                .attachStderr(true)
                .attachStdout(true)
                .tty(true)
                .openStdin(false)
                .stdinOnce(false)
                .cmd(container.getCommand())
                .workingDir(container.getSource().getAbsolutePath())
                .cpuset("0-7")
                .memory(container.getAllowedRam())
                .cpuQuota(5000L)
                .cpuShares(512L)
                .exposedPorts(container.getPort() + "/tcp: {}", container.getPort() + "/udp: {}")
                .build();

       // docker.createContainer()
       /* CreateContainerCmd req = docker.createContainerCmd(container.getImage());
        req.withName(container.getName());
        req.withCpuPeriod(100000);
        req.withOomKillDisable(false);
        Volume volume = new Volume();
        req.withVolumes(volume);

        req.withBinds(new Bind(container.getSource().getAbsolutePath(), volume));

        ExposedPort tcpPort = ExposedPort.tcp(container.getPort());
        Ports portBindings = new Ports();
        portBindings.bind(tcpPort, Ports.Binding(container.getPort()));
        req.withExposedPorts(tcpPort);
        req.withPortBindings(portBindings);
        req.withCapAdd(Capability.ALL);*/

        try {
            ContainerCreation container1 = docker.createContainer(config, container.getName());
            if(container1.getWarnings() != null)
            {
                for(String s : container1.getWarnings())
                {
                    System.err.print(s);
                }
                return null;
            }

            return container1.id();
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteContainerWithName(String cName) {
        try {
            List<Container> exec = docker.listContainers(DockerClient.ListContainersParam.allContainers(true));
            for (Container container : exec) {
                for (String name : container.names()) {
                    if (name.contains(cName)) {
                        try {
                            killContainer(container.id());
                        }catch (Exception e)
                        {
                        }
                        removeContainer(container.id());
                    }
                }
            }
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startContainer(String id)
    {
        try {
            docker.startContainer(id);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void waitUntilStop(String id)
    {
        try {
            docker.waitContainer(id);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopContainer(String id)
    {
        try {
            docker.stopContainer(id, 0);
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
            //e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    public void removeContainer(String id)
    {
        try {
            docker.removeContainer(id, true);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
