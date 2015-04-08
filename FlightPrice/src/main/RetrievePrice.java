package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVReader;

@SuppressWarnings("deprecation")
public class RetrievePrice {

	private final String url = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=";
	private String apikey = "";
	@SuppressWarnings("serial")
	private ArrayList<String> apiKeyList = new ArrayList<String>() {
		{
			// add("API-KEY HERE");
		}
	};
	private final String flightDate = "2015-05-12";
	private final String USER_AGENT = "Mozilla/5.0";
	private final String fileName = "./data/flights.csv";

	public void processCSV() {
		int counter = 1;
		String departure = null;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(new File(fileName)));
			String[] record = null;
			while ((record = reader.readNext()) != null) {
				for (int i = 0; i < record.length; i++) {
					if (i == 0) {
						departure = record[i];
					} else {
						if (record[i].length() == 0)
							continue;
						String arrival = record[i];
						String basePrice = getFlightPrice(counter++, departure, arrival);
						if(basePrice==null){
							System.out.printf("[%s,%s,%s,%s],\n", departure, arrival, 0, 0);
							continue;
						}
						String sgdPrice = convertToSGD(basePrice);
						System.out.printf("[%s,%s,%s,%s],\n", departure, arrival, basePrice, sgdPrice);
//						break;
					}
				}
//				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private String convertToSGD(String basePrice) throws ClientProtocolException,
			IOException {
		if(basePrice==null)
			return "0";
		String currency = basePrice.substring(0, 3);
		Double amount = Double.parseDouble(basePrice.substring(3));
//		System.out.println(currency + " " + amount);
		if (currency.equals("SGD"))
			return ""+amount.intValue();
		String currenyMap = currency + "_SGD";
		
		String url = "http://www.freecurrencyconverterapi.com/api/v3/convert?q="+currenyMap+"&compact=y";

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);
		
		HttpResponse response = client.execute(request);
		InputStream is = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader rd = new BufferedReader(isr);

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		String rate = extractKeyword(result.toString(), "val");
//		System.out.println(rate);
		double convertedAmount = Double.parseDouble(rate)*amount;
		return ""+(int)convertedAmount;
	}

	public String getFlightPrice(int counter, String departure, String arrival)
			throws ClientProtocolException, IOException {
		String jsonString = generateJSONMessage(departure, arrival);
//		System.out.println(jsonString);

		switch (counter) {
		case 1:
			apikey = apiKeyList.get(0);
			break;
		case 50:
			apikey = apiKeyList.get(1);
			break;
		case 100:
			apikey = apiKeyList.get(2);
			break;
		case 150:
			apikey = apiKeyList.get(3);
			break;
		case 200:
			apikey = apiKeyList.get(4);
			break;
		}

		CloseableHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + apikey);

		// add header
		post.setHeader("User-Agent", USER_AGENT);
		StringEntity input = new StringEntity(jsonString);
		input.setContentType("application/json");
		post.setEntity(input);

		HttpResponse response = client.execute(post);
//		System.out.println("\nSending 'POST' request to URL : " + url + apikey);
//		System.out.println("Post parameters : " + post.getEntity());
//		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		InputStream is = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader rd = new BufferedReader(isr);

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		client.close();
//		System.out.println(result.toString());
		return extractKeyword(result.toString(),"baseFareTotal");
	}

	private String extractKeyword(String jsonResult, String keyword) {
		JSONParser parser = new JSONParser();
		KeyFinder finder = new KeyFinder();
		finder.setMatchKey(keyword);
		try {
			while (!finder.isEnd()) {
				parser.parse(jsonResult, finder, true);
				if (finder.isFound()) {
					finder.setFound(false);
					return finder.getValue().toString();
				}
			}
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return null;
	}

	/*
	 * { "request": { "slice": [ { "origin": "SIN", "destination": "HKG",
	 * "date": "2015-04-10", "maxStops": 0 } ], "passengers": { "adultCount": 1,
	 * "infantInLapCount": 0, "infantInSeatCount": 0, "childCount": 0,
	 * "seniorCount": 0 }, "solutions": 20, "refundable": false } }
	 */
	@SuppressWarnings("unchecked")
	private String generateJSONMessage(String departure, String arrival) {
		// create passengers object
		JSONObject passenger = new JSONObject();
		passenger.put("adultCount", new Integer(1));
		passenger.put("infantInLapCount", new Integer(0));
		passenger.put("infantInSeatCount", new Integer(0));
		passenger.put("childCount", new Integer(0));
		passenger.put("seniorCount", new Integer(0));

		// create slice object
		JSONObject sliceObj = new JSONObject();
		sliceObj.put("origin", departure);
		sliceObj.put("destination", arrival);
		sliceObj.put("date", flightDate);
		sliceObj.put("maxStops", new Integer(0));
		JSONArray slice = new JSONArray();
		slice.add(sliceObj);

		JSONObject request = new JSONObject();
		request.put("slice", slice);
		request.put("passengers", passenger);
		request.put("solutions", new Integer(1));
		request.put("refundable", new Boolean(false));

		JSONObject message = new JSONObject();
		message.put("request", request);
		return message.toJSONString();
	}
}