package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Clean_Snow_Brush
 *
 * @author psanker
 */
public class CleanSnowBrush extends Brush
{
    private static int timesUsed = 0;
    private double trueCircle = 0;

    /**
     *
     */
    public CleanSnowBrush()
    {
        this.setName("Clean Snow");
    }

    private void cleanSnow(final SnipeData v)
    {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        final Undo undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int y = (brushSize + 1) * 2; y >= 0; y--)
        {
            final double ySquared = Math.pow(y - brushSize, 2);

            for (int x = (brushSize + 1) * 2; x >= 0; x--)
            {
                final double xSquared = Math.pow(x - brushSize, 2);

                for (int z = (brushSize + 1) * 2; z >= 0; z--)
                {
                    if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared)
                    {
                        if ((this.clampY(this.getBlockPositionX() + x - brushSize, this.getBlockPositionY() + z - brushSize, this.getBlockPositionZ() + y - brushSize).getType() == Material.SNOW) && ((this.clampY(this.getBlockPositionX() + x - brushSize, this.getBlockPositionY() + z - brushSize - 1, this.getBlockPositionZ() + y - brushSize).getType() == Material.SNOW) || (this.clampY(this.getBlockPositionX() + x - brushSize, this.getBlockPositionY() + z - brushSize - 1, this.getBlockPositionZ() + y - brushSize).getType() == Material.AIR)))
                        {
                            undo.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + z, this.getBlockPositionZ() + y));
                            this.setBlockIdAt(this.getBlockPositionZ() + y - brushSize, this.getBlockPositionX() + x - brushSize, this.getBlockPositionY() + z - brushSize, 0);
                        }

                    }
                }
            }
        }

        v.storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.cleanSnow(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.cleanSnow(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 1; i < par.length; i++)
        {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
                return;
            }
            else if (parameter.startsWith("true"))
            {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
            }
            else if (parameter.startsWith("false"))
            {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
            }
            else
            {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return CleanSnowBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        CleanSnowBrush.timesUsed = tUsed;
    }
}
