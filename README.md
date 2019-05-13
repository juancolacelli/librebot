# GNU Librebot

## Dependencies
* **GNU IRC Bot**: https://gitlab.com/juancolacelli/ircbot

### Configuration
#### librebot.properties
You need to rename librebot.properties.sample as librebot.properties

* **server**: IRC server hostname
* **port**: IRC server port
* **ssl**: Activate/Deactivate SSL
* **password**: IRC server password
* **ident**: Ident
* **real_name**: Real name
* **name**: Nick
* **ctcp_version**: CTCP Version response
* **ircop_name**: IRCop name
* **ircop_password**: IRCop password
* **nickserv_password**: NickServ authentication password

#### access.properties
You need to rename access.properties.sample as access.properties and replace your_nickname with your nickname in lowercase

#### channels.properties
You need to rename channels.properties.sample as channels.properties and replace #debug with your desired channels to join (comma separated without spaces)