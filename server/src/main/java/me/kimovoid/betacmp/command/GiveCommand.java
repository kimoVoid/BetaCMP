package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import net.minecraft.block.Block;
import net.minecraft.command.source.CommandSource;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class GiveCommand extends Command {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/give <player> <id> [amount] [data]";
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (args.length < 2 || args.length > 5) {
            throw new IncorrectUsageException(getUsage(source));
        }

        ServerPlayerEntity player = BetaCMP.SERVER.playerManager.get(args[0]);
        if (player != null) {
            try {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    id = nameToItemId(args[1]);
                }

                if (id == -1) {
                    throw new CommandException("Item not found");
                }

                int amount = 1;
                if (args.length > 2) {
                    amount = parseInt(args[2], 1);
                }

                amount = Math.max(1, amount);
                amount = Math.min(64, amount);

                int data = 0;
                if (args.length > 3) {
                    data = parseInt(args[3], 0);
                }

                if (data < 0) {
                    data = 0;
                }

                this.sendSuccess(player.name, String.format("Giving %s %s x %s",
                        player.name, amount, Item.BY_ID[id].getDisplayName() + (data > 0 ? ":" + data : "")));
                dropItem(player, new ItemStack(id, amount, data));
            } catch (NumberFormatException var11) {
                throw new IncorrectUsageException(getUsage(source));
            }
        } else {
            throw new CommandException("Can't find player " + args[0]);
        }
    }

    private void dropItem(ServerPlayerEntity player, ItemStack itemStack) {
        Random random = new Random();
        if (itemStack != null) {
            ItemEntity var3 = new ItemEntity(player.world, player.x, player.y - 0.30000001192092896D + (double) player.getEyeHeight(), player.z, itemStack);
            var3.pickUpDelay = 0;
            float var4 = 0.3F;
            var3.velocityX = -MathHelper.sin(player.yaw / 180.0F * 3.1415927F) * MathHelper.cos(player.pitch / 180.0F * 3.1415927F) * var4;
            var3.velocityZ = MathHelper.cos(player.yaw / 180.0F * 3.1415927F) * MathHelper.cos(player.pitch / 180.0F * 3.1415927F) * var4;
            var3.velocityY = -MathHelper.sin(player.pitch / 180.0F * 3.1415927F) * var4 + 0.1F;
            var4 = 0.02F;
            float var5 = random.nextFloat() * 3.1415927F * 2.0F;
            var4 *= random.nextFloat();
            var3.velocityX += Math.cos(var5) * (double) var4;
            var3.velocityY += (random.nextFloat() - random.nextFloat()) * 0.1F;
            var3.velocityZ += Math.sin(var5) * (double) var4;

            player.world.addEntity(var3);
        }
    }

    public static int nameToItemId(String n) {
        String name = n.replace("_", "");

        /* Attempt to find item name */
        for (int i = 0; i < Item.BY_ID.length; i++) {
            if (Item.BY_ID[i] == null)
                continue;
            String translatedName = Item.BY_ID[i].getDisplayName().replace(" ", ""); // Remove spaces
            if (translatedName.equalsIgnoreCase(name)) {
                return i;
            }
        }

        /* Attempt to find block name */
        for (int i = 0; i < Block.BY_ID.length; i++) {
            if (Block.BY_ID[i] == null)
                continue;
            String translatedName = Block.BY_ID[i].getName().replace(" ", ""); // Remove spaces
            if (translatedName.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
}
