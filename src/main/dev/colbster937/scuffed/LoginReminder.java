package dev.colbster937.scuffed;

import com.mojang.minecraft.server.PlayerInstance;

public class LoginReminder {
    private PlayerInstance player;
    private boolean firstMessage;
    private long remindTime;
    public long timeout;

    public LoginReminder(PlayerInstance player) {
        this.player = player;
        this.firstMessage = true;
        this.remindTime = System.currentTimeMillis();
        this.timeout = player.minecraft.scuffedServer.loginTimeout;
    }

    public void remindLogin() {
	    if (!this.player.loggedIn) {
            if ((System.currentTimeMillis() - remindTime >= 5000L) || this.firstMessage) {
                if (this.player.registered) {
                    this.player.sendChatMessage("Please use /login <password> to log in.");
                } else {
                    this.player.sendChatMessage("Please use /register <password> <password> to register.");
                }
                this.remindTime = System.currentTimeMillis();
                this.firstMessage = false;
            }
        }
	}    
}
