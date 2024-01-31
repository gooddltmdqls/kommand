package xyz.icetang.lib.plugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import kotlin.KotlinVersion;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;


public class KommandPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        // check kommand
        if (isRequire("xyz.icetang.lib.kommand.Kommand")) {
            var pluginMeta = classpathBuilder.getContext().getConfiguration();
            MavenLibraryResolver resolver = new MavenLibraryResolver();

            resolver.addRepository(new RemoteRepository.Builder("spring", "default", "https://repo1.maven.org/maven2/").build());

            resolver.addDependency(new Dependency(new DefaultArtifact("xyz.icetang.lib:kommand-core:3.1.11"), null));

            // check kotlin is shadowed
            if (isRequire("kotlin.KotlinVersion")) {
                resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.8.22"), null));
            }
            // check kotlin reflect is shadowed
            if (isRequire("kotlin.reflect.KClass")) {
                resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-reflect:1.8.22"), null));
            }

            classpathBuilder.addLibrary(resolver);
        }
    }

    private boolean isRequire(String name) {
        try {
            Class.forName(name, false, getClass().getClassLoader());
            return false;
        } catch (ClassNotFoundException ignored) {
            return true;
        }
    }

    private boolean isReobf() {
        try {
            var bukkitServer = Bukkit.getServer();
            var minecraftServer = bukkitServer.getClass().getMethod("getHandle").invoke(bukkitServer);
            var fieldServer = minecraftServer.getClass().getDeclaredFields()[0];

            System.out.println(fieldServer.getName());

            return fieldServer.getName().equals("SERVER");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
