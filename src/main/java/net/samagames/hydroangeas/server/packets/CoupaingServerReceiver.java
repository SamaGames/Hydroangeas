package net.samagames.hydroangeas.server.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.packets.PacketReceiver;

import java.util.HashMap;

public class CoupaingServerReceiver implements PacketReceiver
{
    @Override
    public void receive(String data)
    {
        System.out.println("Before: " + data);
        System.out.println("After: " + data.replaceAll("\\\\", ""));

        JsonObject rootJson = new JsonParser().parse(data.replaceAll("\\\\", "")).getAsJsonObject();

        String game = rootJson.get("game").getAsString();
        String map = rootJson.get("map").getAsString();
        int minSlot = rootJson.get("min-slot").getAsInt();
        int maxSlot = rootJson.get("max-slot").getAsInt();

        JsonArray optionsJson = rootJson.get("options").getAsJsonArray();
        HashMap<String, String> options = new HashMap<>();

        for(int i = 0; i < optionsJson.size(); i++)
        {
            JsonObject optionJson = optionsJson.get(i).getAsJsonObject();
            options.put(optionJson.get("key").getAsString(), optionJson.get("value").getAsString());
        }

        ClientInfos selected = Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().selectMostUseableClient("coupaing");
        MinecraftServerInfos serverInfos = new MinecraftServerInfos(game, map, minSlot, maxSlot, options);

        new MinecraftServerPacket(selected, serverInfos).send();
    }
}
