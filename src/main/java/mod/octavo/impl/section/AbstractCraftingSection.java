package mod.octavo.impl.section;

import mod.octavo.api.EntrySection;
import mod.octavo.api.Icon;
import mod.octavo.core.system.Pin;
import mod.octavo.core.system.ResearchEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCraftingSection extends EntrySection{

	Identifier recipe;

	public AbstractCraftingSection(Identifier recipe){
		this.recipe = recipe;
	}
	
	public AbsIdentifiertion(String s){
		this(new Identifier(s));
	}
	
	public NbtCompound getData(){
		NbtCompound compound = new NbtCompound();
		compound.putString("recipe", recipe.toString());
		return compound;
	}

	public Identifier getRecipe(){
		return recipe;
	}
	
	public Stream<Pin> getPins(int index, World world, ResearchEntry entry){
		// if the recipe exists,
		Optional<? extends Recipe<?>> recipe = world.getRecipeManager().getRecipe(this.recipe);
		if(recipe.isPresent()){
			// get the item as the icon
			ItemStack output = recipe.get().getOutput();
			Icon icon = new Icon(output.getItem().getRegistryName(), output);
			// and return a pin that points to this
			return Stream.of(new Pin(output.getItem(), entry, index, icon));
		}
		return super.getPins(index, world, entry);
	}
}