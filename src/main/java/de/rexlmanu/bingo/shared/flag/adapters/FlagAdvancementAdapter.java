package de.rexlmanu.bingo.shared.flag.adapters;

import com.google.gson.*;
import de.rexlmanu.bingo.game.flags.advancement.FlagAdvancement;

import java.lang.reflect.Type;

public class FlagAdvancementAdapter implements JsonSerializer<FlagAdvancement>, JsonDeserializer<FlagAdvancement> {
    @Override
    public FlagAdvancement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new FlagAdvancement(json.getAsJsonObject().get("advancement").getAsString());
    }

    @Override
    public JsonElement serialize(FlagAdvancement src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", "FlagAdvancement");
        object.addProperty("advancement", src.advancement().getKey().getKey());
        return object;
    }
}
