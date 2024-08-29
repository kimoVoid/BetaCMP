package me.kimovoid.betacmp.command;

import com.google.common.collect.Lists;
import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import me.kimovoid.betacmp.helpers.ServerWorldHelper;
import me.kimovoid.betacmp.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.source.CommandSource;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBox;

import java.util.*;

public class CloneCommand extends Command {

    @Override
    public String getName() {
        return "clone";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/clone <x1> <y1> <z1> <x2> <y2> <z2> <x> <y> <z> [maskMode] [cloneMode]";
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (BetaCMP.SERVER.playerManager.get(source.getSourceName()) == null)
            throw new IncorrectUsageException(getUsage(source), "Sender must be a player.");

        if (args.length < 9)
            throw new IncorrectUsageException(getUsage(source));

        ServerPlayerEntity p = BetaCMP.SERVER.playerManager.get(source.getSourceName());
        BlockPos srcPos1 = parseBlockPos(p, args, 0);
        BlockPos srcPos2 = parseBlockPos(p, args, 3);
        BlockPos destPos = parseBlockPos(p, args, 6);
        StructureBox sourceBox = new StructureBox(Math.min(srcPos1.x, srcPos2.x), Math.min(srcPos1.y, srcPos2.y), Math.min(srcPos1.z, srcPos2.z), Math.max(srcPos1.x, srcPos2.x), Math.max(srcPos1.y, srcPos2.y), Math.max(srcPos1.z, srcPos2.z));
        StructureBox destBox = new StructureBox(destPos.x, destPos.y, destPos.z, destPos.x + sourceBox.getSpanX() - 1, destPos.y + sourceBox.getSpanY() - 1, destPos.z + sourceBox.getSpanZ() - 1);
        int volume = sourceBox.getSpanX() * sourceBox.getSpanY() * sourceBox.getSpanZ();
        if (volume > Settings.fillLimit) {
            throw new CommandException(String.format("Too many blocks in the specified area (%d > %d)", volume, Settings.fillLimit));
        } else {
            boolean bl = false;
            int filterBlock = -1;
            int filterMeta = -1;
            if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && sourceBox.intersects(destBox)) {
                throw new CommandException("Source and destination can not overlap");
            } else {
                if (args.length >= 11 && args[10].equals("move")) {
                    bl = true;
                }

                if (sourceBox.minY >= 0 && sourceBox.maxY < 256 && destBox.minY >= 0 && destBox.maxY < 256) {
                    World world = p.world;
                    if (world.isAreaLoaded(sourceBox.minX, sourceBox.minY, sourceBox.minZ, sourceBox.maxX, sourceBox.maxY, sourceBox.maxZ) && world.isAreaLoaded(destBox.minX, destBox.minY, destBox.minZ, destBox.maxX, destBox.maxY, destBox.maxZ)) {
                        boolean bl2 = false;
                        if (args.length >= 10) {
                            if (args[9].equals("masked")) {
                                bl2 = true;
                            } else if (args[9].equals("filtered")) {
                                if (args.length < 12) {
                                    throw new IncorrectUsageException(getUsage(source));
                                }

                                filterBlock = nameToBlockId(args[11]);
                                if (args.length >= 13) {
                                    filterMeta = parseInt(args[12], 0, 15);
                                }
                            }
                        }

                        List<BlockInfo> list1 = new ArrayList<>();
                        List<BlockInfo> list2 = new ArrayList<>();
                        List<BlockInfo> list3 = new ArrayList<>();
                        LinkedList<BlockPos> linkedList = Lists.newLinkedList();
                        BlockPos translate = new BlockPos(destBox.minX - sourceBox.minX, destBox.minY - sourceBox.minY, destBox.minZ - sourceBox.minZ);

                        for (int z = sourceBox.minZ; z <= sourceBox.maxZ; ++z) {
                            for (int y = sourceBox.minY; y <= sourceBox.maxY; ++y) {
                                for (int x = sourceBox.minX; x <= sourceBox.maxX; ++x) {
                                    BlockPos src = new BlockPos(x, y, z);
                                    BlockPos dest = new BlockPos(x + translate.x, y + translate.y, z + translate.z);
                                    int block = world.getBlock(x, y, z);
                                    int meta = world.getBlockMetadata(x, y, z);
                                    if ((!bl2 || block != 0) && (filterBlock == -1 || block == filterBlock && (filterMeta < 0 || meta == filterMeta))) {
                                        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
                                        Block b = Block.BY_ID[block];
                                        if (blockEntity != null) {
                                            NbtCompound compoundTag = new NbtCompound();
                                            blockEntity.writeNbt(compoundTag);
                                            list2.add(new BlockInfo(dest, b, meta, compoundTag));
                                        } else if (!b.isOpaqueCube() && !b.isFullCube()) {
                                            list3.add(new BlockInfo(dest, b, meta, null));
                                            linkedList.addFirst(src);
                                        } else {
                                            list1.add(new BlockInfo(dest, b, meta, null));
                                            linkedList.addLast(src);
                                        }
                                    }
                                }
                            }
                        }

                        if (bl) {
                            Iterator<BlockPos> iter;
                            BlockPos blockPos;
                            for (iter = linkedList.iterator(); iter.hasNext(); ) {
                                blockPos = (BlockPos) iter.next();
                                BlockEntity blockEntity = world.getBlockEntity(blockPos.x, blockPos.y, blockPos.z);
                                if (blockEntity instanceof Inventory) {
                                    for (int i = 0; i < ((Inventory)blockEntity).getSize(); i++) {
                                        ((Inventory)blockEntity).setStack(i, null);
                                    }
                                }
                                world.setBlockWithMetadata(blockPos.x, blockPos.y, blockPos.z, 0, 0);
                            }
                        }

                        List<BlockInfo> list4 = new ArrayList<>();
                        list4.addAll(list1);
                        list4.addAll(list2);
                        list4.addAll(list3);
                        List<BlockInfo> list5 = Lists.reverse(list4);

                        Iterator<BlockInfo> iter;
                        BlockInfo info;
                        BlockEntity blockEntity;
                        for (iter = list5.iterator(); iter.hasNext(); ) {
                            info = iter.next();
                            blockEntity = world.getBlockEntity(info.pos.x, info.pos.y, info.pos.z);
                            if (blockEntity instanceof Inventory) {
                                for (int i = 0; i < ((Inventory)blockEntity).getSize(); i++) {
                                    ((Inventory)blockEntity).setStack(i, null);
                                }
                            }
                            world.setBlockWithMetadata(info.pos.x, info.pos.y, info.pos.z, 0, 0);
                        }

                        volume = 0;
                        iter = list4.iterator();

                        while (iter.hasNext()) {
                            info = iter.next();
                            if (world.setBlockWithMetadata(info.pos.x, info.pos.y, info.pos.z, info.block.id, info.metadata)) {
                                ++volume;
                            }
                        }

                        for (iter = list2.iterator(); iter.hasNext(); ) {
                            info = iter.next();
                            blockEntity = world.getBlockEntity(info.pos.x, info.pos.y, info.pos.z);
                            if (blockEntity != null) {
                                info.nbt.putInt("x", info.pos.x);
                                info.nbt.putInt("y", info.pos.y);
                                info.nbt.putInt("z", info.pos.z);
                                blockEntity.readNbt(info.nbt);
                                blockEntity.markDirty();
                            }
                            world.setBlockWithMetadata(info.pos.x, info.pos.y, info.pos.z, info.block.id, info.metadata);
                        }

                        if (Settings.fillUpdates) {
                            iter = list5.iterator();

                            while (iter.hasNext()) {
                                info = iter.next();
                                world.updateNeighbors(info.pos.x, info.pos.y, info.pos.z, info.block.id);
                            }

                            if (world instanceof ServerWorldHelper) {
                                List<ScheduledTick> scheduledTicks = ((ServerWorldHelper) world).collectScheduledTicks(sourceBox);

                                if (scheduledTicks != null) {

                                    for (ScheduledTick tick : scheduledTicks) {
                                        if (sourceBox.contains(tick.x, tick.y, tick.z)) {
                                            world.scheduleTick(tick.x + translate.x, tick.y + translate.y, tick.z + translate.x, tick.blockId, (int) (tick.time - world.getData().getTime()));
                                        }
                                    }
                                }
                            }
                        }

                        if (volume <= 0) {
                            throw new CommandException("No blocks cloned");
                        } else {
                            sendSuccess(source.getSourceName(), String.format("%d blocks cloned", volume));
                        }
                    } else {
                        throw new CommandException("Cannot access blocks outside of the world");
                    }
                } else {
                    throw new CommandException("Cannot access blocks outside of the world");
                }
            }
        }
    }

    static class BlockInfo {
        public final BlockPos pos;
        public final Block block;
        public final int metadata;
        public final NbtCompound nbt;

        public BlockInfo(BlockPos p, Block b, int m, NbtCompound n) {
            this.pos = p;
            this.block = b;
            this.metadata = m;
            this.nbt = n;
        }
    }
}
