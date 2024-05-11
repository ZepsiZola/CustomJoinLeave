# CustomJoinLeave
A simple plugin that allows you to customize the join and leave messages of your server.

Implements PlaceholderAPI placeholders and suffixes/prefixes from LuckPerms.

### Introduces 2 commands:
- `/vanishleave` - Sends a fake leave message and sets the player to go into vanish-mode.
- `/vanishjoin` - Sends a fake join message and takes the player out of vanish-mode.

## Default Configuration:
```yaml
# Uses MiniMessage format and any PlaceholderAPI placeholders.
# You can also add <prefix> and <suffix> at either end of the playername.
joinMessage: "<yellow>%player_name% <yellow>joined the game"
leaveMessage: "<yellow>%player_name% <yellow>left the game"

# You can set these permissions to whatever you want.
# Just make sure they match the permissions in your permissions plugin.
permissions:
  silentjoin: "essentials.silentjoin"
  silentquit: "essentials.silentquit"
  vanish: "essentials.vanish"

# Set this to whatever command handles vanish on your server.
commands:
  vanish-on: "essentials:vanish on"
  vanish-off: "essentials:vanish off"```