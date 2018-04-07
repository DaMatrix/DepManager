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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.daporkchop.depmanager.DepManager.logger;

/**
 * Actually downloads the mods into the classpath
 *
 * @author DaPorkchop_
 */
public class DepFetcher {

    private static DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    private static RepositorySystem system = newRepositorySystem(locator);
    private static RepositorySystemSession session = newSession(system);
    private static RemoteRepository central = new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build(),
            curse = new RemoteRepository.Builder("CurseForge", "default", "https://minecraft.curseforge.com/api/maven/").build();

    public static Collection<File> fetch(Set<DependencyConfig> configs) {
        final Map<String, File> files = new Hashtable<>();
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Fetching dependencies", configs.size());
        configs.forEach(config -> {
            progressBar.step(config.name);
            logger.info("Resolving dependencies for " + config.name + "...");
            List<RemoteRepository> repositories = new ArrayList<>();
            repositories.add(central);
            repositories.add(curse);
            config.repositories.forEach(repo -> repositories.add(new RemoteRepository.Builder(repo.id, "default", repo.url).build()));

            ProgressManager.ProgressBar depBar = ProgressManager.push(config.name, config.dependencies.size());
            config.dependencies.forEach(dep -> {
                depBar.step(dep.groupId + "." + dep.artifactId + ":" + dep.version);
                logger.info("  Resolving " + depBar.getMessage() + "...");

                try {
                    Artifact artifact = new DefaultArtifact(dep.groupId + ":" + dep.artifactId + ":" + dep.version);

                    CollectRequest collectRequest = new CollectRequest(new Dependency(artifact, JavaScopes.COMPILE), repositories);
                    DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
                    DependencyRequest request = new DependencyRequest(collectRequest, filter);
                    DependencyResult result = system.resolveDependencies(session, request);
                    result.getArtifactResults().forEach(artifactResult -> {
                        Artifact a = artifactResult.getArtifact();
                        files.put(a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion(), a.getFile());
                    });
                } catch (DependencyResolutionException e) {
                    e.printStackTrace();
                    if (!Prompt.continueDownloadOnError(depBar.getMessage(), config.name)) {
                        FMLCommonHandler.instance().exitJava(1, false);
                    }
                }
            });
            ProgressManager.pop(depBar);
        });
        ProgressManager.pop(progressBar);
        logger.info("Resolved " + files.size() + " dependencies:");
        files.keySet().forEach(s -> logger.info("        " + s));
        return files.values();
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
}
