package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.MagicValues;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;

/**
 * @author Voxel
 */
public class pInkComboNoPhys extends vPerformer
{

    private byte d;
    private byte dr;
    private int ir;

    public pInkComboNoPhys()
    {
        name = "Ink-Combo, No Physics";
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        d = v.getData();
        dr = v.getReplaceData();
        ir = v.getReplaceId();
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.replace();
        vm.data();
        vm.replaceData();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (MagicValues.getIdFor(b.getType()) == ir && b.getData() == dr)
        {
            h.put(b);
            b.setBlockData(MagicValues.getBlockDataFor(MagicValues.getIdFor(b.getType()), d), false);
        }
    }

    @Override
    public boolean isUsingReplaceMaterial()
    {
        return true;
    }
}