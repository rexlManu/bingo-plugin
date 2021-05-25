package de.rexlmanu.bingo.shared.flag.adapters;

import com.google.gson.*;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import org.bukkit.Material;

import java.lang.reflect.Type;

public class FlagItemAdapter implements JsonSerializer<FlagItem>, JsonDeserializer<FlagItem> {

    @Override
    public FlagItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new FlagItem(Material.valueOf(json.getAsJsonObject().get("material").getAsString().toUpperCase()));
    }

    @Override
    public JsonElement serialize(FlagItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", "FlagItem");
        object.addProperty("material", src.material().name().toLowerCase());
        return object;
    }
}
