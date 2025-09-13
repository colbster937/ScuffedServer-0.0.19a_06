package dev.colbster937.scuffed;

import com.mojang.minecraft.server.PlayerInstance;

public class LoginReminder {
    private PlayerInstance player;
    private long remindTime;
    public long timeout;

    public LoginReminder(PlayerInstance player) {
        this.player = player;
        this.remindTime = System.currentTimeMillis();
        this.timeout = player.minecraft.scuffedServer.loginTimeout;
    }

    public void remindLogin() {
	    if (!this.player.loggedIn) {
            long now = System.currentTimeMillis();
            if (now - remindTime <= 1000L) {
                if (this.player.registered) {
                    this.player.sendChatMessage("Please use /login <password> to log in.");
                } else {
                    this.player.sendChatMessage("Please use /register <password> <password> to register.");
                }
                remindTime = System.currentTimeMillis();
            }
        }
	}    
}
