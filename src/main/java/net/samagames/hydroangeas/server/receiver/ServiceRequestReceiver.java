package net.samagames.hydroangeas.server.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.common.protocol.network.TemplateRequest;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;

import java.util.List;
import java.util.UUID;

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

    public HydroangeasServer instance;
    private Gson gson;

    public ServiceRequestReceiver(HydroangeasServer instance)
    {
        this.instance = instance;
        this.gson = new Gson();
    }

    @Override
    public void receive(String packet)
    {
        TemplateRequest data = gson.fromJson(packet, TemplateRequest.class);

        String target = data.getTarget();

        if (!"hydroserver".equals(target))
        {
            return;
        }

        JsonObject response = new JsonObject();
        response.addProperty("reqId", data.getReqId().toString());

        if (data.getName().equals("order"))
        {
            try {
                JsonObject parse = new JsonParser().parse(data.getOperation()).getAsJsonObject();
                UUID asker = UUID.fromString(parse.get("uuid").getAsString());
                SimpleGameTemplate simpleGameTemplate = new SimpleGameTemplate(parse.get("id").getAsString(), parse);

                MinecraftServerS minecraftServerS = instance.getHostGameManager().orderServer(asker, simpleGameTemplate);
                if (minecraftServerS != null)
                {
                    response.addProperty("code", 200);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", minecraftServerS.getUUID().toString());
                    jsonObject.addProperty("name", minecraftServerS.getServerName());
                    jsonObject.addProperty("status", minecraftServerS.getStatus().toString());
                    response.add("data", jsonObject);
                }else {
                    response.addProperty("code", 500);
                    response.addProperty("data", "Cannot order the server.");
                }
            }catch (Exception ignored)
            {
                ignored.printStackTrace();
                response.addProperty("code", 500);
                response.addProperty("data", "Malformation template");
            }

        }else if (data.getName().equals("status"))
        {
            String serverName = data.getOperation();
            MinecraftServerS serverByName = instance.getClientManager().getServerByName(serverName);
            if (serverByName != null)
            {
                response.addProperty("code", 200);
                JsonObject result = new JsonObject();
                result.addProperty("status", serverByName.getStatus().toString());
                result.addProperty("startedTime", System.currentTimeMillis() - serverByName.getStartedTime());
                response.add("data", result);
            }else {
                response.addProperty("code", 404);
                response.addProperty("data", "Server not found, maybe crashed.");
            }
        }else if(data.getName().equals("list"))
        {
            response.addProperty("code", 200);
            List<HydroClient> clients = instance.getClientManager().getClients();
            JsonArray result = new JsonArray();

            for(HydroClient client : clients)
            {
                for (MinecraftServerS s : client.getServerManager().getServers())
                {
                    if(s.isCoupaingServer())
                    {
                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("name", s.getServerName());
                        jsonObject.addProperty("uuid", s.getUUID().toString());
                        jsonObject.addProperty("owner", s.getOwner().toString());
                        jsonObject.addProperty("template", s.getTemplateID());
                        jsonObject.addProperty("startedTime", System.currentTimeMillis() - s.getStartedTime());
                        jsonObject.addProperty("status", s.getStatus().toString());

                        result.add(jsonObject);
                    }
                }
            }
            response.add("data", result);
        }

        instance.getConnectionManager().sendPacket("samaconnect.services.responses", response.toString());
    }
}
