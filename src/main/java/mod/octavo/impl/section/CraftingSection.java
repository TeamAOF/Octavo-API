package mod.octavo.impl.section;

import net.minecraft.util.Identifier;

public class CraftingSection extends AbstractCraftingSection{
	
	public static final String TYPE = "crafting";
	
	public CraftingSection(Identifier recipe){
		super(recipe);
	}
	
	public CraftingSection(String s){
		super(s);
	}
	
	public String getType(){
		return TYPE;
	}
}