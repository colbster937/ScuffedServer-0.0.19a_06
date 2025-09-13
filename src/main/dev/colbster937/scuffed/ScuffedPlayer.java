package dev.colbster937.scuffed;

import com.mojang.minecraft.server.PlayerInstance;

public class ScuffedPlayer {
    private PlayerInstance player;
    private boolean firstLoginReminder;
    private long initTime;
    private long loginRemindTime;
    public long loginTimeout;
	public boolean loggedIn;
	public boolean registered;

    public ScuffedPlayer(PlayerInstance player) {
        this.player = player;
        this.firstLoginReminder = true;
        this.initTime = System.currentTimeMillis();
        this.loginRemindTime = System.currentTimeMillis();
        this.loginTimeout = player.minecraft.scuffedServer.loginTimeout;
        this.loggedIn = false;
        this.registered = false;
    }

    public void remindLogin() {
	    if (!this.loggedIn) {
            if ((System.currentTimeMillis() - this.loginRemindTime >= 5000L) || this.firstLoginReminder) {
                if (ScuffedUtils.isRegistered(this.player.name)) {
                    this.player.sendChatMessage("Please use /login <password> to log in.");
                } else {
                    this.player.sendChatMessage("Please use /register <password> <password> to register.");
                }
                this.loginRemindTime = System.currentTimeMillis();
                this.firstLoginReminder = false;
            }
        }
	}

    public void tick() {
        this.remindLogin();

        if (!this.loggedIn && System.currentTimeMillis() - this.initTime > ((long) this.loginTimeout * 1000L)) {
            this.player.kick("You must log in within " + this.loginTimeout + " seconds!");
            return;
        }
    }
}
