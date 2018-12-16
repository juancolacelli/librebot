package com.colacelli.librebot

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.plugins.access.AccessPlugin
import com.colacelli.ircbot.plugins.apertiumtranslate.ApertiumTranslatePlugin
import com.colacelli.ircbot.plugins.autojoin.AutoJoinPlugin
import com.colacelli.ircbot.plugins.autoreconnect.AutoReconnectPlugin
import com.colacelli.ircbot.plugins.autoresponse.AutoResponsePlugin
import com.colacelli.ircbot.plugins.ctcpversion.CTCPVersionPlugin
import com.colacelli.ircbot.plugins.duckduckgosearch.DuckDuckGoSearchPlugin
import com.colacelli.ircbot.plugins.help.HelpPlugin
import com.colacelli.ircbot.plugins.ircop.IRCopPlugin
import com.colacelli.ircbot.plugins.joinpart.JoinPartPlugin
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin
import com.colacelli.ircbot.plugins.operator.OperatorPlugin
import com.colacelli.ircbot.plugins.pluginloader.PluginLoaderPlugin
import com.colacelli.ircbot.plugins.rejoinonkick.RejoinOnKickPlugin
import com.colacelli.ircbot.plugins.rssfeed.RSSFeedPlugin
import com.colacelli.ircbot.plugins.thepiratebaysearch.ThePirateBaySearchPlugin
import com.colacelli.ircbot.plugins.uptime.UptimePlugin
import com.colacelli.ircbot.plugins.websitetitle.WebsiteTitlePlugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Server
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class Librebot {
    companion object {
        private const val PROPERTIES_HOSTNAME = "hostname"
        private const val PROPERTIES_PORT = "port"
        private const val PROPERTIES_SSL = "ssl"
        private const val PROPERTIES_PASSWORD = "password"
        private const val PROPERTIES_LOGIN = "login"
        private const val PROPERTIES_REAL_NAME = "real_name"
        private const val PROPERTIES_NICK = "nick"
        private const val PROPERTIES_CHANNELS = "channels"
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
                    properties.getProperty(PROPERTIES_REAL_NAME, "GNU Librebot - https://gitlab.com/jic/librebot")
            )

            val bot = IRCBot(server, user)

            val channels = ArrayList<Channel>()
            properties.getProperty(PROPERTIES_CHANNELS, "#gnu").split(",").forEach {
                channels.add(Channel(it))
            }

            bot.addPlugin(AccessPlugin())
            bot.addPlugin(ApertiumTranslatePlugin())
            bot.addPlugin(AutoJoinPlugin(channels))
            bot.addPlugin(AutoReconnectPlugin())
            bot.addPlugin(AutoResponsePlugin())
            bot.addPlugin(CTCPVersionPlugin(properties.getProperty(PROPERTIES_CTCP_VERSION, "GNU Librebot - https://gitlab.com/jic/librebot")))
            bot.addPlugin(DuckDuckGoSearchPlugin())
            bot.addPlugin(HelpPlugin())
            bot.addPlugin(JoinPartPlugin())
            bot.addPlugin(PluginLoaderPlugin())
            bot.addPlugin(OperatorPlugin())
            bot.addPlugin(RejoinOnKickPlugin())
            bot.addPlugin(RSSFeedPlugin())
            bot.addPlugin(ThePirateBaySearchPlugin())
            bot.addPlugin(UptimePlugin())
            bot.addPlugin(WebsiteTitlePlugin())

            val nickservPassword = properties.getProperty(PROPERTIES_NICKSERV_PASSWORD, "")
            if (nickservPassword.isNotBlank()) bot.addPlugin(NickServPlugin(nickservPassword))

            val ircopName = properties.getProperty(PROPERTIES_IRCOP_NAME, "")
            val ircopPassword = properties.getProperty(PROPERTIES_IRCOP_PASSWORD, "")
            if (ircopName.isNotBlank() && ircopPassword.isNotBlank()) bot.addPlugin(IRCopPlugin(ircopName, ircopPassword))

            bot.connect()
        }
    }
}