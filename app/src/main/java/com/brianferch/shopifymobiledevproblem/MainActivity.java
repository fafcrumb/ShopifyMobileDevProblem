package com.brianferch.shopifymobiledevproblem;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView totalSpentView = (TextView) findViewById(R.id.totalSpent);
        final TextView bagSoldView = (TextView) findViewById(R.id.bagsSold);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

        JsonObjectRequest jsObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        double totalSpent = 0;
                        int bronzeBagCount = 0;
                        try {
                            JSONArray orders = response.getJSONArray("orders");
                            totalSpent = calculateTotalSpent("Napoleon", "Batz", orders);
                            bronzeBagCount = countItem("Awesome Bronze Bag", orders);
                        } catch (JSONException error) {
                            Log.e("SHOPIFY", "Unexpected JSON exception", error);
                        }
                        totalSpentView.setText("Total spent by Napoleon Batz: $" + totalSpent);
                        bagSoldView.setText("Number of Awesome Bronze Bags sold: " + bronzeBagCount);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        totalSpentView.setText("That didn't work!");
                    }
                });
        queue.add(jsObjReq);
    }

    private double calculateTotalSpent(String first_name, String last_name, JSONArray orders) throws JSONException {
        double total = 0;
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            if (order.has("customer")) {
                JSONObject customer = order.getJSONObject("customer");
                if (customer.getString("first_name").equals(first_name) &&
                        customer.getString("last_name").equals(last_name)) {
                    total += order.getDouble("total_price");
                }
            }
        }
        return total;
    }

    private int countItem(String itemName, JSONArray orders) throws JSONException {
        int count = 0;
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            JSONArray lineItems = order.getJSONArray("line_items");
            for (int j = 0; j < lineItems.length(); j++) {
                JSONObject item = lineItems.getJSONObject(j);
                String itemTitle = item.getString("title");
                if (itemTitle.equals(itemName)) {
                    count++;
                }
            }
        }
        return count;
    }
}
