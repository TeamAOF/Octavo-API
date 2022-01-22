package mod.octavo.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Octavo implements ModInitializer {

    // For testing. We don't leave this here.
    public static final Item ARCANUM = new OctavoBookItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1),new Identifier(OctavoReference.MODID, "arcanum"));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(OctavoReference.MODID, "arcanum"), ARCANUM);
        JsonReloadEvent.register();
    }
}
