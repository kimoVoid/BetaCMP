# Beta CMP mod for b1.8.1
This is a mod that was made for the [Slimeless Tech Hub](https://discord.gg/a3JEeAyZR4). It uses Ornithe b1.8.1 to run and acts similarly to mods such as Carpet.

## Using
You need to grab a copy of the [Ornithe installer](https://ornithemc.net/) and follow the steps below.
1. Head to the `Server` tab and enable `Show snapshots`.
2. Wait for it to load, then find `b1.8.1`.
3. Leave everything else as default, you can change the directory of the server files if you want.
4. Click the `Install` button.
5. Run the server with `fabric-server-launch.jar` at least once.
6. Place the Beta CMP mod onto the `mods` folder.
7. You're done! :)

## Commands
| Command                               | Description                                         |
|---------------------------------------|-----------------------------------------------------|
| `clear`                               | Clears your inventory                               |
| `give <item name/id> <amount> [meta]` | Enhanced give command                               |
| `ping [player]`                       | View ping (half works due to b1.8.1 pinging slowly) |
| `player <name> <action>`              | Fake player command, similar to Carpet mod's        |
| `rule [args...]`                      | View and set rules (more info below)                |
| `setblock <x> <y> <z> <block> [meta]` | Set block backport                                  |
| `summon <entity> [x] [y] [z]`         | Summon backport                                     |
| `toggledownfall`                      | Toggle weather                                      |

## Rules
You can set rules with the `/rule` command, which will print all available subcommands.
<br>The easiest way to change a rule is `/rule <rule> <value>`.
<br>If you want to change it permanently, use `/rule setDefault <rule> <value>`.

`disableRailTick`
* Disables detector rail random ticking
* Type: `boolean`
* Default: `false`

`lazyRails`
* Makes all detector rails powered by default
* Acts similarly to redstone blocks
* Type: `boolean`
* Default: `false`

## Credits
This mod is based on [Tapestry](https://github.com/Nullspace-MC/Tapestry), which is a similar mod developed for release 1.7.2.
