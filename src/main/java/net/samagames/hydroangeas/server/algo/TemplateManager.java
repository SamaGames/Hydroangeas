package net.samagames.hydroangeas.server.algo;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;
import net.samagames.hydroangeas.server.waitingqueue.Queue;
import net.samagames.hydroangeas.utils.MiscUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
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
public class TemplateManager
{

    private List<AbstractGameTemplate> templates;
    private HydroangeasServer instance;

    public TemplateManager(HydroangeasServer instance)
    {

        this.instance = instance;

        templates = loadTemplates();

        loadQueues();
    }

    public void loadQueues()
    {
        instance.getLogger().info("Ajout des queues pour chaque Template:");
        for (AbstractGameTemplate template : templates)
        {
            Queue queue = instance.getQueueManager().getQueueByName(template.getId());
            if (queue == null)
            {
                instance.getQueueManager().addQueue(template);
            }
            else
            {
                queue.reload(template);
            }

            instance.getLogger().info("ID: " + template.getId() + " Jeu: " + template.getGameName() + " Map: " + template.getMapName());
        }
    }

    public List<AbstractGameTemplate> loadTemplates()
    {
        List<AbstractGameTemplate> result = new ArrayList<>();
        File directory = new File(MiscUtils.getApplicationDirectory(), "templates");
        try
        {
            File[] files = directory.listFiles();
            if (files == null) // Internal IO Exception
                throw new IOException("Internal IO Exception during listing of templates directory!");
            for (File file : files)
            {
                if (file.isFile() && file.getName().endsWith(".json"))
                {
                    try
                    {
                        JsonElement data = new JsonParser().parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        if (data == null)
                            throw new JsonParseException("JSON object return null");
                        if (data.getAsJsonObject().getAsJsonPrimitive("Type") != null && data.getAsJsonObject().getAsJsonPrimitive("Type").getAsString().equals("Package"))
                        {
                            result.add(new PackageGameTemplate(FilenameUtils.removeExtension(file.getName()), data));
                        } else
                        {
                            result.add(new SimpleGameTemplate(FilenameUtils.removeExtension(file.getName()), data));
                        }
                    } catch (JsonParseException | UnsupportedEncodingException e)
                    {
                        instance.getLogger().severe("Invalid template " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public void reload()
    {
        templates.clear();
        templates = loadTemplates();
        loadQueues();
    }

    public AbstractGameTemplate getTemplateByID(String id)
    {
        for (AbstractGameTemplate template : templates)
        {
            if (template.getId().equalsIgnoreCase(id))
            {
                return template;
            }
        }
        return null;
    }

    public AbstractGameTemplate getTemplateByGameAndMap(String game, String map)
    {
        for (AbstractGameTemplate template : templates)
        {
            if (template.getGameName().equalsIgnoreCase(game) && template.getMapName().equalsIgnoreCase(map))
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
        List<String> tmp = new ArrayList<>();
        templates.stream().forEach((template -> tmp.add(template.getId())));
        return tmp;
    }
}
