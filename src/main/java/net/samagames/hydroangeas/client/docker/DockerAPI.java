package net.samagames.hydroangeas.client.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Silva on 13/12/2015.
 */
public class DockerAPI {

    private Gson gson;
    private DockerClient docker;

    public DockerAPI()
    {
        gson = new GsonBuilder().create();

        docker = DockerClientBuilder.getInstance("http://127.0.0.1:2376/").build();
    }

    public String createContainer(DockerContainer container)
    {
        CreateContainerCmd req = docker.createContainerCmd(container.getImage());
        req.withName(container.getName());
        req.withAttachStdin(false);
        req.withAttachStdout(true);
        req.withAttachStderr(true);

        req.withTty(true);
        req.withStdinOpen(false);
        req.withCmd(container.getCommand());
        req.withWorkingDir(container.getSource().getAbsolutePath());
        req.withMemoryLimit(container.getAllowedRam());
        req.withCpuset("0-7");
        req.withCpuPeriod(100000);
        req.withCpuShares(512);
        req.withOomKillDisable(false);
        Volume volume = new Volume(container.getSource().getAbsolutePath());
        req.withVolumes(volume);

        req.withBinds(new Bind(container.getSource().getAbsolutePath(), volume));

        ExposedPort tcpPort = ExposedPort.tcp(container.getPort());
        Ports portBindings = new Ports();
        portBindings.bind(tcpPort, Ports.Binding(container.getPort()));
        req.withExposedPorts(tcpPort);
        req.withPortBindings(portBindings);
        req.withNetworkMode("host");
        req.withPublishAllPorts(true);
        req.withCapAdd(Capability.ALL);

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

    public void deleteContainerWithName(String cName) {
        List<Container> exec = docker.listContainersCmd().exec();
        for (Container container : exec) {
            for (String name : container.getNames()) {
                if (name.equals("/" + cName)) {
                    stopContainer(container.getId());
                    killContainer(container.getId());
                    removeContainer(container.getId());
                }
            }
        }
    }

    public String existContainer(String id)
    {


        return null;
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
