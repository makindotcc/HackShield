/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.loader.endpoint.endpoints.Stats$ServerStatsRequest
 */
package pl.hackshield.auth.common.task;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.loader.endpoint.endpoints.Stats;

public class UpdateStatsTask
implements Runnable {
    private static final long MB = 0x100000L;
    private final HackShieldPlugin plugin;
    private final MBeanServer mbs;
    private final ObjectName objectName;
    private final File disk;

    public UpdateStatsTask(HackShieldPlugin plugin) throws MalformedObjectNameException {
        this.plugin = plugin;
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        this.objectName = ObjectName.getInstance("java.lang:type=OperatingSystem");
        String path = System.getProperty("user.dir");
        String partition = "/";
        if (!path.startsWith(partition)) {
            partition = path.substring(0, path.indexOf("\\"));
        }
        this.disk = new File(partition);
    }

    @Override
    public void run() {
        try {
            int totalRam = this.getMaxRam(this.mbs, this.objectName);
            int totalDisk = (int)(this.disk.getTotalSpace() / 0x100000L);
            Stats.ServerStatsRequest stats = Stats.ServerStatsRequest.from((int)UpdateStatsTask.getProcessCpuLoadPercent(this.mbs, this.objectName), (int)totalRam, (int)(totalRam - this.getRamUsage(this.mbs, this.objectName)), (int)totalDisk, (int)(totalDisk - (int)(this.disk.getTotalSpace() / 0x100000L)), (String)"1.0.0");
            HackShieldPlugin.getInstance().getEndpointManager().getStats().update(stats);
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while updating server data!", e);
        }
    }

    private static int getProcessCpuLoadPercent(MBeanServer mbs, ObjectName name) throws Exception {
        Double value = (Double)mbs.getAttribute(name, "ProcessCpuLoad");
        return (int)Math.round(value * 100.0);
    }

    private int getMaxRam(MBeanServer mbs, ObjectName name) throws Exception {
        Long value = (Long)mbs.getAttribute(name, "TotalPhysicalMemorySize");
        return (int)(value / 0x100000L);
    }

    private int getRamUsage(MBeanServer mbs, ObjectName name) throws Exception {
        Long free = (Long)mbs.getAttribute(name, "FreePhysicalMemorySize");
        int max = this.getMaxRam(mbs, name);
        return (int)((long)max - free / 0x100000L);
    }
}

