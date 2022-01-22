package mod.octavo.core.system;

import mod.octavo.api.EntrySection;
import mod.octavo.api.Icon;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mod.octavo.util.StreamUtil.streamAndApply;

/**
 * Represents a node in the research tree. Stores an ordered list of entry sections representing its content.
 */
public class ResearchEntry{
	
	private Identifier key;
	private List<EntrySection> sections;
	private List<String> meta;
	private List<Parent> parents;
	private List<Icon> icons;
	private Category category;
	
	private String name, desc;
	
	private int x, y;
	
	public ResearchEntry(Identifier key, List<EntrySection> sections, List<Icon> icons, List<String> meta, List<Parent> parents, Category category, String name, String desc, int x, int y){
		this.key = key;
		this.sections = sections;
		this.icons = icons;
		this.meta = meta;
		this.parents = parents;
		this.category = category;
		this.name = name;
		this.desc = desc;
		this.x = x;
		this.y = y;
	}
	
	public List<EntrySection> sections(){
		return Collections.unmodifiableList(sections);
	}
	
	public List<Icon> icons(){
		return icons;
	}
	
	public List<String> meta(){
		return meta;
	}
	
	public List<Parent> parents(){
		return parents;
	}
	
	public Category category(){
		return category;
	}
	
	public Identifier key(){
		return key;
	}
	
	public String name(){
		return name;
	}
	
	public String description(){
		return desc;
	}
	
	public int x(){
		return x;
	}
	
	public int y(){
		return y;
	}
	
	public NbtCompound serialize(Identifier tag){
		NbtCompound nbt = new NbtCompound();
		// key
		nbt.putString("id", tag.toString());
		// name, desc
		nbt.putString("name", name());
		nbt.putString("desc", description());
		// x, y
		nbt.putInt("x", x());
		nbt.putInt("y", y());
		// sections
		NbtList list = new NbtList();
		sections().forEach((section) -> list.add(section.getPassData()));
		nbt.put("sections", list);
		// icons
		NbtList icons = new NbtList();
		icons().forEach((icon) -> icons.add(NbtString.of(icon.toString())));
		nbt.put("icons", icons);
		// parents
		NbtList parents = new NbtList();
		parents().forEach((parent) -> parents.add(NbtString.of(parent.asString())));
		nbt.put("parents", parents);
		// meta
		NbtList meta = new NbtList();
		meta().forEach((met) -> meta.add(NbtString.of(met)));
		nbt.put("meta", meta);
		return nbt;
	}
	
	public static ResearchEntry deserialize(NbtCompound nbt, Category in){
		Identifier key = new Identifier(nbt.getString("id"));
		String name = nbt.getString("name");
		String desc = nbt.getString("desc");
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		List<EntrySection> sections = streamAndApply(nbt.getList("sections", 10), NbtCompound.class, EntrySection::deserialze).collect(Collectors.toList());
		List<Parent> betterParents = streamAndApply(nbt.getList("parents", 8), NbtString.class, NbtString::asString).map(Parent::parse).collect(Collectors.toList());
		List<Icon> icons = streamAndApply(nbt.getList("icons", 8), NbtString.class, NbtString::asString).map(Icon::fromString).collect(Collectors.toList());
		List<String> meta = streamAndApply(nbt.getList("meta", 8), NbtString.class, NbtString::asString).collect(Collectors.toList());
		return new ResearchEntry(key, sections, icons, meta, betterParents, in, name, desc, x, y);
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof ResearchEntry))
			return false;
		ResearchEntry entry = (ResearchEntry)o;
		return key.equals(entry.key);
	}
	
	public int hashCode(){
		return Objects.hash(key);
	}
	
	/**
	 * Returns a stream containing all of the pins of contained sections.
	 *
	 * @param world
	 * 		The world the player is in.
	 * @return A stream containing the pins of contained sections.
	 */
	public Stream<Pin> getAllPins(World world){
		return sections().stream().flatMap(section -> section.getPins(sections.indexOf(section), world, this));
	}
}