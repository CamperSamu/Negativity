package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.PINGSPOOF;

import java.io.IOException;
import java.util.TimerTask;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;

public class PingSpoof extends Cheat implements Listeners {

	public PingSpoof() {
		super(PINGSPOOF, CheatCategory.PLAYER, Materials.SPONGE, false, false, "ping", "spoofing");

		if (checkActive("reachable")) {
			new Thread(() -> {
				new java.util.Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
							managePingSpoof(p, NegativityPlayer.getNegativityPlayer(p));
						}
					}
				}, 6l, 6l);
			}).start();
		}
	}

	/**
	 * Manage the ping and check if it spoof
	 * Method: reachable
	 * Warn: this function have to be called ASYNC.
	 * 
	 * @param p the player to check ping
	 * @param np the negativity player of player
	 */
	public static void managePingSpoof(Player p, NegativityPlayer np) {
		int newPing = p.getPing(), lastPing = np.ints.get(PINGSPOOF, "last-ping", -1);
		if (newPing == lastPing)
			return;
		np.ints.set(PINGSPOOF, "last-ping", newPing);
		if (newPing <= 200)
			return;
		try {
			if (p.getAddress().getAddress().isReachable(newPing - 150)) {
				Negativity.alertMod(ReportType.WARNING, p, Cheat.forKey(PINGSPOOF), 98, "reachable",
						"Last ping: " + lastPing + ", new ping: " + newPing + ".");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
