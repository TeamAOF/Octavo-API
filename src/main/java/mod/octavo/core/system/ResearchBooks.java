package mod.octavo.core.system;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores all of the research books.
 */
public class ResearchBooks{
	
	// public
	// thats a bad idea I think
	public static ArrayList<Identifier> disabled = new ArrayList<>();
	public static Map<Identifier, ResearchBook> books = new LinkedHashMap<>();
	public static Map<Identifier, Puzzle> puzzles = new LinkedHashMap<>();
	
	public static List<ResearchBook> getBooks(){
		return new ArrayList<>(books.values());
	}
	
	public static Stream<ResearchCategory> streamCategories(){
		return books.values().stream().flatMap(ResearchBook::streamCategories);
	}
	
	public static List<ResearchCategory> getCategories(){
		return streamCategories().collect(Collectors.toList());
	}
	
	public static ResearchCategory getCategory(Identifier key){
		return streamCategories().filter(x -> x.key().equals(key)).findFirst().orElse(null);
	}
	
	public static Stream<ResearchEntry> streamEntries(){
		return streamCategories().flatMap(ResearchCategory::streamEntries);
	}
	
	public static List<ResearchEntry> getEntries(){
		return streamEntries().collect(Collectors.toList());
	}
	
	public static Stream<ResearchEntry> streamChildrenOf(ResearchEntry parent){
		return streamEntries().filter(x -> x.parents().stream().anyMatch(it -> it.entry.equals(parent.key())));
	}
	
	public static List<ResearchEntry> getChildrenOf(ResearchEntry parent){
		return streamChildrenOf(parent).collect(Collectors.toList());
	}
	
	public static ResearchEntry getEntry(Identifier key){
		return streamEntries().filter(x -> x.key().equals(key)).findFirst().orElse(null);
	}

	static {
		//disabled.add(Arcana.arcLoc("illustrious_grimoire"));
		//disabled.add(Arcana.arcLoc("tainted_codex"));
		//disabled.add(Arcana.arcLoc("crimson_rites"));
	}
}