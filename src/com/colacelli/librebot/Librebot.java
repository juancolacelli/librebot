package com.colacelli.librebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.access.AccessPlugin;
import com.colacelli.ircbot.plugins.apertiumtranslate.ApertiumTranslatePlugin;
import com.colacelli.ircbot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.ircbot.plugins.autoreconnect.AutoReconnectPlugin;
import com.colacelli.ircbot.plugins.ctcpversion.CTCPVersionPlugin;
import com.colacelli.ircbot.plugins.help.HelpPlugin;
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin;
import com.colacelli.ircbot.plugins.operator.OperatorPlugin;
import com.colacelli.ircbot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.ircbot.plugins.rssfeed.RssFeedPlugin;
import com.colacelli.ircbot.plugins.uptime.UptimePlugin;
import com.colacelli.ircbot.plugins.websitetitle.WebsiteTitlePlugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Librebot {
    private static final String PROPERTIES_SERVER = "server";
    private static final String PROPERTIES_PORT = "port";
    private static final String PROPERTIES_SSL = "ssl";
    private static final String PROPERTIES_PASSWORD = "password";
    private static final String PROPERTIES_IDENT = "ident";
    private static final String PROPERTIES_REAL_NAME = "real_name";
    private static final String PROPERTIES_NICK = "nick";
    private static final String PROPERTIES_CHANNELS = "channels";
    private static final String PROPERTIES_NICKSERV_PASSWORD = "nickserv_password";
    private static final String PROPERTIES_CTCP_VERSION = "ctcp_version";
    private static final String PROPERTIES_FILE = "librebot.properties";

    public static void main(String[] args) {
        Properties properties = loadProperties();

        IRCBot bot = new IRCBot();

        addPlugins(bot, properties);

        bot.connect(
                buildServer(properties),
                buildUser(properties)
        );
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private static User buildUser(Properties properties) {
        User.Builder userBuilder = new User.Builder();
        userBuilder
                .setNick(properties.getProperty(PROPERTIES_NICK))
                .setLogin(properties.getProperty(PROPERTIES_IDENT))
                .setRealName(properties.getProperty(PROPERTIES_REAL_NAME));

        return userBuilder.build();
    }

    private static Server buildServer(Properties properties) {
        Server.Builder serverBuilder = new Server.Builder();
        serverBuilder
                .setHostname(properties.getProperty(PROPERTIES_SERVER))
                .setPort(Integer.parseInt(properties.getProperty(PROPERTIES_PORT)))
                .setSecure(Boolean.parseBoolean(properties.getProperty(PROPERTIES_SSL)))
                .setPassword(properties.getProperty(PROPERTIES_PASSWORD));

        return serverBuilder.build();
    }

    private static void addPlugins(IRCBot bot, Properties properties) {
        ArrayList<Channel> channels = new ArrayList<>();
        for (String channel : properties.getProperty(PROPERTIES_CHANNELS).split(",")) {
            channels.add(new Channel(channel));
        }

        bot.addPlugin(new AccessPlugin());
        bot.addPlugin(new ApertiumTranslatePlugin());
        bot.addPlugin(new AutoJoinPlugin(channels));
        bot.addPlugin(new AutoReconnectPlugin());
        bot.addPlugin(new CTCPVersionPlugin(properties.getProperty(PROPERTIES_CTCP_VERSION)));
        bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new RssFeedPlugin());
        bot.addPlugin(new UptimePlugin());
        bot.addPlugin(new WebsiteTitlePlugin());

        String nickservPassword = properties.getProperty(PROPERTIES_NICKSERV_PASSWORD);
        if (!nickservPassword.isEmpty()) bot.addPlugin(new NickServPlugin(nickservPassword));

        // Help
        bot.addPlugin(new HelpPlugin());
    }
}
