package ml.porez.datapackify;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.Map;

public class Datapackify implements ModInitializer {
	public static final String NAMESPACE = "datapackify";

	private static VillagerTradeManager VILLAGER_TRADES = new VillagerTradeManager();
	public static DefaultedRegistry<IOfferFactoryType> TRADE_OFFERS = FabricRegistryBuilder.createDefaulted(IOfferFactoryType.class, new Identifier(NAMESPACE, "villager_trades"), new Identifier(NAMESPACE, "empty")).buildAndRegister();
	@Override
	public void onInitialize() {
		VillagerTradeFactories.register_all();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(VILLAGER_TRADES);
	}
}
