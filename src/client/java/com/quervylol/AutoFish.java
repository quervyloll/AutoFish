package com.quervylol;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class AutoFish implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			AutoFishCommand.register(dispatcher);
			DelayCommand.register(dispatcher);
			InventoryCheckCommand.register(dispatcher);
		});
	}
}
