package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.HashMap;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CoupaingServerPacket extends AbstractPacket{

    public String game;
    public String map;

    public int minSlot;
    public int maxSlot;

    public HashMap<String, String> options;

    public CoupaingServerPacket()
    {

    }
}
