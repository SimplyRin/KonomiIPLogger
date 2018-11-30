package net.simplyrin.konomiiplogger.utils;

import club.sk1er.utils.HttpClient;
import club.sk1er.utils.JsonHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
public class Request {

	private ProxiedPlayer player;

	public Request(ProxiedPlayer player) {
		this.player = player;
	}

	public void postRequest(Callback callback) {
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

			Result result = new Result(this.player, as, city, country, countryCode, isp, lat, lon, org, query, region, regionName, status, timezone, zip);

			callback.onDone(result);
		});
	}

	public interface Callback {
		public void onDone(Result result);
	}

	@Getter
	@AllArgsConstructor
	public class Result {
		private ProxiedPlayer player;
		private String as;
		private String city;
		private String country;
		private String countryCode;
		private String isp;
		private double lat;
		private double lon;
		private String org;
		private String query;
		private String region;
		private String regionName;
		private String status;
		private String timezone;
		private String zip;
	}

}
