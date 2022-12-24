/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.module;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;
import pl.hackshield.auth.loader.module.ModuleInfo;

public class ModuleLoader {
    private static final Logger LOGGER = Logger.getLogger("ModuleLoader");
    private final File moduleFolder;

    public void loadModule(ClassLoader classLoader, ModuleInfo ... moduleInfos) {
        try {
            URL[] urls2 = new URL[moduleInfos.length];
            for (int i = 0; i < moduleInfos.length; ++i) {
                urls2[i] = new File(this.moduleFolder, moduleInfos[i].getFileName()).toURI().toURL();
            }
            URLClassLoader urlClassLoader = new URLClassLoader(urls2, classLoader);
            for (ModuleInfo moduleInfo : moduleInfos) {
                LOGGER.info("Trying load module from '" + moduleInfo.getFileName() + "'...");
                Class<?> mainClass = urlClassLoader.loadClass(moduleInfo.getClassName());
                Object instance = mainClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                mainClass.getDeclaredMethod(moduleInfo.getInitMethod(), moduleInfo.getParameterTypes()).invoke(instance, moduleInfo.getArgs());
                LOGGER.info("Loaded module from '" + moduleInfo.getFileName() + "'!");
            }
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public ModuleLoader(File moduleFolder) {
        this.moduleFolder = moduleFolder;
    }
}

