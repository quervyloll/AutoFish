package com.quervylol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class InventoryCheckCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("inventorycheck")
                .then(ClientCommandManager.argument("state", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enable = BoolArgumentType.getBool(context, "state");
                            AutoFishCommand.setStopOnInventoryOpen(enable);

                            if (enable) {
                                context.getSource().sendFeedback(Text.literal("Auto fishing will now disable when inventory is opened.").formatted(Formatting.GREEN));
                            } else {
                                context.getSource().sendFeedback(Text.literal("Auto fishing will not disable when inventory is opened.").formatted(Formatting.RED));
                            }

                            return 1;
                        }))
                .executes(context -> {
                    context.getSource().sendError(Text.literal("Usage: /inventorycheck <true/false>"));
                    return 0;
                })
        );
    }
}