package net.samagames.hydroangeas.server.algo;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;

import java.util.UUID;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 25/12/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class HostGameManager {

    private HydroangeasServer instance;

   // private HashMap<UUID, MinecraftServerS> servers = new HashMap<UUID, MinecraftServerS>();

    public HostGameManager(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public MinecraftServerS orderServer(UUID asker, SimpleGameTemplate template)
    {
        MinecraftServerS minecraftServerS = instance.getAlgorithmicMachine().orderTemplate(template);
        //servers.put(minecraftServerS.getUUID(), minecraftServerS);
        minecraftServerS.setOwner(asker);
        minecraftServerS.setCoupaingServer(true);

        return minecraftServerS;
    }

    /*public MinecraftServerS removeServer(UUID uuid)
    {


    }*/




}
