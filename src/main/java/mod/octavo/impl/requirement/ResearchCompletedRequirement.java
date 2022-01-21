package mod.octavo.impl.requirement;

import net.arcanamod.capabilities.Researcher;
import mod.octavo.core.system.Parent;
import mod.octavo.core.system.Requirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static net.arcanamod.Arcana.arcLoc;

public class ResearchCompletedRequirement extends Requirement{
	
	public static final Identifier TYPE = arcLoc("research_completed");
	
	protected Parent req;
	
	public ResearchCompletedRequirement(String req){
		this.req = Parent.parse(req);
	}
	
	public boolean satisfied(PlayerEntity player){
		return req.satisfiedBy(Researcher.getFrom(player));
	}
	
	public void take(PlayerEntity player){
		// no-op
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound compound = new NbtCompound();
		compound.putString("requirement", req.asString());
		return compound;
	}
}