# TerraSkills Powers

NeoForge 1.21.1 gameplay bridge for TerraSkills, TerraFirmaCraft 4.2.10, and
Pufferfish's Skills 0.18.3. It reads unlocked nodes directly from Pufferfish,
so resets and refunds take effect without a second progression store.

## Implemented powers

- **Mine Safety:** `support_assessment_1` through `_5` let a sneaking player
  right-click with a main-hand pickaxe to inspect TFC unsupported,
  collapse-starting blocks. The radius is 0--4 blocks and the cooldown is five
  seconds. This is an inspection only, not a collapse prediction.
- **Careful Mining:** `careful_mining_1` through `_5` reduces *only* TFC's real
  collapse initiation roll for the player who broke the block by 10% per rank.
  The fake-warning roll itself and non-mining paths are untouched; because TFC
  evaluates that roll after a failed real-collapse roll, fake warnings can
  occur more often for trained miners.
- **Contextual speed:** mining (also accepts the legacy `miner` IDs),
  `deep_mining`, `stone_mining` (also accepts `stoneworking`), and
  `excavating` are applied through `BreakSpeed`. Ranks are sent to the client
  every second for responsive prediction while the server continues to read the
  authoritative Pufferfish state.
- **Heights Training:** uses `heights_training_*`, falling back to the
  progress-preserving `mine_safety_*` IDs, to add 0.5 safe blocks per rank.
- **Excavation Reach:** `careful_excavation_1` through `_5` grant +0.5 block
  interaction range per rank while a main-hand pickaxe or shovel is held. The
  transient modifier is removed immediately when the tool or skill no longer
  qualifies, and replaces the old `terra:excavation_reach` KubeJS modifier.
- **Tool Conservation:** `tool_conservation_1` through `_5` has one 3--15%
  roll per TFC pickaxe/shovel mining charge. A successful roll skips the whole
  charge before TFC and Unbreaking handle durability; combat and unrelated
  item-use wear are not intercepted. Brutish Mining doubles this chance.
- **Brutish Mining:** the `B` key toggles this server-authoritative mode for
  players with Mining IV and a Brutish Mining rank. It adds 50--250% mining
  speed and applies 3x--5x pickaxe/shovel mining wear, carrying fractional
  charges per tool. It resets when the player dies or logs out.
- **Careful Eye:** `careful_eye_1` through `_5` make one 0.2--1.0% server-side
  roll per eligible natural TFC raw-rock break. Player-placed raw rock is
  persistently excluded; drops use editable, weighted TFC worldgen-derived
  rock-to-gem associations in the server config. Piston-moved raw stone is
  conservatively excluded at its destination; modded movers should mark their
  destination through the provenance API before allowing discovery rolls.
- **Hazard Management:** the modpack registers shore blocks/items itself (for
  example through KubeJS). This mod recognizes the configured block IDs as
  shores, enforces their configured Hazard Management placement rank, and makes
  the nearest unused shore in range attempt to arrest each TFC propagation
  front. A shore is consumed on either result. The `hazardManagement.shores`
  server-config entries use `block_id=base_chance,required_rank`; defaults are
  `kubejs:copper_braced_shore=0.10,1` and
  `kubejs:black_steel_shore=0.45,4`. With the default +10% per triggering
  miner rank, those give 20--60% and 85--95% at their intended ranks.
- **TFC Astikor handcarts:** when TFC Astikor Carts and Warfactory Ultimate
  Weight are installed, a handcart holds five times the puller's remaining
  Ultimate Weight capacity. It applies TFC's overburdened effect between that
  limit and 120% of it, then pins the puller and reports that the cart is too
  heavy. The calculation includes Ultimate Weight's equipment capacity bonuses
  and nested-container weights.
- **Shared trail activity heatmap:** JourneyMap clients render the persisted,
  dimension-local Trailblazing/Well Trodden activity as the optional
  `TerraSkills: Shared Activity` overlay. It is debug-only and off by default:
  an in-game operator enables it only for their own client with
  `/terraskillspowers debug activity_heatmap enable` and hides it again with
  `/terraskillspowers debug activity_heatmap disable`. Until enabled, the
  client sends no heatmap requests and the server sends no activity data. The
  server clamps the map window radius, rate-limits requests, and returns at
  most the configured number of most-active chunks without loading terrain.

Missing Pufferfish nodes are treated as rank zero, so a configuration reload,
refund, or reset is always safe.

The broader proposed trees remain deliberately unimplemented until their
underlying gameplay systems and integration hooks are designed.

## Build

Run `gradlew.bat build`. The output JAR is placed in `build/libs/`.

`gradlew.bat runClient` includes JourneyMap 1.21.1-6.0.7 for local testing.
JourneyMap stays an optional client integration in published artifacts; it is
not bundled and is never required by a dedicated server.

## Releases

The GitHub Actions workflow builds every branch and pull request and retains a
JAR plus `SHA256SUMS` artifact for 30 days. Pushing an annotated `vX.Y.Z` tag
builds `terraskillspowers-X.Y.Z.jar` and publishes it, with its checksum, as a
GitHub Release.
