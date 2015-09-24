package net.samagames.hydroangeas.server.algo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;
import net.samagames.hydroangeas.utils.MiscUtils;
import org.apache.commons.io.FilenameUtils;

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

    private List<AbstractGameTemplate> templates = new ArrayList<>();
    private HydroangeasServer instance;

    public TemplateManager(HydroangeasServer instance)
    {

        this.instance = instance;

        File directory = new File(MiscUtils.getApplicationDirectory(), "templates");

        try {
            for(File file : directory.listFiles()) {
                if(file.isFile()) {
                    JsonObject data = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
                    if(data.getAsJsonPrimitive("Type").getAsString().equals("Package"))
                    {
                        templates.add(new PackageGameTemplate(FilenameUtils.removeExtension(file.getName()), data));
                    }else{
                        templates.add(new SimpleGameTemplate(FilenameUtils.removeExtension(file.getName()), data));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        loadQueues();
    }

    public void loadQueues()
    {
        instance.getLogger().info("Ajout des queues pour chaque Template:");
        for(AbstractGameTemplate template : templates)
        {
            instance.getQueueManager().addQueue(template);
            instance.getLogger().info("ID: " + template.getId() + " Jeu: " + template.getGameName() + " Map: " + template.getMapName());
        }
    }

    public AbstractGameTemplate getTemplateByID(String id)
    {
        for(AbstractGameTemplate template : templates)
        {
            if(template.getId().equalsIgnoreCase(id))
            {
                return template;
            }
        }
        return null;
    }

    public AbstractGameTemplate getTemplateByGameAndMap(String game, String map)
    {
        for(AbstractGameTemplate template : templates)
        {
            if(template.getGameName().equalsIgnoreCase(game) && template.getMapName().equalsIgnoreCase(map))
            {
                return template;
            }
        }
        return null;
    }

    public List<AbstractGameTemplate> getTemplatesByGame(String game)
    {
        return templates.stream().filter(template -> template.getGameName().equalsIgnoreCase(game)).collect(Collectors.toList());
    }

    public List<AbstractGameTemplate> getTemplates()
    {
        return templates;
    }

    public List<String> getListTemplate()
    {
        ArrayList<String> tmp = new ArrayList<>();
        for(AbstractGameTemplate template : templates)
        {
            tmp.add(template.getId());
        }
        return tmp;
    }
}
