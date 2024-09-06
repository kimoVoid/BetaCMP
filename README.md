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
| Command          | Description                                         |
|------------------|-----------------------------------------------------|
| `clear`          | Clears your inventory                               |
| `clone`          | Clone backport                                      |
| `give`           | Enhanced give command                               |
| `ping`           | View ping (half works due to b1.8.1 pinging slowly) |
| `player`         | Fake player command, similar to Carpet mod's        |
| `rule`           | View and set rules (more info below)                |
| `setblock`       | Set block backport                                  |
| `summon`         | Summon backport                                     |
| `toggledownfall` | Toggle weather                                      |

## Rules
You can set rules with the `/rule` command, which will print all available subcommands.
<br>The easiest way to change a rule is `/rule <rule> <value>`.
<br>If you want to change it permanently, use `/rule setDefault <rule> <value>`.

`fillLimit`
* Volume limit of the fill/clone commands
* Type: `int`
* Default: `32768`

`fillUpdates`
* Determines whether clone sends block updates
* Type: `boolean`
* Default: `true`

`disableRedstoneRandomTick`
* Disables the random ticking of redstone torches
* Type: `boolean`
* Default: `false`

`disableRailRandomTick`
* Disables the random ticking of detector rails.
* Type: `boolean`
* Default: `false`

`railActivatedOnPlaced`
* Makes detector rails activated by default when placed by a player.
* This should be used together with `disableRailRandomTick` so that detector rails cannot deactivate randomly. 
* Type: `boolean`
* Default: `false`

`redstoneInstantTick`
* Enables instant execution of scheduled ticks for repeaters and redstone torches
* Type: `boolean`
* Default: `false`

`liquidInstantTick`
* Enables instant execution of ticks scheduled by liquid blocks.
* Type: `boolean`
* Default: `false`

## Credits
This mod is based on [Tapestry](https://github.com/Nullspace-MC/Tapestry), which is a similar mod developed for release 1.7.2.
