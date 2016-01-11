package net.samagames.hydroangeas.client.docker;

import com.google.gson.*;
import net.samagames.hydroangeas.Hydroangeas;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Silva on 13/12/2015.
 */
public class DockerAPI {

    private static final Gson GSON = new GsonBuilder().create();
    //private DockerClient docker;
    private String url = "http://127.0.0.1:2376";

    public DockerAPI()
    {
        //docker = DefaultDockerClient.builder().uri("http://127.0.0.1:2376/").build();

    }

    public String createContainer(DockerContainer container)
    {
        JsonObject r = new JsonObject();

        r.addProperty("Hostname", "");
        r.addProperty("Domainname", "");
        r.addProperty("User", "");
        r.addProperty("AttachStdin", false);
        r.addProperty("AttachStdout", true);
        r.addProperty("AttachStderr", true);
        r.addProperty("Tty", true);
        r.addProperty("OpenStdin", false);
        r.addProperty("StdinOnce", false);
        r.add("Env", new JsonArray());

        JsonArray cmd = new JsonArray();
        for(String s : container.getCommand())
        {
            cmd.add(s);
        }

        r.add("Cmd", cmd);
        r.add("Entrypoint", null);
        r.addProperty("Image", container.getImage());
        r.add("Labels", new JsonObject());

        JsonArray mounts = new JsonArray();
        JsonObject volume1 = new JsonObject();
        volume1.addProperty("Source", container.getSource().getAbsolutePath());
        volume1.addProperty("Destination", container.getSource().getAbsolutePath());
        volume1.addProperty("Mode", "rw");
        volume1.addProperty("RW", true);
        mounts.add(volume1);

        r.add("Mounts", mounts);

        JsonObject volumes = new JsonObject();
        volumes.add(container.getSource().getAbsolutePath(), new JsonObject());

        r.add("Volumes", volumes);
        r.addProperty("WorkingDir", container.getSource().getAbsolutePath());
        r.addProperty("NetworkDisabled", false);
        //r.addProperty("MacAddress", randomMACAddress()); //Don't know how to use

        JsonObject exposedPorts = new JsonObject();
        exposedPorts.add(container.getPort()+ "/tcp", new JsonObject());
        exposedPorts.add(container.getPort()+ "/udp", new JsonObject());

        r.add("ExposedPorts", exposedPorts);
        r.addProperty("StopSignal", "SIGTERM");

        JsonObject hostconfig = new JsonObject();

        JsonArray binds = new JsonArray();
        binds.add(container.getSource().getAbsolutePath() + ":" + container.getSource().getAbsolutePath() + ":rw");

        hostconfig.add("Binds", binds);
        hostconfig.add("Links", new JsonArray());
        hostconfig.add("LxcConf", new JsonObject());
        hostconfig.addProperty("Memory", container.getAllowedRam() * 2);
        hostconfig.addProperty("MemorySwap", (container.getAllowedRam() * 2 ) + container.getAllowedRam() << 2);
        hostconfig.addProperty("MemoryReservation", container.getAllowedRam() * 2);
        hostconfig.addProperty("KernelMemory", 0);
        hostconfig.addProperty("CpuShares", 512);
        hostconfig.addProperty("CpuPeriod", 100000);
        hostconfig.addProperty("CpuQuota" , 180000);
        hostconfig.addProperty("CpusetCpus", "0-7");
        //hostconfig.addProperty("CpusetMems", "0");
        //hostconfig.addProperty("BlkioWeight", 1000);
        hostconfig.addProperty("MemorySwappiness", 80);
        hostconfig.addProperty("OomKillDisable", true);

        JsonObject portBindings = new JsonObject();

        JsonArray hostPorts = new JsonArray();
        JsonObject hostPort = new JsonObject();
        hostPort.addProperty("HostIp", "0.0.0.0");
        hostPort.addProperty("HostPort", ""+container.getPort());
        hostPorts.add(hostPort);

        portBindings.add(container.getPort()+"/tcp", hostPorts);
        portBindings.add(container.getPort()+"/udp", hostPorts);

        hostconfig.add("PortBindings", portBindings);
        hostconfig.addProperty("PublishAllPorts", true);
        hostconfig.addProperty("Privileged", false);
        hostconfig.addProperty("ReadonlyRootfs", false);

        /*JsonArray dns = new JsonArray();
        dns.add("8.8.8.8");

        hostconfig.add("Dns", dns);
        hostconfig.add("DnsOptions", new JsonArray());
        hostconfig.add("DnsSearch", new JsonArray());*/
        hostconfig.add("ExtraHosts", null);
        hostconfig.add("VolumesFrom", new JsonArray());

        JsonArray caps = new JsonArray();
        caps.add("ALL");

        hostconfig.add("CapAdd", caps);
        hostconfig.add("CapDrop", new JsonArray());

        JsonObject restartPolicy = new JsonObject();
        restartPolicy.addProperty("Name", "");
        restartPolicy.addProperty("MaximumRetryCount", 0);

        hostconfig.add("RestartPolicy", restartPolicy);
        hostconfig.addProperty("NetworkMode", "host");
        hostconfig.add("Devices", new JsonArray());
        hostconfig.add("Ulimits", new JsonArray());

        JsonObject logConfig = new JsonObject();
        logConfig.addProperty("Type", "json-file");
        logConfig.add("Config", new JsonObject());

        hostconfig.add("LogConfig", logConfig);

        hostconfig.add("SecurityOpt", new JsonArray());
        hostconfig.addProperty("CgroupParent", "");
        hostconfig.addProperty("VolumeDriver", "");

        r.add("HostConfig", hostconfig);

        Response response = sendRequest("/containers/create?name=" + container.getName(), r, "POST");

        if(response.getStatus() == 201)
        {
            JsonObject data = response.getResponse().getAsJsonObject();
            String id = data.get("Id").getAsString();
            JsonElement warnings1 = data.get("Warnings");
            if(warnings1 != null && warnings1.isJsonArray())
            {
                JsonArray warnings = warnings1.getAsJsonArray();
                if(warnings != null && warnings.size() > 0)
                {
                    for(JsonElement s : warnings)
                    {
                        System.err.print(s);
                    }
                }
            }

            return id;
        }

        return null;
    }

    public void deleteContainerWithName(String cName) {

        Response response = sendRequest("/containers/json?all=1", new JsonObject(), "GET");

        if(response.getStatus() == 200)
        {
            for(JsonElement object : response.getResponse().getAsJsonArray())
            {
                JsonObject container = object.getAsJsonObject();
                String id = container.get("Id").getAsString();
                for(JsonElement obj : container.get("Names").getAsJsonArray())
                {
                    if (obj.getAsString().contains(cName)) {
                        try {
                            killContainer(id);
                        }catch (Exception e)
                        {
                        }
                        removeContainer(id);
                    }
                }
            }
        }
    }

    public JsonArray listRunningContainers()
    {
        Response response = sendRequest("/containers/json?all=1&filter=[status=running]", new JsonObject(), "GET");

        if(response.getStatus() == 200 && response.getResponse().isJsonArray())
        {
            return response.getResponse().getAsJsonArray();
        }
        return null;
    }

    public boolean startContainer(String id)
    {
        Response response = sendRequest("/containers/" + id + "/start", new JsonObject(), "POST");
        return response.getStatus() == 204;
    }

    public JsonObject inspectContainer(String id)
    {
        Response response = sendRequest("/containers/" + id + "/json?size=0", new JsonObject(), "GET");

        if(response.getStatus() == 200)
        {
            return response.getResponse().getAsJsonObject();
        }
        return null;
    }

    public boolean isRunning(String id)
    {
        JsonObject inspect = inspectContainer(id);
        if(inspect != null)
        {
            String status = inspect.get("State").getAsJsonObject().get("Status").getAsString();
            //Hydroangeas.getInstance().getLogger().info("Status: " + status);
            return status.equals("running");
        }
        return false;
    }

    public void stopContainer(String id)
    {
        sendRequest("/containers/" + id + "/stop?t=0", new JsonObject(), "POST");
    }

    public void killContainer(String id)
    {
        sendRequest("/containers/" + id + "/kill", new JsonObject(), "POST");
    }

    public void removeContainer(String id)
    {
        sendRequest("/containers/" + id + "?v=1&force=1", new JsonObject(), "DELETE");
    }

    private String randomMACAddress(){
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for(byte b : macAddr){

            if(sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public Response sendRequest(String point, JsonElement element, String method) {
        String requestData = GSON.toJson(element);

        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpEntityEnclosingRequestBase request = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return method;
                }
            };
            request.setURI(new URI(this.url + point));
            request.addHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(requestData);
            request.setEntity(params);

            HttpResponse responses = httpClient.execute(request);

            int statusCode = responses.getStatusLine().getStatusCode();
            JsonElement json = null;
            if(responses.getEntity() != null)
            {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(responses.getEntity().getContent()));

                try{
                    json = new JsonParser().parse(in);
                }catch (Exception e)
                {
                    json = null;
                }
            }
            return new Response(statusCode, json);
        } catch (Exception e) {
            Hydroangeas.getInstance().getLogger().severe("Error for " + point + " (message:" + e.getMessage() + ")");
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return new Response(500, null);
    }

    public class Response {

        private int status;
        private JsonElement response;

        public Response(int status, JsonElement response)
        {
            this.status = status;
            this.response = response;
        }

        public int getStatus() {
            return status;
        }

        public JsonElement getResponse() {
            return response;
        }
    }

}
