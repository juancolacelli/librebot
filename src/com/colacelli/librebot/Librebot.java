package com.colacelli.librebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.ircbot.plugins.autoreconnect.AutoReconnectPlugin;
import com.colacelli.ircbot.plugins.esperantotranslator.EsperantoTranslatorPlugin;
import com.colacelli.ircbot.plugins.help.HelpPlugin;
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin;
import com.colacelli.ircbot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.ircbot.plugins.rssfeed.RssFeedPlugin;
import com.colacelli.ircbot.plugins.uptime.UptimePlugin;
import com.colacelli.ircbot.plugins.websitetitle.WebsiteTitlePlugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Librebot {
    private static final String PROPERTIES_FILE = "com/colacelli/librebot/librebot.properties";

    public static void main(String[] args) {
        Properties properties = loadProperties(PROPERTIES_FILE);

        IRCBot bot = new IRCBot();

        addPlugins(bot, properties);

        bot.connect(
                buildServer(properties),
                buildUser(properties)
        );
    }

    private static Properties loadProperties(String propertiesFile) {
        InputStream inputStream;
        inputStream = ClassLoader.getSystemResourceAsStream(propertiesFile);

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private static User buildUser(Properties properties) {
        User.Builder userBuilder = new User.Builder();
        userBuilder
                .setNick(properties.getProperty("NICK"))
                .setLogin(properties.getProperty("LOGIN"));

        return userBuilder.build();
    }

    private static Server buildServer(Properties properties) {
        Server.Builder serverBuilder = new Server.Builder();
        serverBuilder
                .setHostname(properties.getProperty("SERVER"))
                .setPort(Integer.parseInt(properties.getProperty("PORT")))
                .setSecure(Boolean.parseBoolean(properties.getProperty("SECURE")))
                .setPassword(properties.getProperty("PASSWORD"));

        return serverBuilder.build();
    }

    private static void addPlugins(IRCBot bot, Properties properties) {
        ArrayList<Channel> channels = new ArrayList<>();
        for (String channel : properties.getProperty("CHANNELS").split(",")) {
            channels.add(new Channel(channel));
        }

        // Behaviour
        bot.addPlugin(new AutoReconnectPlugin());
        bot.addPlugin(new AutoJoinPlugin(channels));
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new NickServPlugin(properties.getProperty("NICKSERV_PASSWORD")));
        bot.addPlugin(new WebsiteTitlePlugin());
        bot.addPlugin(new RssFeedPlugin(properties.getProperty("RSS_FEED_URLS").split(",")));

        // Commands
        // bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new UptimePlugin());
        bot.addPlugin(new EsperantoTranslatorPlugin(properties.getProperty("REVO_PATH")));

        // Help
        bot.addPlugin(new HelpPlugin());
    }
}
