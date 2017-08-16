package com.latmod.yabba.block;

import com.feed_the_beast.ftbl.lib.block.EnumRotation;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.latmod.yabba.YabbaCommon;
import com.latmod.yabba.item.IUpgrade;
import com.latmod.yabba.item.ItemBlockBarrel;
import com.latmod.yabba.item.YabbaItems;
import com.latmod.yabba.tile.TileBarrelBase;
import com.latmod.yabba.tile.TileItemBarrel;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class BlockStorageBarrelBase extends BlockBarrelBase
{
	public static final Map<UUID, Long> LAST_CLICK_MAP = new HashMap<>();
	public static final PropertyEnum<EnumRotation> ROTATION = PropertyEnum.create("rotation", EnumRotation.class);
	public static final IUnlistedProperty<ResourceLocation> MODEL = new IUnlistedProperty<ResourceLocation>()
	{
		@Override
		public String getName()
		{
			return "model";
		}

		@Override
		public boolean isValid(ResourceLocation value)
		{
			return true;
		}

		@Override
		public Class<ResourceLocation> getType()
		{
			return ResourceLocation.class;
		}

		@Override
		public String valueToString(ResourceLocation value)
		{
			return value.toString();
		}
	};

	public static final IUnlistedProperty<IBlockState> SKIN = new IUnlistedProperty<IBlockState>()
	{
		@Override
		public String getName()
		{
			return "skin";
		}

		@Override
		public boolean isValid(IBlockState value)
		{
			return true;
		}

		@Override
		public Class<IBlockState> getType()
		{
			return IBlockState.class;
		}

		@Override
		public String valueToString(IBlockState value)
		{
			return CommonUtils.getNameFromState(value);
		}
	};

	public BlockStorageBarrelBase(String id)
	{
		super(id, Material.WOOD, MapColor.WOOD);
		setDefaultState(blockState.getBaseState().withProperty(ROTATION, EnumRotation.NORMAL).withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
		setHardness(2F);
	}

	public static EnumFacing normalizeFacing(IBlockState state)
	{
		EnumRotation rotation = state.getValue(ROTATION);

		if (rotation == EnumRotation.FACING_DOWN)
		{
			return EnumFacing.DOWN;
		}
		else if (rotation == EnumRotation.FACING_UP)
		{
			return EnumFacing.UP;
		}

		return state.getValue(BlockHorizontal.FACING);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		list.add(createStack(YabbaCommon.DEFAULT_MODEL_ID, YabbaCommon.DEFAULT_SKIN_ID, Tier.WOOD));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[] {BlockHorizontal.FACING, ROTATION}, new IUnlistedProperty[] {MODEL, SKIN});
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta)).withProperty(ROTATION, EnumRotation.VALUES[(meta >> 2) & 3]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(ROTATION).ordinal() << 2) | state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@Override
	@Deprecated
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntity tile = worldIn.getTileEntity(pos);

		if (tile instanceof TileItemBarrel)
		{
			return ((TileItemBarrel) tile).createState(state);
		}

		return state;
	}

	@Override
	@Deprecated
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(BlockHorizontal.FACING, rot.rotate(state.getValue(BlockHorizontal.FACING)));
	}

	@Override
	@Deprecated
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation(state.getValue(BlockHorizontal.FACING)));
	}

	@Override
	public void dropItem(ItemStack itemStack, @Nullable TileEntity tile)
	{
	}

	@Override
	public void placeFromItem(ItemStack stack, @Nullable TileEntity tile)
	{
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		//IBlockState parent = state.getValue(SKIN).getParentState();
		//return parent.getBlock().canRenderInLayer(parent, layer);
		return layer == BlockRenderLayer.CUTOUT;
	}

	public ItemStack createStack(ResourceLocation model, IBlockState skin, Tier tier)
	{
		TileBarrelBase tile = new TileBarrelBase();
		tile.setModel(model);
		tile.setSkin(skin);
		tile.setTier(tier);
		return tile.createStack(this);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileBarrelBase)
		{
			return ((TileBarrelBase) tileEntity).createStack(this);
		}

		return super.getItem(worldIn, pos, state);
	}

	@Override
	@Deprecated
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(ROTATION, EnumRotation.getRotationFromEntity(pos, placer)).withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
	{
		if (worldIn.isRemote || playerIn.capabilities.isCreativeMode)
		{
			return;
		}

		Long l = LAST_CLICK_MAP.get(playerIn.getGameProfile().getId());
		long time = worldIn.getTotalWorldTime();

		if (l != null && (time - l) < 3)
		{
			return;
		}

		LAST_CLICK_MAP.put(playerIn.getGameProfile().getId(), time);

		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileItemBarrel)
		{
			((TileItemBarrel) tileEntity).onLeftClick(playerIn);
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (side != normalizeFacing(state) && (heldItem.isEmpty() || heldItem.getItem() instanceof ItemBlockBarrel))
		{
			return false;
		}

		if (!worldIn.isRemote)
		{
			TileEntity tile = worldIn.getTileEntity(pos);

			if (tile instanceof TileItemBarrel)
			{
				Long l = LAST_CLICK_MAP.get(playerIn.getGameProfile().getId());
				long time = worldIn.getTotalWorldTime();
				((TileItemBarrel) tile).onRightClick(playerIn, state, hand, hitX, hitY, hitZ, side, l == null ? Long.MAX_VALUE : (time - l));

				if (heldItem.isEmpty() || !(heldItem.getItem() instanceof IUpgrade))
				{
					LAST_CLICK_MAP.put(playerIn.getGameProfile().getId(), time);
				}
			}
		}

		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
	{
		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileItemBarrel && ((TileItemBarrel) tile).canConnectRedstone(side);
	}

	@Override
	@Deprecated
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = blockAccess.getTileEntity(pos);
		return tile instanceof TileItemBarrel ? ((TileItemBarrel) tile).redstoneOutput(side) : 0;
	}

	@Override
	@Deprecated
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = blockAccess.getTileEntity(pos);
		return tile instanceof TileItemBarrel ? ((TileItemBarrel) tile).redstoneOutput(side) : 0;
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileBarrelBase)
		{
			return YabbaCommon.getModelData(((TileBarrelBase) tile).model).getAABB(state);
		}

		return FULL_BLOCK_AABB;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileBarrelBase && ((TileBarrelBase) tile).hasUpgrade(YabbaItems.UPGRADE_OBSIDIAN_SHELL))
		{
			return Float.MAX_VALUE;
		}

		return 8F;
	}
}