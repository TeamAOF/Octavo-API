package mod.octavo.impl.requirement;

import mod.octavo.core.system.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static net.arcanamod.Arcana.arcLoc;

public class XpRequirement extends Requirement{
	
	public static final Identifier TYPE = arcLoc("xp");
	
	public boolean satisfied(PlayerEntity player){
		return player.experienceLevel >= getAmount();
	}
	
	public void take(PlayerEntity player){
		player.experienceLevel -= getAmount();
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		return new NbtCompound();
	}
}