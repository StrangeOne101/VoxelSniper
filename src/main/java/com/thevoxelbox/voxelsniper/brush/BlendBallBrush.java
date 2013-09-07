package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendBallBrush extends BlendBrushBase
{
    private static int timesUsed = 0;

    /**
     *
     */
    public BlendBallBrush()
    {
        this.setName("Blend Ball");
    }

    @Override
    protected final void blend(final SnipeData v)
    {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        // Array that holds the original materials plus a buffer
        final int[][][] oldMaterials = new int[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1];
        // Array that holds the blended materials
        final int[][][] newMaterials = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (brushSize + 1); x++)
        {
            for (int y = 0; y <= 2 * (brushSize + 1); y++)
            {
                for (int z = 0; z <= 2 * (brushSize + 1); z++)
                {
                    oldMaterials[x][y][z] = this.getBlockIdAt(this.getBlockPositionX() - brushSize - 1 + x, this.getBlockPositionY() - brushSize - 1 + y, this.getBlockPositionZ() - brushSize - 1 + z);
                }
            }
        }

        // Log current materials into newmats
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                for (int z = 0; z <= brushSizeDoubled; z++)
                {
                    newMaterials[x][y][z] = oldMaterials[x + 1][y + 1][z + 1];
                }
            }
        }

        // Blend materials
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                for (int z = 0; z <= brushSizeDoubled; z++)
                {
                    final int[] materialFrequency = new int[BlendBrushBase.getMaxBlockMaterialID() + 1]; // Array that tracks frequency of materials neighboring given block
                    int modeMatCount = 0;
                    int modeMatId = 0;
                    boolean tiecheck = true;

                    for (int m = -1; m <= 1; m++)
                    {
                        for (int n = -1; n <= 1; n++)
                        {
                            for (int o = -1; o <= 1; o++)
                            {
                                if (!(m == 0 && n == 0 && o == 0))
                                {
                                    materialFrequency[oldMaterials[x + 1 + m][y + 1 + n][z + 1 + o]]++;
                                }
                            }
                        }
                    }

                    // Find most common neighboring material.
                    for (int i = 0; i <= BlendBrushBase.getMaxBlockMaterialID(); i++)
                    {
                        if (materialFrequency[i] > modeMatCount && !(this.excludeAir && i == Material.AIR.getId()) && !(this.excludeWater && (i == Material.WATER.getId() || i == Material.STATIONARY_WATER.getId())))
                        {
                            modeMatCount = materialFrequency[i];
                            modeMatId = i;
                        }
                    }
                    // Make sure there'world not a tie for most common
                    for (int i = 0; i < modeMatId; i++)
                    {
                        if (materialFrequency[i] == modeMatCount && !(this.excludeAir && i == Material.AIR.getId()) && !(this.excludeWater && (i == Material.WATER.getId() || i == Material.STATIONARY_WATER.getId())))
                        {
                            tiecheck = false;
                        }
                    }

                    // Record most common neighbor material for this block
                    if (tiecheck)
                    {
                        newMaterials[x][y][z] = modeMatId;
                    }
                }
            }
        }

        final Undo undo = new Undo(this.getWorld().getName());
        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes  
        for (int x = brushSizeDoubled; x >= 0; x--)
        {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--)
                {
                    if (xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared)
                    {
                        if (!(this.excludeAir && newMaterials[x][y][z] == Material.AIR.getId()) && !(this.excludeWater && (newMaterials[x][y][z] == Material.WATER.getId() || newMaterials[x][y][z] == Material.STATIONARY_WATER.getId())))
                        {
                            if (this.getBlockIdAt(this.getBlockPositionX() - brushSize + x, this.getBlockPositionY() - brushSize + y, this.getBlockPositionZ() - brushSize + z) != newMaterials[x][y][z])
                            {
                                undo.put(this.clampY(this.getBlockPositionX() - brushSize + x, this.getBlockPositionY() - brushSize + y, this.getBlockPositionZ() - brushSize + z));
                            }
                            this.setBlockIdAt(this.getBlockPositionZ() - brushSize + z, this.getBlockPositionX() - brushSize + x, this.getBlockPositionY() - brushSize + y, newMaterials[x][y][z]);
                        }
                    }
                }
            }
        }
        v.storeUndo(undo);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
            return;
        }

        super.parameters(par, v);
    }

    @Override
    public final int getTimesUsed()
    {
        return BlendBallBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        BlendBallBrush.timesUsed = tUsed;
    }
}
