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

import com.google.common.collect.Sets;
import net.daporkchop.depmanager.config.DependencyConfig;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * Actually downloads the mods into the classpath
 *
 * @author DaPorkchop_
 */
public class DepFetcher {

    private static DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    private static RepositorySystem system = newRepositorySystem(locator);
    private static RepositorySystemSession session = newSession(system);
    private static RemoteRepository porkchop = new RemoteRepository.Builder("DaPorkchop_", "default", "http://maven.daporkchop.net/").build();

    public static void fetch(Set<DependencyConfig> configs) {
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Fetching dependencies", configs.size());
        configs.forEach(config -> {
            progressBar.step(config.name);
            ProgressManager.ProgressBar depBar = ProgressManager.push(config.name, config.dependencies.size());
            config.dependencies.forEach(dep -> {
                depBar.step(dep.groupId + "." + dep.artifactId + ":" + dep.version);

                try {
                    Artifact artifact = new DefaultArtifact(dep.groupId + ":" + dep.artifactId + ":" + dep.version);

                    CollectRequest collectRequest = new CollectRequest(new Dependency(artifact, JavaScopes.COMPILE), Arrays.asList(porkchop));
                    DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
                    DependencyRequest request = new DependencyRequest(collectRequest, filter);
                    DependencyResult result = system.resolveDependencies(session, request);
                } catch (DependencyResolutionException e) {
                    e.printStackTrace();
                }
                //logger.warn("Unable to find artifact \"" + dep.groupId + "." + dep.artifactId + ":" + dep.version + "\n for configuration \"" + config.name + "\n");
            });
            ProgressManager.pop(depBar);
        });
        ProgressManager.pop(progressBar);
        FMLCommonHandler.instance().exitJava(0, true);
    }

    private static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(DepManager.REPOSITORY);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }

    public static void main(String... args) {
        DependencyConfig.Dep dep = new DependencyConfig.Dep();
        dep.groupId = "net.daporkchop.lib";
        dep.artifactId = "crypto";
        dep.version = "0.1.1";
        DependencyConfig config = new DependencyConfig();
        config.name = "Test!";
        config.dependencies = Arrays.asList(dep);
        fetch(Sets.newHashSet(config));
        //this works, why doesn't the other one?
    }
}
