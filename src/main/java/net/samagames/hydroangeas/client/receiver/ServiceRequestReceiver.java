package net.samagames.hydroangeas.client.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.remote.RemoteService;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.common.protocol.network.ServiceRequest;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import java.io.IOException;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 22/08/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class ServiceRequestReceiver implements PacketReceiver
{

    public HydroangeasClient instance;

    public ServiceRequestReceiver(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    @Override
    public void receive(String packet)
    {
        ServiceRequest data = new Gson().fromJson(packet, ServiceRequest.class);

        String target = data.getTarget();

        MinecraftServerC server = instance.getServerManager().getServerByName(target);

        if (server == null)
        {
            return;
        }

        JsonObject response = new JsonObject();
        response.addProperty("reqId", data.getReqId().toString());

        if (data.getName().equals("fetch"))
        {
            response.addProperty("code", 200);

            JsonArray list = new JsonArray();

            for (RemoteService service : server.getRemoteControl().getServices())
            {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", service.getmBeanInfo().getClassName());

                JsonArray operations = new JsonArray();
                for (MBeanOperationInfo operation : service.getmBeanInfo().getOperations())
                {
                    operations.add(new Gson().toJsonTree(operation));
                }
                jsonObject.add("operations", operations);

                list.add(jsonObject);
            }

            response.add("data", list);

        }else
        {
            RemoteService service = server.getRemoteControl().getService(data.getName());

            if (service != null)
            {
                try {
                    Object result = server.getRemoteControl().invokeService(service, data.getOperation(), data.getArguments(), data.getSignature());
                    response.addProperty("code", 200);
                    response.add("data", new Gson().toJsonTree(result));
                } catch (ReflectionException | IOException | MBeanException | InstanceNotFoundException e) {
                    e.printStackTrace();
                    response.addProperty("code", 500);
                    response.add("data", null);
                }
            }else
            {
                response.addProperty("code", 404);
                response.add("data", null);
            }
        }

        instance.getConnectionManager().sendPacket("samaconnect.services.responses", response.toString());
    }
}
