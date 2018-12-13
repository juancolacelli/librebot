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
import com.colacelli.ircbot.plugins.pluginloader.PluginLoaderPlugin
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin
import com.colacelli.ircbot.plugins.operator.OperatorPlugin
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

const val PROPERTIES_SERVER = "server"
const val PROPERTIES_PORT = "port"
const val PROPERTIES_SECURE = "secure"
const val PROPERTIES_PASSWORD = "password"
const val PROPERTIES_LOGIN = "login"
const val PROPERTIES_REAL_NAME = "real_name"
const val PROPERTIES_NICK = "nick"
const val PROPERTIES_CHANNELS = "channels"
const val PROPERTIES_NICKSERV_PASSWORD = "nickserv_password"
const val PROPERTIES_IRCOP_NAME = "ircop_name"
const val PROPERTIES_IRCOP_PASSWORD = "ircop_password"
const val PROPERTIES_CTCP_VERSION = "ctcp_version"
const val PROPERTIES_FILE = "librebot.properties"

fun main(args : Array<String>) {

    val properties = Properties()
    properties.load(FileInputStream(PROPERTIES_FILE))

    val server = Server.Builder()
            .setHostname(properties.getProperty(PROPERTIES_SERVER))
            .setPort(properties.getProperty(PROPERTIES_PORT).toInt())
            .setSecure(properties.getProperty(PROPERTIES_SECURE).toBoolean())
            .setPassword(properties.getProperty(PROPERTIES_PASSWORD))
            .build()

    val user = User.Builder()
            .setLogin(properties.getProperty(PROPERTIES_LOGIN))
            .setRealName(properties.getProperty(PROPERTIES_REAL_NAME))
            .setNick(properties.getProperty(PROPERTIES_NICK))
            .build()

    val channels = ArrayList<Channel>()
    properties.getProperty(PROPERTIES_CHANNELS).split(",").forEach {
        channels.add(Channel(it))
    }

    val librebot = IRCBot()

    librebot.addPlugin(AccessPlugin())
    librebot.addPlugin(ApertiumTranslatePlugin())
    librebot.addPlugin(AutoJoinPlugin(channels))
    librebot.addPlugin(AutoReconnectPlugin())
    librebot.addPlugin(AutoResponsePlugin())
    librebot.addPlugin(CTCPVersionPlugin(properties.getProperty(PROPERTIES_CTCP_VERSION)))
    librebot.addPlugin(DuckDuckGoSearchPlugin())
    librebot.addPlugin(HelpPlugin())
    librebot.addPlugin(JoinPartPlugin())
    librebot.addPlugin(PluginLoaderPlugin())
    librebot.addPlugin(OperatorPlugin())
    librebot.addPlugin(RejoinOnKickPlugin())
    librebot.addPlugin(RSSFeedPlugin())
    librebot.addPlugin(ThePirateBaySearchPlugin())
    librebot.addPlugin(UptimePlugin())
    librebot.addPlugin(WebsiteTitlePlugin())

    val nickservPassword = properties.getProperty(PROPERTIES_NICKSERV_PASSWORD)
    if (nickservPassword.isNotBlank()) librebot.addPlugin(NickServPlugin(nickservPassword))

    val ircopName = properties.getProperty(PROPERTIES_IRCOP_NAME)
    val ircopPassword = properties.getProperty(PROPERTIES_IRCOP_PASSWORD)
    if (ircopName.isNotBlank() && ircopPassword.isNotBlank()) librebot.addPlugin(IRCopPlugin(ircopName, ircopPassword))

    librebot.connect(server, user)
}