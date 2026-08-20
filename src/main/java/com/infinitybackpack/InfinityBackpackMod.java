package com.infinitybackpack;

import com.infinitybackpack.command.ModCommands;
import com.infinitybackpack.event.ModEvents;
import com.infinitybackpack.event.ModItemGroups;
import com.infinitybackpack.network.ModNetworking;
import com.infinitybackpack.registry.*;
import net.fabricmc.api.ModInitializer;

public class InfinityBackpackMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstants.LOGGER.info("Initializing Infinity Backpack mod...");

        ModEnchantments.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModMenus.init();
        ModPotions.init();
        ModItems.init();
        ModEvents.init();
        ModCommands.init();
        ModNetworking.init();
        ModItemGroups.init();
    }
}