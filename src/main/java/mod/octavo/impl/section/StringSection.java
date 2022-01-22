package mod.octavo.impl.section;

import mod.octavo.api.EntrySection;
import mod.octavo.core.OctavoReference;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * An entry section that displays text over any number of pages.
 */
public class StringSection extends EntrySection{
	
	public static final Identifier TYPE = new Identifier(OctavoReference.MODID,"string");
	
	String content;
	
	public StringSection(String content){
		this.content = content;
	}
	
	public Identifier getType(){
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