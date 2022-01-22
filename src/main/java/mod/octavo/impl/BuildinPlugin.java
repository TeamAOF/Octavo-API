package mod.octavo.impl;

import mod.octavo.api.BackgroundLayer;
import mod.octavo.api.EntrySection;
import mod.octavo.api.OctavoPlugin;
import mod.octavo.api.Requirement;
import mod.octavo.impl.requirement.ItemRequirement;
import mod.octavo.impl.requirement.ItemTagRequirement;
import mod.octavo.impl.requirement.XpRequirement;
import mod.octavo.impl.section.CraftingSection;
import mod.octavo.impl.section.ImageSection;
import mod.octavo.impl.section.SmeltingSection;
import mod.octavo.impl.section.StringSection;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BuildinPlugin implements OctavoPlugin {
    @Override
    public void registerRequirements(Requirement.ORegistry registry) {
        // item and item tag requirement creation is handled by ResearchLoader -- an explicit form may be useful though.
        registry.registerDeserializer(ItemRequirement.TYPE, compound -> new ItemRequirement(Registry.ITEM.get(new Identifier(compound.getString("itemType")))));
        registry.registerDeserializer(ItemTagRequirement.TYPE, compound -> new ItemTagRequirement(new Identifier(compound.getString("itemTag"))));

        registry.registerFactory(XpRequirement.TYPE, __ -> new XpRequirement());
        registry.registerDeserializer(XpRequirement.TYPE, __ -> new XpRequirement());
    }

    @Override
    public void registerSections(EntrySection.ORegistry registry) {
        registry.registerFactory(StringSection.TYPE, StringSection::new);
        registry.registerDeserializer(StringSection.TYPE, nbt -> new StringSection(nbt.getString("text")));
        registry.registerFactory(CraftingSection.TYPE, CraftingSection::new);
        registry.registerDeserializer(CraftingSection.TYPE, nbt -> new CraftingSection(nbt.getString("recipe")));
        registry.registerFactory(SmeltingSection.TYPE, SmeltingSection::new);
        registry.registerDeserializer(SmeltingSection.TYPE, nbt -> new SmeltingSection(nbt.getString("recipe")));
        registry.registerFactory(ImageSection.TYPE, ImageSection::new);
        registry.registerDeserializer(ImageSection.TYPE, nbt -> new ImageSection(nbt.getString("image")));
    }

    @Override
    public void registerBackgroundLayers(BackgroundLayer.ORegistry registry) {
        registry.registerFactory(ImageLayer.TYPE, ImageLayer::new);
        registry.registerDeserializer(ImageLayer.TYPE, nbt -> new ImageLayer(nbt.getString("image")));
    }

    @Override
    public void registerBooks() {

    }
}
