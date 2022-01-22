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
	public static Map<Identifier, Book> books = new LinkedHashMap<>();
	
	public static List<Book> getBooks(){
		return new ArrayList<>(books.values());
	}
	
	public static Stream<Category> streamCategories(){
		return books.values().stream().flatMap(Book::streamCategories);
	}
	
	public static List<Category> getCategories(){
		return streamCategories().collect(Collectors.toList());
	}
	
	public static Category getCategory(Identifier key){
		return streamCategories().filter(x -> x.key().equals(key)).findFirst().orElse(null);
	}
	
	public static Stream<ResearchEntry> streamEntries(){
		return streamCategories().flatMap(Category::streamEntries);
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
}