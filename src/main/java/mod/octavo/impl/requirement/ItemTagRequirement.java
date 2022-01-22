package mod.octavo.impl.requirement;

import mod.octavo.api.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class ItemTagRequirement extends Requirement{
	
	protected Tag<Item> tag;
	protected Identifier tagName;
	
	public static final Identifier TYPE = new Identifier("minecraft","item_tag");
	
	public ItemTagRequirement(Identifier tagName){
		this(ItemTags.getTagGroup().getTag(tagName), tagName);
	}
	
	public ItemTagRequirement(Tag<Item> tag, Identifier tagName){
		this.tag = tag;
	}
	
	public boolean satisfied(PlayerEntity player){
		return player.getInventory().remove(x -> tag.contains(x.getItem()), 0, player.playerScreenHandler.getCraftingInput()) >= (getAmount() == 0 ? 1 : getAmount());
	}
	
	public void take(PlayerEntity player){
		player.getInventory().remove(x -> tag.contains(x.getItem()), getAmount(), player.playerScreenHandler.getCraftingInput());
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound compound = new NbtCompound();
		compound.putString("itemTag", tagName.toString());
		return compound;
	}
	
	public Tag<Item> getTag(){
		return tag;
	}
	
	public Identifier getTagName(){
		return tagName;
	}
}