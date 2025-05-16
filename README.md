# RepairShare  

This quality-of-life plugin allows tools that have the "Mending" enchantment to be repaired regardless if it's in the player's hands or equipped as armor.\
\
Additionally, players can hold their Mending tool/armor in their main hand, and use Sneak + Interact (Left Shift + Right Click) to consume XP and repair their tools.\
\
A sound effect is played whenever either scenario occurs. This will be configurable in the future.\
\
You can change the XP mending multiplier as well as costs and amount of mending received!
# Images
*Tools in inventory get automatically repaired if they have the "Mending" enchantment*\
![](https://i.imgur.com/sscIDTM.gif)
\
*Holding sneak and interacting (hold left shift + right click) will consume player's XP and mend the tool in main hand*\
![](https://i.imgur.com/A3mJQ1l.gif)

# Commands

`/repair toggle`\
Allow player to toggle on/off auto mend and self mend\
\
`/sleep reload`\
Reloads config.yml values

# Permissions

```
  repair.*: Gives access to all RepairShare commands and features
  repair.auto_mend: Allows repairing tools with mending in a player's inventory when XP is gained automatically
  repair.self_mend: Allows repairing tools by consuming current XP
  repair.toggle: /repair toggle
  repair.reload: /repair reload
```

# Config

```
  # Mending repairs (experience gained * multiplier), where multiplier by default is 2
  xp_multiplier: 2
  # Allows players to use their current exp to repair items
  allow_self_repair: true
  # How much exp it costs to self repair (whole number)
  self_repair_cost: 6
  # How much durability is given to the tool being repaired (whole number)
  # Note: This is still affected by the xp_multiplier
  self_repair_amount: 6
```

# Notes
* If you find any issues or have suggestions, please open an issue on the GitHub repo [here](https://github.com/sicfran774/RepairShare/issues)
* Due to the nature of armor being equipped when interacted with, the repair will still take place but it will be equipped at the same time. It is better to equip the armor itself and use mending normally than using self-mend
* XP sharing between players as a planned next update
