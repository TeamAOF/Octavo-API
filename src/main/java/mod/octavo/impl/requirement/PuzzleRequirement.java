package mod.octavo.impl.requirement;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.network.Connection;
import mod.octavo.core.system.Puzzle;
import mod.octavo.core.system.Requirement;
import mod.octavo.core.system.ResearchBooks;
import mod.octavo.core.system.ResearchEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static net.arcanamod.Arcana.arcLoc;

public class PuzzleRequirement extends Requirement{
	
	public static final Identifier TYPE = arcLoc("puzzle");
	
	protected Identifier puzzleId;
	
	public PuzzleRequirement(Identifier puzzleId){
		this.puzzleId = puzzleId;
	}
	
	public boolean satisfied(PlayerEntity player){
		return Researcher.getFrom(player).isPuzzleCompleted(ResearchBooks.puzzles.get(puzzleId));
	}
	
	public void take(PlayerEntity player){
		// no-op
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound compound = new NbtCompound();
		compound.putString("puzzle", puzzleId.toString());
		return compound;
	}
	
	public boolean onClick(ResearchEntry entry, PlayerEntity player){
		Puzzle puzzle = ResearchBooks.puzzles.get(puzzleId);
		if(!(puzzle instanceof Fieldwork || satisfied(player)))
			Connection.sendGetNoteHandler(puzzleId, entry.key().toString());
		return false;
	}
	
	public Identifier getPuzzleId(){
		return puzzleId;
	}
}