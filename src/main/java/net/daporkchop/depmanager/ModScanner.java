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

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static net.daporkchop.depmanager.DepManager.logger;

/**
 * Scans mod jars for a DepManager config, and if found, loads it
 *
 * @author DaPorkchop_
 */
public class ModScanner {
    public static Set<String> RAW_CONFIG = new HashSet<>();

    static {
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Scanning for dependencies",
                ((URLClassLoader) ModScanner.class.getClassLoader()).getURLs().length);
        try {
            for (URL url : ((URLClassLoader) ModScanner.class.getClassLoader()).getURLs()) {
                File file = new File(url.getPath());
                progressBar.step(file.getName());
                if (file.exists() && !file.isDirectory()) {
                    ZipInputStream zis = new ZipInputStream(
                            new BufferedInputStream(new FileInputStream(file)));
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().endsWith(".depmanager.json")) {
                            ZipFile zip = new ZipFile(new File(url.getPath()));
                            InputStream inputStream = zip.getInputStream(zip.getEntry(entry.getName()));
                            List<String> lines = IOUtils.readLines(inputStream, Charset.forName("UTF-8"));
                            inputStream.close();
                            StringBuilder builder = new StringBuilder();
                            lines.forEach(builder::append);
                            RAW_CONFIG.add(builder.toString().trim());
                            break;
                        }
                    }
                    zis.close();
                }
            }
        } catch (IOException e) {
            logger.fatal("IOException while scanning mod JARs!");
            logger.fatal(e.getMessage());
            FMLCommonHandler.instance().exitJava(1, true);
        }
        ProgressManager.pop(progressBar);
    }
}
