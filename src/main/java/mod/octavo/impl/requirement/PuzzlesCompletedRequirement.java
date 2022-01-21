package mod.octavo.impl.requirement;

import net.arcanamod.capabilities.Researcher;
import mod.octavo.core.system.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static net.arcanamod.Arcana.arcLoc;

public class PuzzlesCompletedRequirement extends Requirement{
	
	public static final Identifier TYPE = arcLoc("puzzles_completed");
	
	public boolean satisfied(PlayerEntity player){
		return Researcher.getFrom(player).getPuzzlesCompleted() >= getAmount();
	}
	
	public void take(PlayerEntity player){
		// no-op
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		return new NbtCompound();
	}
}