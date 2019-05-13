package com.colacelli.librebot

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.plugins.access.AccessPlugin
import com.colacelli.ircbot.plugins.autoop.AutoOpPlugin
import com.colacelli.ircbot.plugins.autoreconnect.AutoReconnectPlugin
import com.colacelli.ircbot.plugins.autoresponse.AutoResponsePlugin
import com.colacelli.ircbot.plugins.ctcpversion.CTCPVersionPlugin
import com.colacelli.ircbot.plugins.help.HelpPlugin
import com.colacelli.ircbot.plugins.ircop.IRCopPlugin
import com.colacelli.ircbot.plugins.joinpart.JoinPartPlugin
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin
import com.colacelli.ircbot.plugins.operator.OperatorPlugin
import com.colacelli.ircbot.plugins.pluginloader.PluginLoaderPlugin
import com.colacelli.ircbot.plugins.rejoinonkick.RejoinOnKickPlugin
import com.colacelli.ircbot.plugins.rssfeed.RSSFeedPlugin
import com.colacelli.ircbot.plugins.search.SearchPlugin
import com.colacelli.ircbot.plugins.torrent.TorrentPlugin
import com.colacelli.ircbot.plugins.translate.TranslatePlugin
import com.colacelli.ircbot.plugins.uptime.UptimePlugin
import com.colacelli.ircbot.plugins.websitetitle.WebsiteTitlePlugin
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Server
import java.io.FileInputStream
import java.util.*

class Librebot {
    companion object {
        private const val PROPERTIES_HOSTNAME = "hostname"
        private const val PROPERTIES_PORT = "port"
        private const val PROPERTIES_SSL = "ssl"
        private const val PROPERTIES_PASSWORD = "password"
        private const val PROPERTIES_LOGIN = "login"
        private const val PROPERTIES_REAL_NAME = "real_name"
        private const val PROPERTIES_NICK = "nick"
        private const val PROPERTIES_NICKSERV_PASSWORD = "nickserv_password"
        private const val PROPERTIES_IRCOP_NAME = "ircop_name"
        private const val PROPERTIES_IRCOP_PASSWORD = "ircop_password"
        private const val PROPERTIES_CTCP_VERSION = "ctcp_version"
        private const val PROPERTIES_FILE = "librebot.properties"

        @JvmStatic
        fun main(args: Array<String>) {

            val properties = Properties()
            properties.load(FileInputStream(PROPERTIES_FILE))

            val server = Server(
                    properties.getProperty(PROPERTIES_HOSTNAME, "irc.freenode.net"),
                    properties.getProperty(PROPERTIES_PORT, "6697").toInt(),
                    properties.getProperty(PROPERTIES_SSL, "true").toBoolean(),
                    properties.getProperty(PROPERTIES_PASSWORD, "")
            )

            val user = User(
                    properties.getProperty(PROPERTIES_NICK, "librebot"),
                    properties.getProperty(PROPERTIES_LOGIN, "librebot"),
                    properties.getProperty(PROPERTIES_REAL_NAME, "GNU Librebot - https://gitlab.com/juancolacelli/librebot")
            )

            val bot = IRCBot(server, user)

            bot.access.checkWithNickServ = false

            bot.pluginLoader.add(AccessPlugin())
            bot.pluginLoader.add(AutoOpPlugin())
            bot.pluginLoader.add(AutoReconnectPlugin())
            bot.pluginLoader.add(AutoResponsePlugin())
            bot.pluginLoader.add(CTCPVersionPlugin(properties.getProperty(PROPERTIES_CTCP_VERSION, "GNU Librebot - https://gitlab.com/juancolacelli/librebot")))
            bot.pluginLoader.add(HelpPlugin())
            bot.pluginLoader.add(JoinPartPlugin())
            bot.pluginLoader.add(OperatorPlugin())
            bot.pluginLoader.add(PluginLoaderPlugin())
            bot.pluginLoader.add(RejoinOnKickPlugin())
            bot.pluginLoader.add(RSSFeedPlugin())
            bot.pluginLoader.add(SearchPlugin())
            bot.pluginLoader.add(TorrentPlugin())
            bot.pluginLoader.add(TranslatePlugin())
            bot.pluginLoader.add(UptimePlugin())
            bot.pluginLoader.add(WebsiteTitlePlugin())

            val nickservPassword = properties.getProperty(PROPERTIES_NICKSERV_PASSWORD, "")
            if (nickservPassword.isNotBlank()) bot.pluginLoader.add(NickServPlugin(nickservPassword))

            val ircopName = properties.getProperty(PROPERTIES_IRCOP_NAME, "")
            val ircopPassword = properties.getProperty(PROPERTIES_IRCOP_PASSWORD, "")
            if (ircopName.isNotBlank() && ircopPassword.isNotBlank()) bot.pluginLoader.add(IRCopPlugin(ircopName, ircopPassword))

            bot.connect()
        }
    }
}
