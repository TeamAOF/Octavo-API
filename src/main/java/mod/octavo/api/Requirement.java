package mod.octavo.api;

import mod.octavo.core.system.ResearchEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Requirement{
	
	////////// static stuff
	
	private static Map<Identifier, Function<List<String>, Requirement>> factories = new LinkedHashMap<>();
	private static Map<Identifier, Function<NbtCompound, Requirement>> deserializers = new LinkedHashMap<>();
	
	public static Requirement makeRequirement(Identifier type, List<String> content){
		if(factories.get(type) != null)
			return factories.get(type).apply(content);
		else
			return null;
	}
	
	public static Requirement deserialize(NbtCompound passData){
		Identifier type = new Identifier(passData.getString("type"));
		NbtCompound data = passData.getCompound("data");
		int amount = passData.getInt("amount");
		if(deserializers.get(type) != null){
			Requirement requirement = deserializers.get(type).apply(data);
			requirement.amount = amount;
			return requirement;
		}
		return null;
	}
	
	////////// instance stuff
	
	public int amount = 1;
	
	public int getAmount(){
		return amount;
	}
	
	public NbtCompound getPassData(){
		NbtCompound nbt = new NbtCompound();
		nbt.putString("type", type().toString());
		nbt.put("data", data());
		nbt.putInt("amount", getAmount());
		return nbt;
	}
	
	public Requirement setAmount(int amount){
		this.amount = amount;
		return this;
	}
	
	public abstract boolean satisfied(PlayerEntity player);
	
	public abstract void take(PlayerEntity player);
	
	public abstract Identifier type();
	
	public abstract NbtCompound data();
	
	public boolean onClick(ResearchEntry entry, PlayerEntity player){
		return false;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof Requirement))
			return false;
		Requirement that = (Requirement)o;
		return getAmount() == that.getAmount() && type().equals(that.type());
	}
	
	public int hashCode(){
		return Objects.hash(getAmount(), type());
	}

    public static class ORegistry {
		public boolean registerFactory(Identifier id, Function<List<String>, Requirement> factory){
			factories.put(id, factory);
			return true;
		}
		public boolean registerDeserializer(Identifier id, Function<NbtCompound, Requirement> deserializer){
			deserializers.put(id, deserializer);
			return true;
		}
    }
}