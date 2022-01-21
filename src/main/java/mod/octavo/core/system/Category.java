package mod.octavo.core.system;

import mod.octavo.api.BackgroundLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a research tab. Contains a number of research entries, stored by key.
 */
public class Category {
	
	protected Map<Identifier, ResearchEntry> entries;
	private Identifier key, icon, bg, requirement;
	private Book in;
	private String name;
	private List<BackgroundLayer> bgs = new ArrayList<>();
	
	protected int serializationIndex = 0;
	
	public Category(Map<Identifier, ResearchEntry> entries, Identifier key, Identifier icon, Identifier bg, Identifier requirement, String name, Book in){
		this.entries = entries;
		this.key = key;
		this.requirement = requirement;
		this.in = in;
		this.icon = icon;
		this.name = name;
		this.bg = bg;
	}
	
	public Identifier key(){
		return key;
	}
	
	public ResearchEntry entry(ResearchEntry entry){
		return entries.get(entry.key());
	}
	
	public List<ResearchEntry> entries(){
		return new ArrayList<>(entries.values());
	}
	
	public Stream<ResearchEntry> streamEntries(){
		return entries.values().stream();
	}
	
	public Book book(){
		return in;
	}
	
	public Identifier icon(){
		return icon;
	}
	
	public String name(){
		return name;
	}
	
	public Identifier bg(){
		return bg;
	}
	
	public List<BackgroundLayer> getBgs(){
		return bgs;
	}
	
	int serializationIndex(){
		return serializationIndex;
	}
	
	public Identifier requirement(){
		return requirement;
	}
	
	public NbtCompound serialize(Identifier tag, int index){
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", tag.toString());
		nbt.putString("icon", icon.toString());
		nbt.putString("bg", bg.toString());
		nbt.putString("requirement", requirement != null ? requirement.toString() : "null");
		nbt.putString("name", name);
		nbt.putInt("index", index);
		
		NbtList list = new NbtList();
		entries.forEach((location, entry) -> list.add(entry.serialize(location)));
		nbt.put("entries", list);
		
		NbtList bgsList = new NbtList();
		bgs.forEach(layer -> bgsList.add(layer.getPassData()));
		nbt.put("bgs", bgsList);
		return nbt;
	}
	
	public static Category deserialize(NbtCompound nbt, Book in){
		Identifier key = new Identifier(nbt.getString("id"));
		Identifier icon = new Identifier(nbt.getString("icon"));
		Identifier bg = new Identifier(nbt.getString("bg"));
		Identifier requirement = nbt.getString("requirement").equals("null") ? null : new Identifier(nbt.getString("requirement"));
		String name = nbt.getString("name");
		NbtList entriesList = nbt.getList("entries", 10);
		// same story as ResearchBook
		Map<Identifier, ResearchEntry> c = new LinkedHashMap<>();
		Category category = new Category(c, key, icon, bg, requirement, name, in);
		category.serializationIndex = nbt.getInt("index");
		
		Map<Identifier, ResearchEntry> entries = entriesList.stream().map(NbtCompound.class::cast).map((NbtCompound nbt1) -> ResearchEntry.deserialize(nbt1, category)).collect(Collectors.toMap(ResearchEntry::key, Function.identity(), (a, b) -> a));
		c.putAll(entries);
		
		category.bgs = nbt.getList("bgs", Constants.NBT.TAG_COMPOUND).stream().map(NbtCompound.class::cast).map(BackgroundLayer::deserialize).collect(Collectors.toList());
		return category;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof Category))
			return false;
		Category category = (Category)o;
		return key().equals(category.key());
	}
	
	public int hashCode(){
		return Objects.hash(key());
	}
}