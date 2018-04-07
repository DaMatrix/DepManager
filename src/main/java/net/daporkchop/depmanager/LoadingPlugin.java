/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018 DaPorkchop_
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.depmanager;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class LoadingPlugin implements IFMLLoadingPlugin {
    public LoadingPlugin() throws Exception {
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        URI uri = url.toURI();
        File file = new File(uri);
        FMLLog.info("URL: " + url);
        FMLLog.info("URI: " + uri);
        FMLLog.info("File: " + file.getAbsolutePath());
        //Launch.classLoader.addURL(url);

        FMLLog.info("Hackery time! Let's remove \"org.apache.\" from the LaunchClassLoader exclusions.");
        try {
            Field field = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            field.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>) field.get(Launch.classLoader);
            if (classLoaderExceptions.contains("org.apache.")) {
                classLoaderExceptions.remove("org.apache.");

                classLoaderExceptions.add("org.apache.commons");
                classLoaderExceptions.add("org.apache.http");
                classLoaderExceptions.add("org.apache.logging");
                FMLLog.info("Removed!");
            } else {
                FMLLog.bigWarning("ClassLoader exception list didn't contain \"org.apache.\"! Things will probably break!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(1, true);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
