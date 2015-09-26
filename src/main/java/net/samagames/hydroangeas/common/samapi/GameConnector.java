package net.samagames.hydroangeas.common.samapi;

import net.samagames.hydroangeas.Hydroangeas;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 20/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class GameConnector
{

    public static void sendPlayerToServer(String serverName, UUID playerId)
    {
        Hydroangeas.getInstance().getRedisSubscriber().send("join." + serverName, playerId.toString());
    }
}
