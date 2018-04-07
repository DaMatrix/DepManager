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

import net.daporkchop.depmanager.config.ConfigParser;
import net.daporkchop.depmanager.config.DependencyConfig;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mod(modid = DepManager.MOD_ID,
        name = DepManager.MOD_NAME,
        version = DepManager.VERSION)
public class DepManager {
    public static final File REPOSITORY = new File(".", "/depmanager/repo");
    public static final String MOD_ID = "depmanager";
    public static final String MOD_NAME = "DepManager";
    public static final String VERSION = "0.0.1";
    @Mod.Instance(MOD_ID)
    public static DepManager INSTANCE;
    public static Logger logger;

    static {
        REPOSITORY.mkdirs();
    }

    private Set<DependencyConfig> configs = new HashSet<>();

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        logger = LogManager.getLogger("depmanager");

        ModScanner.RAW_CONFIG.forEach(raw -> configs.add(ConfigParser.parse(raw)));
        Collection<File> files = DepFetcher.fetch(configs);
        files.forEach(file -> {
            try {
                Launch.classLoader.addURL(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) throws Exception {
        Class.forName("net.daporkchop.lib.math.vector.d.Vec2d");
        logger.info("YEAH BOII THIS WORKS!");
    }
}
