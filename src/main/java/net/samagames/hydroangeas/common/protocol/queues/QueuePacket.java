package net.samagames.hydroangeas.common.protocol.queues;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 24/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class QueuePacket extends AbstractPacket {

    private String game;
    private String map;

    private String templateID;

    private TypeQueue typeQueue;

    public QueuePacket()
    {
    }

    public QueuePacket(TypeQueue typeQueue, String game, String map)
    {
        this(typeQueue, game + "_" + map);
        this.game = game;
        this.map = map;
    }

    public QueuePacket(TypeQueue typeQueue, String templateID)
    {
        this.typeQueue = typeQueue;
        this.templateID = templateID;
    }

    public TypeQueue getTypeQueue() {
        return typeQueue;
    }

    public String getTemplateID() {
        return templateID;
    }

    public String getMap() {
        return map;
    }

    public String getGame() {
        return game;
    }


    public enum TypeQueue{NAMEDID, NAMED, RANDOM, FAST}
}
