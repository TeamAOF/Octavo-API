package mod.octavo.impl.requirement;

import mod.octavo.core.system.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Identifier;

import static net.arcanamod.Arcana.arcLoc;

public class ItemTagRequirement extends Requirement{
	
	protected ITag<Item> tag;
	protected Identifier tagName;
	
	public static final Identifier TYPE = arcLoc("item_tag");
	
	public ItemTagRequirement(Identifier tagName){
		this(ItemTags.getCollection().get(tagName), tagName);
	}
	
	public ItemTagRequirement(ITag<Item> tag, Identifier tagName){
		this.tag = tag;
	}
	
	public boolean satisfied(PlayerEntity player){
		return player.inventory.func_234564_a_(x -> x.getItem().isIn(tag), 0, player.container.func_234641_j_()) >= (getAmount() == 0 ? 1 : getAmount());
	}
	
	public void take(PlayerEntity player){
		player.inventory.func_234564_a_(x -> x.getItem().isIn(tag), getAmount(), player.container.func_234641_j_());
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound compound = new NbtCompound();
		compound.putString("itemTag", tagName.toString());
		return compound;
	}
	
	public ITag<Item> getTag(){
		return tag;
	}
	
	public Identifier getTagName(){
		return tagName;
	}
}