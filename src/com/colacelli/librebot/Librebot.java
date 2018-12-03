package com.colacelli.librebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.access.AccessPlugin;
import com.colacelli.ircbot.plugins.apertiumtranslate.ApertiumTranslatePlugin;
import com.colacelli.ircbot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.ircbot.plugins.autoreconnect.AutoReconnectPlugin;
import com.colacelli.ircbot.plugins.ctcpversion.CTCPVersionPlugin;
import com.colacelli.ircbot.plugins.duckduckgosearch.DuckDuckGoSearchPlugin;
import com.colacelli.ircbot.plugins.help.HelpPlugin;
import com.colacelli.ircbot.plugins.ircop.IRCopPlugin;
import com.colacelli.ircbot.plugins.joinpart.JoinPartPlugin;
import com.colacelli.ircbot.plugins.loader.LoaderPlugin;
import com.colacelli.ircbot.plugins.nickserv.NickServPlugin;
import com.colacelli.ircbot.plugins.operator.OperatorPlugin;
import com.colacelli.ircbot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.ircbot.plugins.rssfeed.RssFeedPlugin;
import com.colacelli.ircbot.plugins.thepiratebaysearch.ThePirateBaySearchPlugin;
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
    private static final String PROPERTIES_IRCOP_NAME = "ircop_name";
    private static final String PROPERTIES_IRCOP_PASSWORD = "ircop_password";
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
        User.Builder builder = new User.Builder();
        builder
                .setNick(properties.getProperty(PROPERTIES_NICK))
                .setLogin(properties.getProperty(PROPERTIES_IDENT))
                .setRealName(properties.getProperty(PROPERTIES_REAL_NAME));

        return builder.build();
    }

    private static Server buildServer(Properties properties) {
        Server.Builder builder = new Server.Builder();
        builder
                .setHostname(properties.getProperty(PROPERTIES_SERVER))
                .setPort(Integer.parseInt(properties.getProperty(PROPERTIES_PORT)))
                .setSecure(Boolean.parseBoolean(properties.getProperty(PROPERTIES_SSL)))
                .setPassword(properties.getProperty(PROPERTIES_PASSWORD));

        return builder.build();
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
        bot.addPlugin(new DuckDuckGoSearchPlugin());
        bot.addPlugin(new HelpPlugin());
        bot.addPlugin(new JoinPartPlugin());
        bot.addPlugin(new LoaderPlugin());
        bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new RssFeedPlugin());
        bot.addPlugin(new ThePirateBaySearchPlugin());
        bot.addPlugin(new UptimePlugin());
        bot.addPlugin(new WebsiteTitlePlugin());

        String nickservPassword = properties.getProperty(PROPERTIES_NICKSERV_PASSWORD);
        if (!nickservPassword.isEmpty()) bot.addPlugin(new NickServPlugin(nickservPassword));

        String ircopName = properties.getProperty(PROPERTIES_IRCOP_NAME);
        String ircopPassword = properties.getProperty(PROPERTIES_IRCOP_PASSWORD);
        if (!ircopName.isEmpty() && !ircopPassword.isEmpty()) bot.addPlugin(new IRCopPlugin(ircopName, ircopPassword));
    }
}
