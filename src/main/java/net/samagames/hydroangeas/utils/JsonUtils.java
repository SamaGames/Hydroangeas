package net.samagames.hydroangeas.utils;

import com.google.gson.JsonElement;

public class JsonUtils
{
    public static String getStringOrNull(JsonElement jsonElement)
    {
        return jsonElement.isJsonNull() ? null : jsonElement.getAsString();
    }
}
