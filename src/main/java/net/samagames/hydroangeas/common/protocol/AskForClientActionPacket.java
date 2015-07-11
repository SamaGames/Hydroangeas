package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 11/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class AskForClientActionPacket extends AbstractPacket{

    private UUID uuid;
    private ActionCommand command;

    //Datas
    private String data;

    public AskForClientActionPacket(){}

    public AskForClientActionPacket(UUID uuid, ActionCommand command, String data)
    {
        this.uuid = uuid;
        this.command = command;
        this.data = data;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getData()
    {
        return data;
    }

    public ActionCommand getCommand() {
        return command;
    }

    public enum ActionCommand{
        SERVEREND, CLIENTSHUTDOWN, CONSOLECOMMAND
    }
}
