package mod.octavo.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mod.octavo.core.Octavo;
import mod.octavo.core.OctavoReference;
import net.minecraft.client.util.math.MatrixStack;
import mod.octavo.api.BackgroundLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageLayer extends BackgroundLayer{
	
	public static final Identifier TYPE = new Identifier(OctavoReference.MODID,"image");
	
	public Identifier image;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public ImageLayer(){}
	
	public ImageLayer(String image){
		this.image = new Identifier(image);
	}
	
	public Identifier type(){
		return TYPE;
	}
	
	public NbtCompound data(){
		NbtCompound data = new NbtCompound();
		data.putString("image", image.toString());
		return data;
	}
	
	public void load(JsonObject data, Identifier file){
		JsonPrimitive imagePrim = data.getAsJsonPrimitive("image");
		if(imagePrim != null)
			if(imagePrim.isString()){
				Identifier base = new Identifier(imagePrim.getAsString());
				image = new Identifier(base.getNamespace(), "textures/" + base.getPath() + ".png");
			}else
				LOGGER.error("Field \"image\" for an image background layer was not a string, in " + file + "!");
		else
			LOGGER.error("Field \"image\" for an image background layer was not defined, in " + file + "!");
	}
	
	public void render(MatrixStack stack, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom){
	
	}
}