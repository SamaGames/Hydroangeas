package net.samagames.hydroangeas.common.packets;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CommandPacket extends AbstractPacket
{
    private String sourceUUID;
    private String action;

    public CommandPacket()
    {

    }

    public CommandPacket(String source, String action)
    {
        this.sourceUUID = source;
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }


    // TODO: Send logs of the command to the client
    public String getSourceUUID()
    {
        return sourceUUID;
    }
}