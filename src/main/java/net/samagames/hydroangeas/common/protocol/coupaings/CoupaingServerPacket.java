package net.samagames.hydroangeas.common.protocol.coupaings;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CoupaingServerPacket extends AbstractPacket{

    private String game;

    private String map;

    private int minSlot;
    private int maxSlot;

    private JsonElement options;

    public CoupaingServerPacket()
    {

    }

    public String getGame() {
        return game;
    }

    public String getMap() {
        return map;
    }

    public int getMinSlot() {
        return minSlot;
    }

    public int getMaxSlot() {
        return maxSlot;
    }

    public JsonElement getOptions() {
        return options;
    }
}
