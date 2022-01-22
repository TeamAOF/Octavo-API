package mod.octavo.api;

import mod.octavo.core.system.Pin;
import mod.octavo.core.system.ResearchEntry;
import mod.octavo.impl.section.CraftingSection;
import mod.octavo.impl.section.ImageSection;
import mod.octavo.impl.section.SmeltingSection;
import mod.octavo.impl.section.StringSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mod.octavo.util.StreamUtil.streamAndApply;

/**
 * Represents one section of content - for example, continuous text, an image, or an inline recipe. May provide a number of pins.
 */
public abstract class EntrySection{
	
	// static stuff
	private static Map<Identifier, Function<String, EntrySection>> factories = new LinkedHashMap<>();
	private static Map<Identifier, Function<NbtCompound, EntrySection>> deserializers = new LinkedHashMap<>();
	
	public static Function<String, EntrySection> getFactory(Identifier type){
		return factories.get(type);
	}
	
	public static EntrySection makeSection(Identifier type, String content){
		if(getFactory(type) != null)
			return getFactory(type).apply(content);
		else
			return null;
	}
	
	public static EntrySection deserialze(NbtCompound passData){
		Identifier type = new Identifier(passData.getString("type"));
		NbtCompound data = passData.getCompound("data");
		List<Requirement> requirements = streamAndApply(passData.getList("requirements", 10), NbtCompound.class, Requirement::deserialize).collect(Collectors.toList());
		if(deserializers.get(type) != null){
			EntrySection section = deserializers.get(type).apply(data);
			requirements.forEach(section::addRequirement);
			// recieving on client
			section.entry = new Identifier(passData.getString("entry"));
			return section;
		}
		return null;
	}

	// instance stuff
	
	protected List<Requirement> requirements = new ArrayList<>();
	protected Identifier entry;
	
	public void addRequirement(Requirement requirement){
		requirements.add(requirement);
	}
	
	public List<Requirement> getRequirements(){
		return Collections.unmodifiableList(requirements);
	}
	
	public NbtCompound getPassData(){
		NbtCompound nbt = new NbtCompound();
		nbt.putString("type", getType().toString());
		nbt.put("data", getData());
		nbt.putString("entry", getEntry().toString());
		
		NbtList list = new NbtList();
		getRequirements().forEach((requirement) -> list.add(requirement.getPassData()));
		nbt.put("requirements", list);
		
		return nbt;
	}
	
	public Identifier getEntry(){
		return entry;
	}
	
	public abstract Identifier getType();
	
	public abstract NbtCompound getData();
	
	public void addOwnRequirements(){
	}
	
	/**
	 * Returns a stream containing this entry's pins.
	 *
	 * @param index
	 * 		The index in the entry of this section.
	 * @param world
	 * 		The world the player is in.
	 * @param entry
	 * 		The entry this is in.
	 * @return This entry's pins.
	 */
	public Stream<Pin> getPins(int index, World world, ResearchEntry entry){
		return Stream.empty();
	}

	public static class ORegistry{
		public boolean registerFactory(Identifier id, Function<String, EntrySection> factory){
			factories.put(id, factory);
			return true;
		}
		public boolean registerDeserializer(Identifier id, Function<NbtCompound, EntrySection> deserializer){
			deserializers.put(id, deserializer);
			return true;
		}
	}
}