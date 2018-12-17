package net.simplyrin.konomiiplogger.utils;

import club.sk1er.utils.HttpClient;
import club.sk1er.utils.JsonHolder;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.simplyrin.konomiiplogger.Main;
import net.simplyrin.konomiiplogger.event.KonomiIPCheckEvent;
import net.simplyrin.konomiiplogger.event.KonomiIPInfo;
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
@AllArgsConstructor
public class Request {

	private Main instance;
	private ProxiedPlayer player;

	public void postRequest() {
		ThreadPool.run(() -> {
			String address = this.player.getAddress().getHostString();

			String raw = HttpClient.rawWithAgent("http://ip-api.com/json/" + address);
			JsonHolder jsonHolder = new JsonHolder(raw);

			String as = jsonHolder.optString("as");
			String city = jsonHolder.optString("city");
			String country = jsonHolder.optString("country");
			String countryCode = jsonHolder.optString("countryCode");
			String isp = jsonHolder.optString("isp");
			double lat = jsonHolder.optDouble("lat");
			double lon = jsonHolder.optDouble("lon");
			String org = jsonHolder.optString("org");
			String query = jsonHolder.optString("query");
			String region = jsonHolder.optString("region");
			String regionName = jsonHolder.optString("regionName");
			String status = jsonHolder.optString("status");
			String timezone = jsonHolder.optString("timezone");
			String zip = jsonHolder.optString("zip");

			KonomiIPInfo result = new KonomiIPInfo(this.player, as, city, country, countryCode, isp, lat, lon, org, query, region, regionName, status, timezone, zip);

			// callback.onDone(result);

			this.instance.getProxy().getPluginManager().callEvent(new KonomiIPCheckEvent(this.player, result));
		});
	}

}
