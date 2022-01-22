package mod.octavo.impl.section;

import net.minecraft.util.Identifier;

public class SmeltingSection extends AbstractCraftingSection{
	
	public static final Identifier TYPE = new Identifier("minecraft","smelting");
	
	public SmeltingSection(Identifier recipe){
		super(recipe);
	}

	public SmeltingSection(String recipe){
		this(new Identifier(recipe));
	}
	
	public Identifier getType(){
		return TYPE;
	}
}
