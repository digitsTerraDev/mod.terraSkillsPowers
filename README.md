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
  Fake warnings, support checks, propagation, explosions, and all non-mining
  causes retain TFC behaviour.
- **Contextual speed:** mining (also accepts the legacy `miner` IDs),
  `deep_mining`, `stone_mining` (also accepts `stoneworking`), and
  `excavating` are applied through `BreakSpeed`. Ranks are sent to the client
  every second for responsive prediction while the server continues to read the
  authoritative Pufferfish state.
- **Heights Training:** uses `heights_training_*`, falling back to the
  progress-preserving `mine_safety_*` IDs, to add 0.5 safe blocks per rank.

The current public TerraSkills tree does not yet define the brief's
`support_assessment_*`, `careful_mining_*`, `stone_mining_*`, or
`excavating_*` nodes. The bridge treats missing nodes as rank zero; add those
definitions to `terraskills:miner` before expecting their effects.

Tool Conservation, Brutish Mining, Careful Eye, Hazard Management, and the
other proposed powers are deliberately not implemented: their required TFC
durability, provenance, support-item, or broader gameplay hooks are not yet
present.

## Build

Run `gradlew.bat build`. The output JAR is placed in `build/libs/`.

## Releases

The GitHub Actions workflow builds every branch and pull request and retains a
JAR plus `SHA256SUMS` artifact for 30 days. Pushing an annotated `vX.Y.Z` tag
builds `terraskillspowers-X.Y.Z.jar` and publishes it, with its checksum, as a
GitHub Release.
