package mod.octavo.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

// Very temp just for testing.
public class OctavoBookItem extends Item {
    Identifier book;

    public OctavoBookItem(Item.Settings settings, Identifier book){
        super(settings);
        this.book = book;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //player.getHeldItem(hand).getOrCreateTag().putBoolean("open", true);

        //ClientUtils.openResearchBookUI(book, null, player.getHeldItem(hand));
        return new TypedActionResult<>(ActionResult.SUCCESS,user.getStackInHand(hand));
    }
}