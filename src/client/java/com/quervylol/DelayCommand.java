package com.quervylol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DelayCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("delay")
                .then(ClientCommandManager.argument("ticks", IntegerArgumentType.integer(1, 1000))
                        .executes(context -> {
                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                            AutoFishCommand.REEL_DELAY_TICKS = ticks;
                            context.getSource().sendFeedback(Text.literal("Cast delay set to " + ticks + " ticks.").formatted(Formatting.GREEN));
                            return 1;
                        }))
                .executes(context -> {
                    context.getSource().sendError(Text.literal("Usage: /delay <ticks>"));
                    return 0;
                })
        );
    }
}
