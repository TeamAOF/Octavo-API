package mod.octavo.api;

import com.google.gson.JsonObject;
import net.minecraft.client.util.math.MatrixStack;
import mod.octavo.impl.ImageLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BackgroundLayer{
	
	////////// static stuff
	
	private static Map<Identifier, Supplier<BackgroundLayer>> factories = new LinkedHashMap<>();
	private static Map<Identifier, Function<NbtCompound, BackgroundLayer>> deserializers = new LinkedHashMap<>();
	
	public static BackgroundLayer makeLayer(Identifier type, JsonObject content, Identifier file, float speed, float vanishZoom){
		if(getBlank(type) != null){
			BackgroundLayer layer = getBlank(type).get();
			layer.setSpeed(speed);
			layer.setVanishZoom(vanishZoom);
			layer.load(content, file);
			return layer;
		}else
			return null;
	}
	
	public static BackgroundLayer deserialize(NbtCompound passData){
		Identifier type = new Identifier(passData.getString("type"));
		NbtCompound data = passData.getCompound("data");
		float speed = passData.getFloat("speed");
		float vanishZoom = passData.getFloat("vanishZoom");
		if(deserializers.get(type) != null){
			BackgroundLayer layer = deserializers.get(type).apply(data);
			layer.setSpeed(speed).setVanishZoom(vanishZoom);
			return layer;
		}
		return null;
	}
	
	public static Supplier<BackgroundLayer> getBlank(Identifier type){
		return factories.get(type);
	}
	
	public static void init(){
		factories.put(ImageLayer.TYPE, ImageLayer::new);
		deserializers.put(ImageLayer.TYPE, nbt -> new ImageLayer(nbt.getString("image")));
	}
	
	///////// instance stuff
	
	protected float speed = 0.5f, vanishZoom = -1;
	
	public float speed(){
		return speed;
	}
	
	public float vanishZoom(){
		return vanishZoom;
	}
	
	public BackgroundLayer setSpeed(float speed){
		this.speed = speed;
		return this;
	}
	
	public BackgroundLayer setVanishZoom(float vanishZoom){
		this.vanishZoom = vanishZoom;
		return this;
	}
	
	public NbtCompound getPassData(){
		NbtCompound nbt = new NbtCompound();
		nbt.putString("type", type().toString());
		nbt.put("data", data());
		nbt.putFloat("speed", speed());
		nbt.putFloat("vanishZoom", vanishZoom());
		return nbt;
	}
	
	public abstract Identifier type();
	
	public abstract NbtCompound data();
	
	public abstract void load(JsonObject data, Identifier file);
	
	public abstract void render(MatrixStack stack, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom);
}