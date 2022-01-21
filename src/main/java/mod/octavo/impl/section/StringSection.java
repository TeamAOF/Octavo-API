package mod.octavo.impl.section;

import mod.octavo.api.EntrySection;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

/**
 * An entry section that displays text over any number of pages.
 */
public class StringSection extends EntrySection{
	
	public static final String TYPE = "string";
	
	String content;
	
	public StringSection(String content){
		this.content = content;
	}
	
	public String getType(){
		return TYPE;
	}
	
	public NbtCompound getData(){
		NbtCompound tag = new NbtCompound();
		tag.putString("text", getText());
		return tag;
	}
	
	public String getText(){
		return content;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		StringSection section = (StringSection)o;
		return content.equals(section.content);
	}
	
	public int hashCode(){
		return Objects.hash(content);
	}
}