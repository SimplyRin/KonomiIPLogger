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
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
