package me.cocopopeater.events;

import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.tools.ToolType;
import me.cocopopeater.tools.impls.TreeTool;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class RightClickBlockEvent {
    public static void register(){
        UseBlockCallback.EVENT.register(RightClickBlockEvent::run);
    }

    public static ActionResult run(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult hitResult){
        if(!ConfigHandler.getInstance().isEnabled()) return ActionResult.PASS;

        String itemName = playerEntity.getMainHandStack().getItem().getName().getString();

        if(!(PlayerVariableManager.getTool(itemName) == null)){
            // The user has bound a tool to the currently held item
            Tool tool = PlayerVariableManager.getTool(itemName);
            tool.applyEffect(hitResult);
            return ActionResult.FAIL;
        }

        if(playerEntity.getMainHandStack().getItem() == Items.WOODEN_AXE){
            PlayerVariableManager.setPos2(hitResult.getBlockPos());
             return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private static void handleEvent(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult hitResult, ToolType tool){

    }
}
