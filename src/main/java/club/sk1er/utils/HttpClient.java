package club.sk1er.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;

public class HttpClient {

	/*
	 *     Copyright (C) 2018  Hyperium <https://hyperium.cc/>
	 *
	 *     This program is free software: you can redistribute it and/or modify
	 *     it under the terms of the GNU Lesser General Public License as published
	 *     by the Free Software Foundation, either version 3 of the License, or
	 *     (at your option) any later version.
	 *
	 *     This program is distributed in the hope that it will be useful,
	 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
	 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 *     GNU Lesser General Public License for more details.
	 *
	 *     You should have received a copy of the GNU Lesser General Public License
	 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
	 */

	public static String rawWithAgent(String url) {
		System.out.println("Fetching: " + url);
		try {
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(true);
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setReadTimeout(15000);
			connection.setConnectTimeout(15000);
			connection.setDoOutput(true);
			InputStream is = connection.getInputStream();
			return IOUtils.toString(is, Charsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonObject object = new JsonObject();
		object.addProperty("success", false);
		object.addProperty("cause", "Exception");
		return object.toString();
	}

}
