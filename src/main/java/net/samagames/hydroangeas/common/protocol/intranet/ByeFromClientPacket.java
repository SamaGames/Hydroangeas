package net.samagames.hydroangeas.common.protocol.intranet;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 05/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ByeFromClientPacket extends AbstractPacket {

    private UUID uuid;

    public ByeFromClientPacket(UUID uuid)
    {
        this.uuid = uuid;
    }

    public ByeFromClientPacket() {

    }

    public UUID getUUID()
    {
        return uuid;
    }
}
