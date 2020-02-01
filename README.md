# GNU Librebot

## Dependencies
* **GNU IRC Bot**: https://gitlab.com/juancolacelli/ircbot

### Configuration
#### librebot.properties
You need to rename settings.json.sample as settings.json

* **hostname**: IRC server hostname
* **port**: IRC server port
* **ssl**: Activate/Deactivate SSL
* **password**: IRC server password
* **nick**: Nick
* **login**: Ident
* **real_name**: Real name
* **ctcp_version**: CTCP Version response
* **nickserv_password**: NickServ authentication password
* **ircop_name**: IRCop name
* **ircop_password**: IRCop password
* **check_access_with_nickserv**: Check if nicks are identified with NickServ when using commands

#### access.properties
You need to rename access.properties.sample as access.properties and replace your_nickname with your nickname in lowercase

#### channels.properties
You need to rename channels.properties.sample as channels.properties and replace #debug with your desired channels to join (comma separated without spaces)