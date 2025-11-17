# Tolerant Luddic Majority

## Description

This mod adds a dynamic **Tolerant Luddic Majority** condition for player colonies.

- Colonies with a majority of Luddic faithful receive **bonuses to stability, rural production, and population growth**.
- Victory over Luddic Church expeditions unlocks **Tech-Mining** and removes **drug dependence** for mining industries.
- Certain conditions, like **pollution**, heavy industry, or authoritarian oversight, can negate these bonuses.
- Colonies with **mild climates** gain additional benefits.

The system integrates with colony growth, automatically applying or removing bonuses as populations change.

## Features

- Dynamic tracking of colony size and population composition.
- Overrides vanilla Luddic Church hostile activity behavior to prevent conflicts with modded bonuses.
- Tooltips and Codex support explain bonuses and negating factors.
- Fully compatible with vanilla Starsector and major mods.

## Installation

1. Download or clone this repository into your Starsector `mods/` folder.
2. No additional setup required.

## Requirements

- Starsector 0.95+
- Optional: LazyLib, MagicLib, or other supporting libraries for extended functionality.

## Notes

- The mod uses a persistent `ColonySizeChangeListener` to ensure bonuses remain consistent across saves.
- Vanilla Luddic Church listeners that interfere with the condition are automatically removed, without affecting hostile activity events.

## License

Default: MIT.

## Author

- DonationBox
- Exon Expedition