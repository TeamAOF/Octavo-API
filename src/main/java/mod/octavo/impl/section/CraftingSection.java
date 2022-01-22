package mod.octavo.impl.section;

import net.minecraft.util.Identifier;

public class CraftingSection extends AbstractCraftingSection{
	
	public static final Identifier TYPE = new Identifier("minecraft","crafting");

	public CraftingSection(Identifier recipe){
		super(recipe);
	}

	public CraftingSection(String recipe){
		this(new Identifier(recipe));
	}
	
	public Identifier getType(){
		return TYPE;
	}
}