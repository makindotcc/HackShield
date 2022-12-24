/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.quest.condition;

import java.util.HashMap;
import java.util.Map;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.quest.condition.impl.BreakBlockCondition;
import pl.hackshield.auth.api.quest.condition.impl.TravelDistanceCondition;

public class ConditionRegistry {
    public static final Map<String, Class<? extends Condition>> CONDITION_BY_NAME = new HashMap<String, Class<? extends Condition>>();
    public static final Map<Class<? extends Condition>, String> CONDITION_BY_CLASS = new HashMap<Class<? extends Condition>, String>();

    private static void register(String name, Class<? extends Condition> conditionClass) {
        CONDITION_BY_NAME.put(name, conditionClass);
        CONDITION_BY_CLASS.put(conditionClass, name);
    }

    static {
        ConditionRegistry.register("break-block", BreakBlockCondition.class);
        ConditionRegistry.register("travel-distance", TravelDistanceCondition.class);
    }
}

