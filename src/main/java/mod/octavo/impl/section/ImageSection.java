package mod.octavo.impl.section;

import mod.octavo.api.EntrySection;
import mod.octavo.core.Octavo;
import mod.octavo.core.OctavoReference;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ImageSection extends EntrySection{
	
	public static final Identifier TYPE = new Identifier(OctavoReference.MODID,"image");
	
	Identifier image;
	
	public ImageSection(String image){
		this(new Identifier(image));
	}
	
	public ImageSection(Identifier image){
		this.image = image;
	}
	
	public Identifier getType(){
		return TYPE;
	}
	
	public Identifier getImage(){
		return image;
	}
	
	public NbtCompound getData(){
		NbtCompound tag = new NbtCompound();
		tag.putString("image", getImage().toString());
		return tag;
	}
}