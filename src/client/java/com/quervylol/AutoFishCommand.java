package com.quervylol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.screen.ChatScreen;

public class AutoFishCommand {
    private static boolean isAutoFishing = false;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean hasCastRod = false;
    private static boolean lastCastAutomated = false;
    private static int fishingTicks;
    private static final int CAST_WAIT_TICKS = 100;
    public static int REEL_DELAY_TICKS = 20;
    private static int postReelDelayTicks = 0;
    private static boolean stopOnInventoryOpen = false;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("autofish")
                .then(ClientCommandManager.argument("state", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enable = BoolArgumentType.getBool(context, "state");

                            if (client.player == null) {
                                context.getSource().sendError(Text.literal("Player not found."));
                                return 0;
                            }

                            if (enable && client.player.getMainHandStack().getItem() != Items.FISHING_ROD) {
                                context.getSource().sendError(Text.literal("You must hold a fishing rod to use auto fishing."));
                                return 0;
                            }

                            if (enable) {
                                if (isAutoFishing) {
                                    context.getSource().sendError(Text.literal("Auto fishing is already enabled."));
                                    return 0;
                                }
                                startAutoFishing();
                                context.getSource().sendFeedback(Text.literal("Auto fishing enabled.").formatted(Formatting.GREEN));
                            } else {
                                if (!isAutoFishing) {
                                    context.getSource().sendError(Text.literal("Auto fishing is already disabled."));
                                    return 0;
                                }
                                stopAutoFishing();
                                context.getSource().sendFeedback(Text.literal("Auto fishing disabled.").formatted(Formatting.RED));
                            }

                            return 1;
                        }))
                .executes(context -> {
                    context.getSource().sendError(Text.literal("Usage: /autofish <true/false>"));
                    return 0;
                })
        );
    }

    private static void startAutoFishing() {
        fishingTicks = 0;
        postReelDelayTicks = 0;
        isAutoFishing = true;
        hasCastRod = false;
        lastCastAutomated = false;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isAutoFishing && client.player != null) {
                if (stopOnInventoryOpen && client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) {
                    stopAutoFishing();
                    client.player.sendMessage(Text.literal("Auto fishing disabled! Opened GUI!").formatted(Formatting.RED));
                    return;
                }

                if (client.player.getMainHandStack().getItem() != Items.FISHING_ROD) {
                    stopAutoFishing();
                    client.player.sendMessage(Text.literal("Auto fishing disabled! Swapped item!").formatted(Formatting.RED));
                    return;
                }

                if (postReelDelayTicks > 0) {
                    postReelDelayTicks--;
                    if (postReelDelayTicks == 0) {
                        hasCastRod = false;
                    }
                } else {
                    fishingTicks++;

                    if (!hasCastRod) {
                        castRod();
                        hasCastRod = true;
                        lastCastAutomated = true;
                    }

                    if (fishingTicks >= CAST_WAIT_TICKS) {
                        if (isFishBiting()) {
                            reelIn();
                            fishingTicks = 0;
                            postReelDelayTicks = REEL_DELAY_TICKS;
                        }
                    }
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.mouse.wasRightButtonClicked() && !lastCastAutomated) {
                if (isAutoFishing) {
                    stopAutoFishing();
                    client.player.sendMessage(Text.literal("Auto fishing disabled! Reeled in manually!").formatted(Formatting.RED));
                }
            }
            lastCastAutomated = false;
        });
    }

    private static void stopAutoFishing() {
        isAutoFishing = false;
        postReelDelayTicks = 0;
    }

    private static void castRod() {
        if (client.player != null && client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
            lastCastAutomated = true;
        }
    }

    private static boolean isFishBiting() {
        if (client.player != null && client.player.fishHook != null) {
            Vec3d velocity = client.player.fishHook.getVelocity();
            if (velocity.y < -0.05 && fishingTicks > CAST_WAIT_TICKS / 2) {
                return true;
            }
        }
        return false;
    }

    private static void reelIn() {
        if (client.player != null) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
            client.player.playSound(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0F, 1.0F);
        }
    }

    public static void setStopOnInventoryOpen(boolean value) {
        stopOnInventoryOpen = value;
    }
}
