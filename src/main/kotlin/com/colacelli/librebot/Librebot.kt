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
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths


class Librebot {
    companion object {
        private val bots = ArrayList<IRCBot>()

        private fun loadSettings(): JsonArray? {
            val reader = Files.newBufferedReader(Paths.get("settings.json"))
            return Gson().fromJson(reader, JsonArray::class.java)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            loadSettings()?.forEach {
                val settings = it.asJsonObject

                val server = Server(
                        settings.get("hostname").asString,
                        settings.get("port").asInt,
                        settings.get("ssl").asBoolean,
                        settings.get("password").asString
                )

                val user = User(
                        settings.get("nick").asString,
                        settings.get("login").asString,
                        settings.get("real_name").asString
                )

                val bot = IRCBot(server, user)

                bot.access.checkWithNickServ = settings.get("check_access_with_nickserv").asBoolean

                bot.pluginLoader.add(AccessPlugin())
                bot.pluginLoader.add(AutoOpPlugin())
                bot.pluginLoader.add(AutoReconnectPlugin())
                bot.pluginLoader.add(AutoResponsePlugin())
                bot.pluginLoader.add(CTCPVersionPlugin(settings.get("ctcp_version").asString))
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

                val nickservPassword = settings.get("nickserv_password").asString
                if (nickservPassword.isNotBlank()) bot.pluginLoader.add(NickServPlugin(nickservPassword))

                val ircopName = settings.get("ircop_name").asString
                val ircopPassword = settings.get("ircop_password").asString
                if (ircopName.isNotBlank() && ircopPassword.isNotBlank()) bot.pluginLoader.add(IRCopPlugin(ircopName, ircopPassword))

                bots.add(bot)
            }

            bots.forEach {
                // Last server does not use coroutines
                if (bots.indexOf(it) < bots.size - 1) {
                    GlobalScope.launch {
                        it.connect()
                    }
                } else {
                    it.connect()
                }
            }
        }
    }
}
