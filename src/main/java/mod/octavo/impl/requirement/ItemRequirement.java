package mod.octavo.impl.requirement;

import mod.octavo.api.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRequirement extends Requirement{
	
	// perhaps support NBT in the future? will be required for enchantments in the future at least.
	protected Item item;
	protected ItemStack stack;
	
	public static final Identifier TYPE = new Identifier("minecraft","item");
	
	public ItemRequirement(Item item){
		this.item = item;
	}
	
	public boolean satisfied(PlayerEntity player){
		return player.getInventory().func_234564_a_(x -> x.getItem() == item, 0, player.container.func_234641_j_()) >= (getAmount() == 0 ? 1 : getAmount());
	}
	
	public void take(PlayerEntity player){
		player.getInventory().func_234564_a_(x -> x.getItem() == item, getAmount(), player.container.func_234641_j_());
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound compound = new NbtCompound();
		compound.putString("itemType", String.valueOf(Registry.ITEM.getKey(item)));
		return compound;
	}
	
	public Item getItem(){
		return item;
	}
	
	public ItemStack getStack(){
		if(stack == null)
			return stack = new ItemStack(getItem());
		return stack;
	}
}