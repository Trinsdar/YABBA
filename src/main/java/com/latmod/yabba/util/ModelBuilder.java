package com.latmod.yabba.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 03.01.2017.
 */
public class ModelBuilder
{
    private static final FaceBakery BAKERY = new FaceBakery();

    public static ModelRotation getRotation(EnumFacing facing)
    {
        switch(facing)
        {
            case SOUTH:
                return ModelRotation.X0_Y180;
            case EAST:
                return ModelRotation.X0_Y90;
            case WEST:
                return ModelRotation.X0_Y270;
            default:
                return ModelRotation.X0_Y0;
        }
    }

    private List<BakedQuad> quads;
    private ModelRotation rotation;

    public ModelBuilder(ModelRotation r)
    {
        quads = new ArrayList<>();
        rotation = r;
    }

    public void setRotation(ModelRotation r)
    {
        rotation = r;
    }

    public List<BakedQuad> getQuads()
    {
        return new ArrayList<>(quads);
    }

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, SpriteSet sprites)
    {
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            addQuad(fromX, fromY, fromZ, toX, toY, toZ, facing, sprites.get(facing));
        }
    }

    public void addQuad(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, EnumFacing face, @Nullable TextureAtlasSprite sprite)
    {
        if(sprite == null)
        {
            return;
        }

        float[] uv = new float[4];
        switch(face)
        {
            case DOWN:
            case UP:
                uv[0] = fromX;
                uv[1] = fromZ;
                uv[2] = toX;
                uv[3] = toZ;
                break;
            case NORTH:
            case SOUTH:
                uv[0] = fromX;
                uv[1] = fromY;
                uv[2] = toX;
                uv[3] = toY;
                break;
            case EAST:
            case WEST:
                uv[0] = fromZ;
                uv[1] = fromY;
                uv[2] = toZ;
                uv[3] = toY;
                break;
        }

        quads.add(BAKERY.makeBakedQuad(new Vector3f(fromX, fromY, fromZ), new Vector3f(toX, toY, toZ), new BlockPartFace(face, -1, "", new BlockFaceUV(uv, 0)), sprite, face, rotation, null, false, true));
    }
}