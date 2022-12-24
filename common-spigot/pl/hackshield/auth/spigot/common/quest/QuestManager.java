/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.bukkit.event.Listener
 *  pl.hackshield.auth.api.quest.Quest
 *  pl.hackshield.auth.api.quest.condition.base.Condition
 *  pl.hackshield.auth.api.quest.deserializer.ConditionDeserializer
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.loader.endpoint.EndpointManager
 */
package pl.hackshield.auth.spigot.common.quest;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.bukkit.event.Listener;
import pl.hackshield.auth.api.quest.Quest;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.quest.deserializer.ConditionDeserializer;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.quest.endpoint.Quests;
import pl.hackshield.auth.spigot.common.quest.endpoint.QuestsProgress;
import pl.hackshield.auth.spigot.common.quest.endpoint.ServerInfo;
import pl.hackshield.auth.spigot.common.quest.listener.PlayerDigListener;
import pl.hackshield.auth.spigot.common.quest.task.UpdateQuestsBatch;

public class QuestManager {
    private final SpigotCommon plugin;
    private final EndpointManager endpointManager;
    private final Gson gson;
    private Quests questsEndpoint;
    private ServerInfo serverInfoEndpoint;
    private QuestsProgress questsProgressEndpoint;
    private final UpdateQuestsBatch task;
    private final Map<String, List<UUID>> cachedQuests = Maps.newConcurrentMap();
    private final Map<UUID, Quest> quests = Maps.newConcurrentMap();

    public QuestManager(SpigotCommon plugin, EndpointManager endpointManager) {
        this.plugin = plugin;
        this.endpointManager = endpointManager;
        this.gson = new GsonBuilder().registerTypeAdapter(Condition.class, (Object)new ConditionDeserializer()).create();
        this.questsEndpoint = new Quests(endpointManager);
        this.serverInfoEndpoint = new ServerInfo(endpointManager);
        this.questsProgressEndpoint = new QuestsProgress(endpointManager);
        this.task = new UpdateQuestsBatch(plugin, this);
        this.task.schedule();
        this.registerListeners();
        this.refreshQuestList();
    }

    private void registerListeners() {
        this.plugin.getPlugin().getServer().getPluginManager().registerEvents((Listener)new PlayerDigListener(this.plugin), this.plugin.getPlugin());
    }

    public boolean refreshQuestList() {
        try {
            ServerInfo.ServerInfoResponse serverInfoResponse = this.serverInfoEndpoint.serverInfo();
            if (serverInfoResponse.getError() != null) {
                this.plugin.getLogger().warning("Couldn't load server info! " + serverInfoResponse.getError().toString());
                return false;
            }
            Quests.QuestsResponse questsResponse = this.questsEndpoint.quests(this.gson);
            if (serverInfoResponse.getError() != null) {
                this.plugin.getLogger().warning("Couldn't load quests! " + questsResponse.getError().toString());
                return false;
            }
            List<Quest> questSeries = questsResponse.getData().getQuestSerieses();
            questSeries.clear();
            this.quests.clear();
            this.cachedQuests.clear();
            this.plugin.getLogger().info("Loaded " + questSeries.size() + " quests.");
            if (questSeries.size() == 0) {
                String json = "{'id': 'a28cbdbb-dcf4-48c8-8ebe-16a0477ecb9f',\n'name': 'zadanie 1',\n'condition': {\n    'event': 'break-block',\n    'block': {\n        'material': 'STONE'\n    }\n},\n'increment-by': 1,\n'target': 64,\n'period': 600000\n}";
                questSeries.add((Quest)this.gson.fromJson(json, Quest.class));
            }
            questSeries.forEach(quest -> this.quests.put(quest.getId(), (Quest)quest));
            this.quests.forEach((uuid, quest) -> {
                this.cachedQuests.computeIfAbsent(quest.getCondition().getEvent(), c -> new ArrayList()).add(uuid);
                Condition resetCondition = quest.getResetCondition();
                if (resetCondition != null) {
                    this.cachedQuests.computeIfAbsent(resetCondition.getEvent(), c -> new ArrayList()).add(uuid);
                }
            });
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't refresh quest list!", e);
            return false;
        }
        return true;
    }

    public void stop() {
        if (Objects.nonNull(this.task)) {
            this.task.stop();
        }
    }

    public void incrementProgress(Quest quest, UUID gamerId) {
        this.setProgress(quest, gamerId, quest.getIncrementBy());
    }

    public void resetProgress(Quest quest, UUID gamerId) {
        this.setProgress(quest, gamerId, Integer.MIN_VALUE);
    }

    private void setProgress(Quest quest, UUID gamerId, Integer value) {
        AtomicInteger progress = (AtomicInteger)quest.getBatch().get(gamerId);
        if (Objects.isNull(progress)) {
            quest.getBatch().put(gamerId, new AtomicInteger(value));
            return;
        }
        progress.addAndGet(value);
    }

    public QuestsProgress getQuestsProgressEndpoint() {
        return this.questsProgressEndpoint;
    }

    public Map<UUID, Quest> getQuests() {
        return this.quests;
    }

    public void fireQuestCheck(HackShieldUser user, Condition data) {
        String event = data.getEvent();
        this.cachedQuests.get(event).stream().filter(Objects::nonNull).map(this.quests::get).filter(q -> q.getCondition().isValid(data)).forEach(q -> this.plugin.getQuestManager().incrementProgress((Quest)q, user.getAccountID()));
        this.cachedQuests.get(event).stream().map(this.quests::get).filter(Objects::nonNull).filter(q -> q.getResetCondition() != null).filter(q -> q.getResetCondition().isValid(data)).forEach(q -> this.plugin.getQuestManager().resetProgress((Quest)q, user.getAccountID()));
    }
}

