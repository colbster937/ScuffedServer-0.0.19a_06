package dev.colbster937.scuffed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Iterator;
import java.util.logging.Logger;

import com.mojang.minecraft.server.MinecraftServer;
import com.mojang.minecraft.server.PlayerInstance;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.password.PasswordHasher;
import com.mojang.minecraft.net.Packet;

public class ScuffedServer {
    private static Logger logger;
    private Properties properties = new Properties();
    private MinecraftServer server;
    public boolean antiCheat = true;
    public boolean liquidFlow = true;
    public int buildLimit = 64;
    public int maxPlayers = 16;
    public int loginTimeout = 30;

    private String serverVersion = "0.1";

    public ScuffedServer(MinecraftServer server) {
        this.server = server;

        logger = this.server.logger;

        logger.info("Loading ScuffedServer " + serverVersion);

        try {
            this.properties.load(new FileReader("scuffed.properties"));
        } catch (Exception var3) {
            logger.warning("Failed to load scuffed.properties!");
        }

        try {
            this.antiCheat = Boolean.parseBoolean(this.properties.getProperty("anti-cheat", "true"));
            this.liquidFlow = Boolean.parseBoolean(this.properties.getProperty("liquid-flow", "true"));
            this.buildLimit = Integer.parseInt(this.properties.getProperty("build-limit", "64"));
            this.maxPlayers = Integer.parseInt(this.properties.getProperty("max-players", "16"));
            this.loginTimeout = Integer.parseInt(this.properties.getProperty("login-timeout", "30"));

            if (this.maxPlayers < 1) {
                this.maxPlayers = 16;
            }

            this.properties.setProperty("anti-cheat", "" + this.antiCheat);
            this.properties.setProperty("liquid-flow", "" + this.liquidFlow);
            this.properties.setProperty("build-limit", "" + this.buildLimit);
            this.properties.setProperty("max-players", "" + this.maxPlayers);
            this.properties.setProperty("login-timeout", "" + this.loginTimeout);
        } catch (Exception e) {
            logger.warning("scuffed.properties is broken! Delete it or fix it!");
            System.exit(0);
        }

        try {
            this.properties.store(new FileWriter("scuffed.properties"), "Scuffed Overrides");
        } catch (Exception var1) {
            logger.warning("Failed to save scuffed.properties!");
        }

        logger.info("Scuffed Overrides:");
        logger.info(" * anti-cheat = " + ScuffedUtils.formatEnabledDisabled(this.antiCheat));
        logger.info(" * liquid-flow = " + ScuffedUtils.formatEnabledDisabled(this.liquidFlow));
        logger.info(" * build-limit = " + this.buildLimit);
        logger.info(" * max-players = " + this.maxPlayers);
        logger.info(" * login-timeout = " + this.loginTimeout);

        this.server.maxPlayers = this.maxPlayers;
        this.server.maxConnectCount = this.maxPlayers;
    }

    public boolean parseCommand(PlayerInstance player, String commandString) {
        String[] command = commandString.split(" ");
        if (command[0].toLowerCase().equals("register") || command[0].toLowerCase().equals("reg")) {
            if (player.registered) {
                player.sendChatMessage("You are already registered! Use /login.");
                return true;
            }
            if (command.length != 3) {
                player.sendChatMessage("Usage: /register <password> <password>");
                return true;
            }
            if (!command[1].equals(command[2])) {
                player.sendChatMessage("Passwords do not match!");
                return true;
            }
            try {
                String hash = PasswordHasher.hash(command[1]);
                File file = new File("users", player.name + ".txt");
                file.getParentFile().mkdirs();
                Files.write(file.toPath(), hash.getBytes());
                player.sendChatMessage("Successfully Registered!");
                player.registered = true;
                this.login(player, true);
            } catch (Exception e) {
                player.sendChatMessage("Failed to register. Try again.");
            }
            return true;
        } else if (command[0].toLowerCase().equals("login") || command[0].toLowerCase().equals("l")) {
            if (player.loggedIn) {
                player.sendChatMessage("You are already logged in.");
                return true;
            }
            if (command.length != 2) {
                player.sendChatMessage("Usage: /login <password>");
                return true;
            }
            try {
                File file = new File("users", player.name + ".txt");
                if (!file.exists()) {
                    player.sendChatMessage("You are not registered! Use /register first.");
                    return true;
                }
                String storedHash = new String(Files.readAllBytes(file.toPath())).trim();
                String inputHash = PasswordHasher.hash(command[1]);
                if (storedHash.equals(inputHash)) {
                    this.login(player, true);
                } else {
                    player.sendChatMessage("Incorrect password!");
                }
            } catch (Exception e) {
                player.sendChatMessage("Failed to log in. Try again.");
            }
            return true;
        }
        return false;
    }

    public void login(PlayerInstance player, boolean alert) {
        player.loggedIn = true;
        if (alert) player.sendChatMessage("Login successful! You can now play.");
        this.server.sendPacket(Packet.CHAT_MESSAGE,
                new Object[] { Integer.valueOf(-1), player.name + " joined the game" });

        Level lvl = this.server.level;

        this.server.sendPlayerPacket(player, Packet.PLAYER_JOIN,
                new Object[] { Integer.valueOf(player.playerID), player.name,
                        Integer.valueOf((lvl.xSpawn << 5) + 16), Integer.valueOf((lvl.ySpawn << 5) + 16),
                        Integer.valueOf((lvl.zSpawn << 5) + 16),
                        Integer.valueOf((int) (lvl.rotSpawn * 256.0F / 360.0F)), Integer.valueOf(0) });
        Iterator players = this.server.getPlayerList().iterator();

        while (players.hasNext()) {
            PlayerInstance playerInstance = (PlayerInstance) players.next();
            if (playerInstance != null && playerInstance != player && playerInstance.onlyIP) {
                player.connection.sendPacket(Packet.PLAYER_JOIN,
                        new Object[] { Integer.valueOf(playerInstance.playerID), playerInstance.name,
                                Integer.valueOf(playerInstance.x),
                                Integer.valueOf(playerInstance.y), Integer.valueOf(playerInstance.z),
                                Integer.valueOf(playerInstance.yaw),
                                Integer.valueOf(playerInstance.pitch) });
            }
        }
    }

    public static boolean chatLoggedIn(PlayerInstance player, String message) {
		if (!player.loggedIn && !ScuffedUtils.isLoginCommand(message)) {
		    player.sendChatMessage("You need to log in first to chat!");
            return false;
        }

        return true;
    }

    public void sendPlayerPacketLoggedIn(PlayerInstance player, Packet packet, Object... data) {
        if (player.loggedIn) player.minecraft.sendPlayerPacket(player, packet, data);
    }
}
