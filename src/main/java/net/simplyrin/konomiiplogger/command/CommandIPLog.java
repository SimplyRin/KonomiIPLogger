package net.simplyrin.konomiiplogger.command;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.simplyrin.konomiiplogger.Main;
import net.simplyrin.threadpool.ThreadPool;

/**
 * Created by SimplyRin on 2018/11/30.
 *
 * Copyright (C) 2018 SimplyRin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class CommandIPLog extends Command {

	private Main instance;

	public CommandIPLog(Main instance) {
		super("iplog", null, "iplogger");
		this.instance = instance;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("konomiiplogger.command")) {
			sender.sendMessage(ChatColor.RED + "You don't have access to this command");
			return;
		}

		if (args.length > 0) {
			String name = args[0];

			UUID uuid;
			try {
				uuid = UUID.fromString(this.instance.getString("Name." + name.toLowerCase() + ".UUID"));
			} catch (Exception e) {
				this.instance.sendMessage(sender, ChatColor.RED + "Player not found!");
				return;
			}

			String key = uuid.toString();

			ThreadPool.run(() -> {
				this.instance.sendMessage(sender, "&aName: " + this.instance.getString("UUID." + key + ".Name"));
				this.instance.sendMessage(sender, "&aAs: " + this.instance.getString("UUID." + key + ".As"));
				this.instance.sendMessage(sender, "&aCity: " + this.instance.getString("UUID." + key + ".City"));
				this.instance.sendMessage(sender, "&aCountry: " + this.instance.getString("UUID." + key + ".Country"));
				this.instance.sendMessage(sender, "&aCountryCode: " + this.instance.getString("UUID." + key + ".CountryCode"));
				this.instance.sendMessage(sender, "&aIsp: " + this.instance.getString("UUID." + key + ".Isp"));
				this.instance.sendMessage(sender, "&aLat: " + this.instance.getString("UUID." + key + ".Lat"));
				this.instance.sendMessage(sender, "&aLon: " + this.instance.getString("UUID." + key + ".Lon"));
				this.instance.sendMessage(sender, "&aOrg: " + this.instance.getString("UUID." + key + ".Org"));
				this.instance.sendMessage(sender, "&aQuery: " + this.instance.getString("UUID." + key + ".Query"));
				this.instance.sendMessage(sender, "&aRegion: " + this.instance.getString("UUID." + key + ".Region"));
				this.instance.sendMessage(sender, "&aRegionName: " + this.instance.getString("UUID." + key + ".RegionName"));
				this.instance.sendMessage(sender, "&aStatus: " + this.instance.getString("UUID." + key + ".Status"));
				this.instance.sendMessage(sender, "&aTimezone: " + this.instance.getString("UUID." + key + ".Timezone"));
				this.instance.sendMessage(sender, "&aZip: " + this.instance.getString("UUID." + key + ".Zip"));
			});
			return;
		}

		this.instance.sendMessage(sender, ChatColor.RED + "Usage: /iplog <player>");
	}

}
