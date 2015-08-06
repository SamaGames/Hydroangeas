package net.samagames.hydroangeas.server.algo;

import com.google.gson.JsonParser;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class TemplateManager {

    private List<BasicGameTemplate> templates = new ArrayList<>();
    private HydroangeasServer instance;

    public TemplateManager(HydroangeasServer instance)
    {

        this.instance = instance;
        try {
            templates.add(
                    new BasicGameTemplate("quake_babylone",
                            new JsonParser().parse(new FileReader(new File(MiscUtils.getApplicationDirectory(), "templates/quake_babylone.json"))).getAsJsonObject()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        loadQueues();
    }

    public void loadQueues()
    {
        instance.getLogger().info("Ajout des queues pour chaque Template:");
        for(BasicGameTemplate template : templates)
        {
            instance.getQueueManager().addQueue(template);
            instance.getLogger().info("ID: " + template.getId() + " Jeu: " + template.getGameName() + " Map: " + template.getMapName());
        }
    }

    public BasicGameTemplate getTemplateByID(String id)
    {
        for(BasicGameTemplate template : templates)
        {
            if(template.getId().equalsIgnoreCase(id))
            {
                return template;
            }
        }
        return null;
    }

    public BasicGameTemplate getTemplateByGameAndMap(String game, String map)
    {
        for(BasicGameTemplate template : templates)
        {
            if(template.getGameName().equalsIgnoreCase(game) && template.getMapName().equalsIgnoreCase(map))
            {
                return template;
            }
        }
        return null;
    }

    public List<BasicGameTemplate> getTemplatesByGame(String game)
    {
        return templates.stream().filter(template -> template.getGameName().equalsIgnoreCase(game)).collect(Collectors.toList());
    }

    public List<BasicGameTemplate> getTemplates()
    {
        return templates;
    }

    public List<String> getListTemplate()
    {
        ArrayList<String> tmp = new ArrayList<>();
        for(BasicGameTemplate template : templates)
        {
            tmp.add(template.getId());
        }
        return tmp;
    }
}
