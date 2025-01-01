package me.cocopopeater.events;

import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LeftClickBlockEvent {
    public static void register(){
        AttackBlockCallback.EVENT.register(LeftClickBlockEvent::run);
    }

    public static ActionResult run(PlayerEntity playerEntity, World world, Hand hand, BlockPos blockPos, Direction direction){
        if(!ConfigHandler.getInstance().isEnabled()) return ActionResult.PASS;
        ActionResult result = ActionResult.PASS;

        String itemName = playerEntity.getMainHandStack().getItem().getName().getString();

        if(!(PlayerVariableManager.getTool(itemName) == null)){
            // The user has bound a tool to the currently held item
            Tool tool = PlayerVariableManager.getTool(itemName);
            tool.resetTool(true);
            return ActionResult.FAIL;
        }

        if(playerEntity.getMainHandStack().getItem() == Items.WOODEN_AXE){
            PlayerVariableManager.setPos1(blockPos);
            result = ActionResult.FAIL;
        }
        return result;
    }
}
