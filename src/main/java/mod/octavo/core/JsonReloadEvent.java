package mod.octavo.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.logging.LogManager;
import java.util.logging.Logger;

// Cleanup required. Merge with DatapackLoader recommended.
public class JsonReloadEvent {
    public static final Logger LOGGER = LogManager.getLogManager().getLogger("OctavoJsonReloadEvent");

    public static void register(){
        DatapackLoader loader = new DatapackLoader();
        ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
        serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public void reload(ResourceManager manager) {
                loader.clear();

                Collection<Identifier> resources = manager.findResources(OctavoReference.MODID, path -> path.endsWith(".json"));

                for (Identifier path : resources) {
                    try {
                        Resource resource = manager.getResource(path);
                        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                            JsonElement json = JsonParser.parseReader(reader);

                            Identifier identifier = identifierFromPath(path);

                            loader.apply(json, identifier);
                        }
                    } catch (IOException e) {
                        LOGGER.warning("Failed to read "+path+e);
                    }
                }
                loader.applyAfter();
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(OctavoReference.MODID,OctavoReference.MODID);
            }
        });
    }

    private static Identifier identifierFromPath(Identifier location) {
        String path = location.getPath();
        path = path.substring((OctavoReference.MODID+"/").length(), path.length() - ".json".length());
        return new Identifier(location.getNamespace(), path);
    }
}
