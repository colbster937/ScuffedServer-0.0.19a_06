package dev.colbster937.scuffed;

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
}
