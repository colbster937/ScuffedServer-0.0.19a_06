package dev.colbster937.scuffed;

import java.io.File;

import com.mojang.minecraft.server.PlayerInstance;

public class ScuffedUtils {
    public static String formatEnabledDisabled(boolean value) {
        return value ? "ENABLED" : "DISABLED";
    }

    public static boolean isCommand(String commandString, String checkCommand) {
        String[] command = commandString.split(" ");
        if (!command[0].startsWith("/")) command[0] = "/" + command[0];
        return command[0].equalsIgnoreCase(checkCommand);
    }

    public static int isLoginCommand(String commandString) {
        if (ScuffedUtils.isCommand(commandString, "/login") || ScuffedUtils.isCommand(commandString, "/l")) {
            return 1;
        } else if (ScuffedUtils.isCommand(commandString, "/register") || ScuffedUtils.isCommand(commandString, "/reg")) {
            return 2;
        } else {
            return 0;
        }
    }

    public static boolean isRegistered(String player) {
        File file = new File("users", player + ".txt");
        return file.exists();
    }
}
