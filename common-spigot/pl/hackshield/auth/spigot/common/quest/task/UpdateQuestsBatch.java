/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.spigot.common.quest.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.quest.QuestBatch;
import pl.hackshield.auth.spigot.common.quest.QuestManager;
import pl.hackshield.auth.spigot.common.quest.endpoint.QuestsProgress;

public class UpdateQuestsBatch
implements Runnable {
    private final SpigotCommon plugin;
    private final QuestManager questManager;
    private final List<QuestBatch> batchList = new ArrayList<QuestBatch>();
    private int taskId = -1;

    public UpdateQuestsBatch(SpigotCommon plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;
    }

    public void schedule() {
        this.plugin.runRepeatingAsync(this, 5L, 5L, TimeUnit.SECONDS);
    }

    public void stop() {
        if (this.taskId > -1) {
            // empty if block
        }
    }

    @Override
    public void run() {
        try {
            if (this.batchList.size() == 0) {
                this.questManager.getQuests().values().forEach(quest -> quest.getBatch().keySet().forEach(gamerId -> this.batchList.add(new QuestBatch((UUID)gamerId, quest.getId(), ((AtomicInteger)quest.getBatch().remove(gamerId)).get()))));
            }
            if (this.batchList.size() == 0) {
                return;
            }
            this.batchList.forEach(b -> this.plugin.getLogger().info(b.toString()));
            QuestsProgress.QuestsProgressResponse response = this.questManager.getQuestsProgressEndpoint().progress(QuestsProgress.QuestsProgressRequest.from(this.batchList));
            if (response.getError() != null) {
                this.plugin.getPlugin().getLogger().warning("Failed send quests progress! [" + response.getError().getCode() + "] " + response.getError().getMessage());
                return;
            }
            this.batchList.clear();
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't send quest progress update!", e);
        }
    }
}

