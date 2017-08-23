package net.samagames.hydroangeas.client.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.remote.RemoteControl;
import net.samagames.hydroangeas.client.remote.RemoteService;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.common.protocol.network.ServiceRequest;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import java.io.IOException;

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
                if (!service.getmBeanInfo().getClassName().startsWith("net.samagames"))
                    continue;

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", service.getName());

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
            RemoteControl remoteControl = server.getRemoteControl();
            if (remoteControl != null && remoteControl.isConnected())
            {
                RemoteService service = remoteControl.getService(data.getName());

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
            }else
            {
                response.addProperty("code", 503);
                response.add("data", null);
            }
        }

        instance.getConnectionManager().sendPacket("samaconnect.services.responses", response.toString());
    }
}
