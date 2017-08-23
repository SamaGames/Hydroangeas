package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.Hydroangeas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PackageGameTemplate implements AbstractGameTemplate
{

    private String id;

    private List<String> templates = new ArrayList<>();

    private SimpleGameTemplate currentTemplate;

    public PackageGameTemplate(String id, JsonElement data)
    {
        this.id = id;
        JsonObject object = data.getAsJsonObject();

        for (JsonElement element : object.getAsJsonArray("Templates"))
        {
            templates.add(element.getAsString());
        }
    }

    public boolean selectTemplate()
    {
        Random random = new Random();
        String selected = templates.get(random.nextInt(templates.size()));
        AbstractGameTemplate template = Hydroangeas.getInstance().getAsServer().getTemplateManager().getTemplateByID(selected);
        if (template == null || template instanceof PackageGameTemplate)
        {
            Hydroangeas.getInstance().getLogger().severe("Package Template: " + id + " contains an invalid sub template");
            return false;
        } else
        {
            currentTemplate = (SimpleGameTemplate) template;
        }
        return true;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getGameName()
    {
        return currentTemplate.getGameName();
    }

    @Override
    public String getMapName()
    {
        return currentTemplate.getMapName();
    }

    @Override
    public int getMinSlot()
    {
        return currentTemplate.getMinSlot();
    }

    @Override
    public int getMaxSlot()
    {
        return currentTemplate.getMaxSlot();
    }

    @Override
    public JsonElement getOptions()
    {
        return currentTemplate.getOptions();
    }

    @Override
    public int getWeight()
    {
        return currentTemplate.getWeight();
    }

    @Override
    public boolean isCoupaing()
    {
        return currentTemplate.isCoupaing();
    }

    @Override
    public String toString()
    {
        return "Template id: " + id + ((isCoupaing()) ? " Coupaing Server " : " ");
    }

    @Override
    public JsonElement getStartupOptions()
    {
        return currentTemplate.getStartupOptions();
    }

    @Override
    public void addTimeToStart(long time) {
        currentTemplate.addTimeToStart(time);
    }

    @Override
    public long getTimeToStart() {
        return currentTemplate.getTimeToStart();
    }

    @Override
    public void resetStats() {
        for(String template : templates)
        {
            try{
                Hydroangeas.getInstance().getAsServer().getTemplateManager().getTemplateByID(template).resetStats();
            }catch (Exception e)
            {

            }
        }
    }
}
