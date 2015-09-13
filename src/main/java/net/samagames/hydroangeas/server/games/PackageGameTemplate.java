package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Silva on 09/09/2015.
 */
public class PackageGameTemplate implements AbstractGameTemplate {

    private String id;

    private List<String> templates = new ArrayList<>();

    private SimpleGameTemplate currentTemplate;

    public PackageGameTemplate(String id, JsonElement data)
    {
        this.id = id;
        JsonObject object = data.getAsJsonObject();

        for(JsonElement element : object.getAsJsonArray("Templates"))
        {
            templates.add(element.getAsString());
        }
    }

    public boolean selectTemplate()
    {
        Random random = new Random();
        String selected = templates.get(random.nextInt(templates.size()));
        AbstractGameTemplate template = Hydroangeas.getInstance().getAsServer().getTemplateManager().getTemplateByID(selected);
        if(template == null || template instanceof PackageGameTemplate)
        {
            Hydroangeas.getInstance().getLogger().severe("Package Template: "+id+" contains an invalid sub template");
            return false;
        }else{
            currentTemplate = (SimpleGameTemplate) template;
        }
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGameName() {
        return currentTemplate.getGameName();
    }

    @Override
    public String getMapName() {
        return currentTemplate.getMapName();
    }

    @Override
    public int getMinSlot() {
        return currentTemplate.getMinSlot();
    }

    @Override
    public int getMaxSlot() {
        return currentTemplate.getMaxSlot();
    }

    @Override
    public JsonElement getOptions() {
        return currentTemplate.getOptions();
    }

    @Override
    public int getWeight() {
        return currentTemplate.getWeight();
    }

    @Override
    public boolean isCoupaing() {
        return currentTemplate.isCoupaing();
    }

    @Override
    public String toString()
    {
        return "Template id: " + id + ((isCoupaing())?" Coupaing Server ":" ");
    }
}
