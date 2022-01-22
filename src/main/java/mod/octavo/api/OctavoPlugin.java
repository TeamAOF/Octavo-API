package mod.octavo.api;

public interface OctavoPlugin {
    void registerRequirements(Requirement.ORegistry registry);
    void registerSections(EntrySection.ORegistry registry);
    void registerBackgroundLayers(BackgroundLayer.ORegistry registry);
    void registerBooks();
}
