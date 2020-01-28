package com.example.apipratice1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.textclassifier.TextLinks;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements adapter.OnItemClickListener {
    public static final String EXTRA_URL="imageurl";
    public static final String EXTRA_Titel="title";
    public static final String EXTRA_des="des";




    private RecyclerView  mrecyclerView;
    private adapter adapter;
    private ArrayList<item> arrayList;
    private RequestQueue requestQueue;
   SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mrecyclerView=findViewById(R.id.recyclerview);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList=new ArrayList<>();

        requestQueue= Volley.newRequestQueue(this);
        parsjason();

        mSwipeRefreshLayout=findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }
    private void parsjason(){
        String url="https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";
        JsonObjectRequest reques=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray=response.getJSONArray("rows");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject hit=jsonArray.getJSONObject(i);

                                String  titl=hit.getString("title");
                                String desb=hit.getString("description");
                                String url=hit.getString("imageHref");


                                arrayList.add(new item(url,titl,desb));


                            }
                            adapter=new adapter(MainActivity.this,arrayList);
                            mrecyclerView.setAdapter(adapter);
                            adapter.setOnClickListener(MainActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(reques);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(MainActivity.this,DetailActivity.class);
        item item=arrayList.get(position);
        intent.putExtra(EXTRA_URL,item.getMimgurl());
        intent.putExtra(EXTRA_Titel,item.getmTitle());
        intent.putExtra(EXTRA_des,item.getMdes());

        startActivity(intent);
    }
}
