package com.latmod.yabba;

import com.feed_the_beast.ftblib.lib.util.BlockUtils;
import com.latmod.yabba.block.BlockAntibarrel;
import com.latmod.yabba.block.BlockDecorativeBlock;
import com.latmod.yabba.block.BlockItemBarrel;
import com.latmod.yabba.block.BlockItemBarrelConnector;
import com.latmod.yabba.block.Tier;
import com.latmod.yabba.item.ItemBlockAntibarrel;
import com.latmod.yabba.item.ItemHammer;
import com.latmod.yabba.item.ItemPainter;
import com.latmod.yabba.item.ItemWrench;
import com.latmod.yabba.item.upgrade.ItemUpgrade;
import com.latmod.yabba.item.upgrade.ItemUpgradeCreative;
import com.latmod.yabba.item.upgrade.ItemUpgradeHopper;
import com.latmod.yabba.item.upgrade.ItemUpgradeRedstone;
import com.latmod.yabba.item.upgrade.ItemUpgradeSmelting;
import com.latmod.yabba.item.upgrade.ItemUpgradeTier;
import com.latmod.yabba.tile.BarrelNetwork;
import com.latmod.yabba.tile.ItemBarrel;
import com.latmod.yabba.tile.TileAntibarrel;
import com.latmod.yabba.tile.TileCompoundItemBarrel;
import com.latmod.yabba.tile.TileDecorativeBlock;
import com.latmod.yabba.tile.TileItemBarrel;
import com.latmod.yabba.tile.TileItemBarrelConnector;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Yabba.MOD_ID)
public class YabbaEventHandler
{
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(Yabba.MOD_ID, "barrel_network");

	private static Block withName(Block block, String name)
	{
		block.setCreativeTab(Yabba.TAB);
		block.setRegistryName(name);
		block.setTranslationKey(Yabba.MOD_ID + "." + name);
		return block;
	}

	private static Item withName(Item item, String name)
	{
		item.setCreativeTab(Yabba.TAB);
		item.setRegistryName(name);
		item.setTranslationKey(Yabba.MOD_ID + "." + name);
		return item;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
				withName(new BlockItemBarrel(), "item_barrel"),
				withName(new BlockItemBarrelConnector(), "item_barrel_connector"),
				withName(new BlockAntibarrel(), "antibarrel"),
				withName(new BlockDecorativeBlock(), "decorative_block")
		);

		GameRegistry.registerTileEntity(TileItemBarrel.class, new ResourceLocation(Yabba.MOD_ID, "item_barrel"));
		GameRegistry.registerTileEntity(TileItemBarrelConnector.class, new ResourceLocation(Yabba.MOD_ID, "item_barrel_connector"));
		GameRegistry.registerTileEntity(TileAntibarrel.class, new ResourceLocation(Yabba.MOD_ID, "antibarrel"));
		GameRegistry.registerTileEntity(TileCompoundItemBarrel.class, new ResourceLocation(Yabba.MOD_ID, "compound_item_barrel"));
		GameRegistry.registerTileEntity(TileDecorativeBlock.class, new ResourceLocation(Yabba.MOD_ID, "decorative_block"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new ItemBlock(YabbaBlocks.ITEM_BARREL).setRegistryName("item_barrel"),
				new ItemBlock(YabbaBlocks.ITEM_BARREL_CONNECTOR).setRegistryName("item_barrel_connector"),
				new ItemBlockAntibarrel(YabbaBlocks.ANTIBARREL).setRegistryName("antibarrel"),
				new ItemBlock(YabbaBlocks.DECORATIVE_BLOCK).setRegistryName("decorative_block")
		);

		event.getRegistry().registerAll(
				withName(new Item(), "upgrade_blank"),
				withName(new ItemUpgradeTier(Tier.IRON), "upgrade_iron_tier"),
				withName(new ItemUpgradeTier(Tier.GOLD), "upgrade_gold_tier"),
				withName(new ItemUpgradeTier(Tier.DIAMOND), "upgrade_diamond_tier"),
				withName(new ItemUpgradeTier(Tier.STAR), "upgrade_star_tier"),
				withName(new ItemUpgradeCreative(), "upgrade_creative"),
				withName(new ItemUpgrade(), "upgrade_obsidian_shell"),
				withName(new ItemUpgradeRedstone(), "upgrade_redstone_out"),
				withName(new ItemUpgradeHopper(), "upgrade_hopper"),
				withName(new ItemUpgrade(), "upgrade_void"),
				withName(new ItemUpgrade(), "upgrade_pickup"),
				withName(new ItemUpgradeSmelting(), "upgrade_smelting"),
				withName(new ItemHammer(), "hammer"),
				withName(new ItemPainter(), "painter"),
				withName(new ItemWrench(), "wrench")
		);
	}

	@SubscribeEvent
	public static void attachWorldCap(AttachCapabilitiesEvent<World> event)
	{
		event.addCapability(WORLD_CAP_ID, new BarrelNetwork(event.getObject()));
	}

	@SubscribeEvent
	public static void tickServerWorld(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			BarrelNetwork network = BarrelNetwork.get(event.world);

			if (network != null)
			{
				network.tick();
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event)
	{
		boolean modified = false;
		EntityPlayer player = event.getEntityPlayer();
		EntityItem entityItem = event.getItem();
		ItemStack itemStack = entityItem.getItem();

		int size = player.inventory.getSizeInventory();

		for (int i = 0; i < size; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack.getCount() == 1 && BlockUtils.hasData(stack))
			{
				Item stackItem = stack.getItem();

				if (stackItem == YabbaItems.ITEM_BARREL)
				{
					TileItemBarrel barrel = new TileItemBarrel();
					barrel.readFromNBT(BlockUtils.getData(stack));

					if (!barrel.barrel.content.isEmpty() && barrel.barrel.hasUpgrade(YabbaItems.UPGRADE_PICKUP))
					{
						int originalSize = itemStack.getCount();
						itemStack = ((ItemBarrel) barrel.barrel.content).insertItem(0, itemStack, false);

						if (originalSize != itemStack.getCount())
						{
							entityItem.setItem(itemStack);
							barrel.writeToItem(stack);
							modified = true;
						}

						if (itemStack.isEmpty())
						{
							entityItem.setDead();
							net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, entityItem, itemStack);

							if (!entityItem.isSilent())
							{
								entityItem.world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityItem.world.rand.nextFloat() - entityItem.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
							}

							player.onItemPickup(entityItem, originalSize);
							break;
						}
					}
				}
			}
		}

		if (modified)
		{
			event.setCanceled(true);
		}
	}
}