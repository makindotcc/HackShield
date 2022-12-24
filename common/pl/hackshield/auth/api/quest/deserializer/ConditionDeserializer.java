/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.quest.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import pl.hackshield.auth.api.quest.condition.ConditionRegistry;
import pl.hackshield.auth.api.quest.condition.base.Condition;

public class ConditionDeserializer
implements JsonDeserializer<Condition> {
    private static final Gson GSON = new Gson();

    @Override
    public Condition deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        String conditionType = object.get("event").getAsString();
        Class<? extends Condition> conditionClass = ConditionRegistry.CONDITION_BY_NAME.get(conditionType);
        if (conditionClass == null) {
            return null;
        }
        return GSON.fromJson((JsonElement)object, conditionClass);
    }
}

