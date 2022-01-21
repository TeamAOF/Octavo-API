package mod.octavo.api;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mod.octavo.core.system.Pin;
import mod.octavo.core.system.ResearchEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * An icon associated with a research entry or pin. This can either directly reference an image file, or the texture of an item
 * with set NBT data.
 * <p>
 * An icon is parsed by first checking for an item with an ID that matches the icon. If there is an item, any NBT tags will be
 * parsed as JSON. If there are no items that correlate to that ID, then an image is checked for in <code>&lt;namespace&gt;:textures/</code>.
 *
 * @see ResearchEntry
 * @see Pin
 * @see JsonToNBT
 */
public record Icon(Identifier Identifier, ItemStack stack) {
	
	// Either an item, with optional NBT data, or an direct image reference.
	// Images are assumed to be in <namespace>:textures/.
	// Any resource locations that point to items are assumed to be items; otherwise its assumed to be an image.
	// NBT data can be encoded too. When NBT data is present, an error will be logged if the reference is not an item.
	// NBT data is added in curly braces after the ID, as valid JSON.
	// See JsonToNBT.
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public Icon(Identifier Identifier, @Nullable ItemStack stack){
		this.Identifier = Identifier;
		this.stack = stack;
	}
	
	public Icon(ItemStack stack){
		this.Identifier = stack.getItem().getRegistryName();
		this.stack = stack;
	}
	
	@Nullable
	public ItemStack getStack(){
		return stack;
	}
	
	public Identifier getIdentifier(){
		return Identifier;
	}
	
	public static Icon fromString(String string){
		// Check if theres NBT data.
		NbtCompound tag = null;
		if(string.contains("{")){
			String[] split = string.split("\\{", 2);
			try{
				tag = JsonToNBT.getTagFromJson("{" + split[1]);
				string = split[0];
			}catch(CommandSyntaxException e){
				e.printStackTrace();
				LOGGER.error("Unable to parse JSON: {" + split[1]);
			}
		}
		// Check if there's an item that corresponds to the ID.
		Identifier key = new Identifier(string);
		if(ForgeRegistries.ITEMS.containsKey(key)){
			Item item = ForgeRegistries.ITEMS.getValue(key);
			ItemStack stack = new ItemStack(item);
			// Apply NBT, if any.
			if(tag != null)
				stack.setTag(tag);
			// Return icon.
			return new Icon(key, stack);
		}
		// Otherwise, return the ID as an image.
		// If NBT was encoded, this is probably wrong.
		if(tag != null)
			LOGGER.error("NBT data was encoded for research entry icon " + key + ", but " + key + " is not an item!");
		// Add "textures/" to path.
		key = new Identifier(key.getNamespace(), "textures/" + key.getPath());
		return new Icon(key, null);
	}
	
	public String toString(){
		// If ItemStack is null, just provide the key, but substring'd by 9.
		if(stack == null)
			return new Identifier(Identifier.getNamespace(), Identifier.getPath().substring(9)).toString();
		// If there's no NBT, just send over the item's ID.
		if(!stack.hasTag())
			return Identifier.toString();
		// Otherwise, we need to send over both.
		return Identifier.toString() + nbtToJson(stack.getTag());
	}
	
	private static String nbtToJson(NbtCompound nbt){
		StringBuilder stringbuilder = new StringBuilder("{");
		Collection<String> collection = nbt.keySet();
		
		for(String s : collection){
			if(stringbuilder.length() != 1)
				stringbuilder.append(',');
			stringbuilder.append(handleEscape(s)).append(':').append(nbt.get(s) instanceof StringNBT ? "\"" + nbt.getString(s) + "\"" : nbt.get(s));
		}
		return stringbuilder.append('}').toString();
	}
	
	private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
	
	protected static String handleEscape(String in){
		return SIMPLE_VALUE.matcher(in).matches() ? "\"" + in + "\"" : StringNBT.quoteAndEscape(in);
	}
}