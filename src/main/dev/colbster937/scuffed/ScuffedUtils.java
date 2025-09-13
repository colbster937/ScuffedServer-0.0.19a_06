package dev.colbster937.scuffed;

import java.io.File;

import com.mojang.minecraft.server.PlayerInstance;

public class ScuffedUtils {
    public static String formatEnabledDisabled(boolean value) {
        return value ? "ENABLED" : "DISABLED";
    }

    public static boolean isCommand(String commandString, String checkCommand) {
        String[] command = commandString.split(" ");
        return command[0].equalsIgnoreCase(checkCommand);
    }

    public static boolean isLoginCommand(String commandSring) {
        return ScuffedUtils.isCommand(commandSring, "/l") || ScuffedUtils.isCommand(commandSring, "/reg") || ScuffedUtils.isCommand(commandSring, "/login") || ScuffedUtils.isCommand(commandSring, "/register");
    }

    public static boolean isRegistered(PlayerInstance player) {
        File file = new File("users", player.name + ".txt");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
