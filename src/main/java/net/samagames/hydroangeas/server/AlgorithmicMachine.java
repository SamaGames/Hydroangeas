package net.samagames.hydroangeas.server;

import com.google.gson.JsonArray;
import net.samagames.hydroangeas.common.protocol.MinecraftServerUpdatePacket;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AlgorithmicMachine
{
    private final HydroangeasServer instance;
    private JsonArray lastData;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void startMachinery()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");
        instance.getScheduler().schedule(() -> instance.getClientManager().getClients().get(0).getServerManager().addServer("quake", "babylon", 2, 10, new HashMap<>()), 20, TimeUnit.SECONDS);
    }

    public void onServerUpdate(MinecraftServerUpdatePacket serverStatus)
    {
        if(serverStatus.getAction().equals(MinecraftServerUpdatePacket.UType.END))
        {
            HydroClient client = instance.getClientManager().getClientByUUID(serverStatus.getUUID());
            MinecraftServerS oldserver = client.getServerManager().getServerByName(serverStatus.getServerName());
            client.getServerManager().addServer(oldserver.getGame(), oldserver.getMap(), oldserver.getMinSlot(), oldserver.getMaxSlot(), oldserver.getOptions());

            //We restart the same server on the same client for test
        }

    }
}
