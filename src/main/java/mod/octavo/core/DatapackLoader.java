package mod.octavo.core;

import com.google.gson.*;
import mod.octavo.api.BackgroundLayer;
import mod.octavo.api.EntrySection;
import mod.octavo.api.Icon;
import mod.octavo.api.Requirement;
import mod.octavo.core.system.*;
import mod.octavo.impl.requirement.ItemRequirement;
import mod.octavo.impl.requirement.ItemTagRequirement;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// Cleanup required.
public class DatapackLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Map<Identifier, JsonArray> bookQueue = new LinkedHashMap<>();
    private static Map<Identifier, JsonArray> categoryQueue = new LinkedHashMap<>();
    private static Map<Identifier, JsonArray> entryQueue = new LinkedHashMap<>();
    private static Map<Identifier, JsonArray> puzzleQueue = new LinkedHashMap<>();

    public void clear() {
        bookQueue.clear();
        categoryQueue.clear();
        entryQueue.clear();
        puzzleQueue.clear();
    }

    public void apply(JsonElement json, Identifier identifier) {
        if(json.isJsonObject())
            applyJson(json.getAsJsonObject(), identifier);
    }

    public void applyAfter(){
        bookQueue.forEach(DatapackLoader::applyBooksArray);
        categoryQueue.forEach(DatapackLoader::applyCategoriesArray);
        entryQueue.forEach(DatapackLoader::applyEntriesArray);
    }

    private static void applyBooksArray(Identifier rl, JsonArray books){
        for(JsonElement bookElement : books){
            if(!bookElement.isJsonObject())
                LOGGER.error("Non-object found in books array in " + rl + "!");
            else{
                JsonObject book = bookElement.getAsJsonObject();
                // expecting key, prefix
                Identifier key = new Identifier(book.get("key").getAsString());
                String prefix = book.get("prefix").getAsString();
                Book bookObject = new Book(key, new LinkedHashMap<>(), prefix);
                ResearchBooks.books.putIfAbsent(key, bookObject);
                LOGGER.info("Loaded book " + key);
            }
        }
    }

    private static void applyCategoriesArray(Identifier rl, JsonArray categories){
        for(JsonElement categoryElement : categories){
            if(!categoryElement.isJsonObject())
                LOGGER.error("Non-object found in categories array in " + rl + "!");
            else{
                JsonObject category = categoryElement.getAsJsonObject();
                // expecting key, in, icon, bg, optionally bgs
                Identifier key = new Identifier(category.get("key").getAsString());
                Identifier bg = new Identifier(category.get("bg").getAsString());
                bg = new Identifier(bg.getNamespace(), "textures/" + bg.getPath());
                Identifier icon = new Identifier(category.get("icon").getAsString());
                icon = new Identifier(icon.getNamespace(), "textures/" + icon.getPath());
                String name = category.get("name").getAsString();
                Identifier requirement = category.has("requires") ? new Identifier(category.get("requires").getAsString()) : null;
                Book in = ResearchBooks.books.get(new Identifier(category.get("in").getAsString()));
                Category categoryObject = new Category(new LinkedHashMap<>(), key, icon, bg, requirement, name, in);
                if(category.has("bgs")){
                    JsonArray layers = category.getAsJsonArray("bgs");
                    for(JsonElement layerElem : layers){
                        JsonObject layerObj = layerElem.getAsJsonObject();
                        BackgroundLayer layer = BackgroundLayer.makeLayer(
                                new Identifier(layerObj.getAsJsonPrimitive("type").getAsString()),
                                layerObj,
                                rl,
                                layerObj.getAsJsonPrimitive("speed").getAsFloat(),
                                layerObj.has("vanishZoom") ? layerObj.getAsJsonPrimitive("vanishZoom").getAsFloat() : -1);
                        if(layer != null)
                            categoryObject.getBgs().add(layer);
                    }
                }
                in.categories.putIfAbsent(key, categoryObject);
            }
        }
    }

    private static void applyEntriesArray(Identifier rl, JsonArray entries){
        for(JsonElement entryElement : entries){
            if(!entryElement.isJsonObject())
                LOGGER.error("Non-object found in entries array in " + rl + "!");
            else{
                JsonObject entry = entryElement.getAsJsonObject();

                // expecting key, name, desc, icons, category, x, y, sections
                Identifier key = new Identifier(entry.get("key").getAsString());
                String name = entry.get("name").getAsString();
                String desc = entry.has("desc") ? entry.get("desc").getAsString() : "";
                List<Icon> icons = idsToIcons(entry.getAsJsonArray("icons"), rl);
                Category category = ResearchBooks.getCategory(new Identifier(entry.get("category").getAsString()));
                int x = entry.get("x").getAsInt();
                int y = entry.get("y").getAsInt();
                List<EntrySection> sections = jsonToSections(entry.getAsJsonArray("sections"), rl);

                // optionally parents, meta
                List<Parent> parents = new ArrayList<>();
                if(entry.has("parents"))
                    parents = StreamSupport.stream(entry.getAsJsonArray("parents").spliterator(), false).map(JsonElement::getAsString).map(Parent::parse).collect(Collectors.toList());

                List<String> meta = new ArrayList<>();
                if(entry.has("meta"))
                    meta = StreamSupport.stream(entry.getAsJsonArray("meta").spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList());

                ResearchEntry entryObject = new ResearchEntry(key, sections, icons, meta, parents, category, name, desc, x, y);
                category.entries.putIfAbsent(key, entryObject);
                sections.forEach(section -> section.entry = entryObject.key());
            }
        }
    }

    public static void applyJson(JsonObject json, Identifier rl){
        if(json.has("books")){
            JsonArray books = json.getAsJsonArray("books");
            bookQueue.put(rl, books);
        }
        if(json.has("categories")){
            JsonArray categories = json.getAsJsonArray("categories");
            categoryQueue.put(rl, categories);
        }
        if(json.has("entries")){
            JsonArray entries = json.getAsJsonArray("entries");
            entryQueue.put(rl, entries);
        }
    }

    private static List<Icon> idsToIcons(JsonArray itemIds, Identifier rl){
        List<Icon> ret = new ArrayList<>();
        for(JsonElement element : itemIds){
            ret.add(Icon.fromString(element.getAsString()));
        }
        if(ret.isEmpty())
            LOGGER.error("An entry has 0 icons in " + rl + "!");
        return ret;
    }

    private static List<EntrySection> jsonToSections(JsonArray sections, Identifier file){
        List<EntrySection> ret = new ArrayList<>();
        for(JsonElement sectionElement : sections)
            if(sectionElement.isJsonObject()){
                // expecting type, content
                JsonObject section = sectionElement.getAsJsonObject();
                Identifier type = new Identifier(section.get("type").getAsString());
                String content = section.get("content").getAsString();
                EntrySection es = EntrySection.makeSection(type, content);
                if(es != null){
                    if(section.has("requirements"))
                        if(section.get("requirements").isJsonArray()){
                            for(Requirement requirement : jsonToRequirements(section.get("requirements").getAsJsonArray(), file))
                                if(requirement != null)
                                    es.addRequirement(requirement);
                        }else
                            LOGGER.error("Non-array named \"requirements\" found in " + file + "!");
                    es.addOwnRequirements();
                    ret.add(es);
                }else if(EntrySection.getFactory(type) == null)
                    LOGGER.error("Invalid EntrySection type \"" + type + "\" referenced in " + file + "!");
                else
                    LOGGER.error("Invalid EntrySection content \"" + content + "\" for type \"" + type + "\" used in file " + file + "!");
            }else
                LOGGER.error("Non-object found in sections array in " + file + "!");
        return ret;
    }

    private static List<Requirement> jsonToRequirements(JsonArray requirements, Identifier file){
        List<Requirement> ret = new ArrayList<>();
        for(JsonElement requirementElement : requirements){
            if(requirementElement.isJsonPrimitive()){
                String desc = requirementElement.getAsString();
                int amount = 1;
                // if it has * in it, then its amount is not one
                if(desc.contains("*")){
                    String[] parts = desc.split("\\*");
                    if(parts.length != 2)
                        LOGGER.error("Multiple \"*\"s found in requirement in " + file + "!");
                    desc = parts[parts.length - 1];
                    amount = Integer.parseInt(parts[0]);
                }
                List<String> params = new ArrayList<>();
                // document this better.
                // If this has a "{" it has parameters; remove those
                if(desc.contains("{") && desc.endsWith("}")){
                    String[] param_parts = desc.split("\\{", 2);
                    desc = param_parts[0];
                    params = Arrays.asList(param_parts[1].substring(0, param_parts[1].length() - 1).split(", "));
                }
                // If this has "::" it's a custom requirement
                if(desc.contains("::")){
                    String[] parts = desc.split("::");
                    if(parts.length != 2)
                        LOGGER.error("Multiple \"::\"s found in requirement in " + file + "!");
                    Identifier type = new Identifier(parts[0], parts[1]);
                    Requirement add = Requirement.makeRequirement(type, params);
                    if(add != null){
                        add.amount = amount;
                        ret.add(add);
                    }else
                        LOGGER.error("Invalid requirement type " + type + " found in file " + file + "!");
                    // if this begins with a hash
                }else if(desc.startsWith("#")){
                    // its a tag
                    Identifier itemTagLoc = new Identifier(desc.substring(1));
                    Tag<Item> itemTag = ItemTags.getTagGroup().getTag(itemTagLoc);
                    if(itemTag != null){
                        ItemTagRequirement tagReq = new ItemTagRequirement(itemTag, itemTagLoc);
                        tagReq.amount = amount;
                        ret.add(tagReq);
                    }else
                        LOGGER.error("Invalid item tag " + itemTagLoc + " found in file " + file + "!");
                }else{
                    // its an item
                    Identifier item = new Identifier(desc);
                    Item value = Registry.ITEM.get(item);
                    if(value != null){
                        ItemRequirement add = new ItemRequirement(value);
                        add.amount = amount;
                        ret.add(add);
                    }else
                        LOGGER.error("Invalid item " + item + " found in file " + file + "!");
                }
            }
        }
        return ret;
    }
}
