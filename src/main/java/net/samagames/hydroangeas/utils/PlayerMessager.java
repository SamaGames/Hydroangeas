package net.samagames.hydroangeas.utils;

import net.samagames.hydroangeas.Hydroangeas;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class PlayerMessager
{

    public static void sendMessage(UUID player, String message)
    {
        Hydroangeas.getInstance().getRedisSubscriber().send("apiexec.send", player + " {\"text\":\"" + message + "\"}");
    }
}
