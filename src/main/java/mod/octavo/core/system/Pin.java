package mod.octavo.core.system;

import mod.octavo.api.EntrySection;
import mod.octavo.api.Icon;
import mod.octavo.impl.section.AbstractCraftingSection;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * A quick reference to a specific page in a research book, which may point to an item's recipe.
 */
public class Pin {
	private @Nullable Item result;
	private ResearchEntry entry;
	private int stage;
	private Icon icon;

	public Pin(@Nullable Item result, ResearchEntry entry, int stage, Icon icon){
		this.result = result;
		this.entry = entry;
		this.stage = stage;
		this.icon = icon;
	}
	
	// Grabs the icon and item from the entry.
	// If a recipe isn't being pointed to, uses the icon of the entry its in.
	public Pin(ResearchEntry entry, int stage, World world){
		// Check if the section is a recipe.
		if(entry.sections().size() > stage){
			this.stage = stage;
			EntrySection section = entry.sections().get(stage);
			if(section instanceof AbstractCraftingSection && world.getRecipeManager().get(((AbstractCraftingSection)section).getRecipe()).isPresent()){
				Recipe<?> recipe = world.getRecipeManager().get(((AbstractCraftingSection)section).getRecipe()).get();
				this.icon = new Icon(recipe.getOutput());
				this.result = recipe.getOutput().getItem();
			}else
				this.icon = entry.icons().get(0);
		}else
			this.stage = 0;
		this.entry = entry;
	}
	
	public ResearchEntry getEntry(){
		return entry;
	}
	
	public int getStage(){
		return stage;
	}
	
	@Nullable
	public Item getResult(){
		return result;
	}
	
	public Icon getIcon(){
		return icon;
	}
}