package mod.octavo.core.system;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

/**
 * Represents a whole research book, such as the Arcanum or Tainted Codex.
 * Contains a number of research categories, stored by key.
 */
public class Book {
	
	public Map<Identifier, Category> categories;
	private Identifier key;
	private String prefix;
	
	public Book(Identifier key, Map<Identifier, Category> categories, String prefix){
		this.categories = categories;
		this.key = key;
		this.prefix = prefix;
	}
	
	public Category getCategory(Identifier key){
		return categories.get(key);
	}
	
	public List<Category> getCategories(){
		return new ArrayList<>(categories.values());
	}
	
	public Stream<Category> streamCategories(){
		return categories.values().stream();
	}
	
	public Stream<ResearchEntry> streamEntries(){
		return streamCategories().flatMap(Category::streamEntries);
	}
	
	public List<ResearchEntry> getEntries(){
		return streamEntries().collect(Collectors.toList());
	}
	
	public ResearchEntry getEntry(Identifier key){
		return streamEntries().filter(entry -> entry.key().equals(key)).findFirst().orElse(null);
	}
	
	public Identifier getKey(){
		return key;
	}
	
	public Map<Identifier, Category> getCategoriesMap(){
		return Collections.unmodifiableMap(categories);
	}
	
	public String getPrefix(){
		return prefix;
	}
	
	public NbtCompound serialize(Identifier tag){
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", tag.toString());
		nbt.putString("prefix", prefix);
		NbtList list = new NbtList();
		int index = 0;
		for(Map.Entry<Identifier, Category> entry : categories.entrySet()){
			// enforce a specific order so things are transferred correctly
			list.add(entry.getValue().serialize(entry.getKey(), index));
			index++;
		}
		nbt.put("categories", list);
		return nbt;
	}
	
	public static Book deserialize(NbtCompound nbt){
		Identifier key = new Identifier(nbt.getString("id"));
		String prefix = nbt.getString("prefix");
		NbtList categoryList = nbt.getList("categories", 10);
		// need to have a book to put them *in*
		// book isn't in ClientBooks until all categories have been deserialized, so this is needed
		Map<Identifier, Category> c = new LinkedHashMap<>();
		Book book = new Book(key, c, prefix);
		
		Map<Identifier, Category> categories = StreamSupport.stream(categoryList.spliterator(), false).map(NbtCompound.class::cast).map(nbt1 -> Category.deserialize(nbt1, book)).sorted(Comparator.comparingInt(Category::serializationIndex)).collect(toMap(Category::key, Function.identity(), (a, b) -> a, LinkedHashMap::new));
		
		// this could be replaced by adding c to ClientBooks before deserializing, but this wouldn't look any different
		// and would leave a broken book in if an exception occurs.
		c.putAll(categories);
		return book;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof Book))
			return false;
		Book book = (Book)o;
		return getKey().equals(book.getKey());
	}
	
	public int hashCode(){
		return Objects.hash(getKey());
	}
}