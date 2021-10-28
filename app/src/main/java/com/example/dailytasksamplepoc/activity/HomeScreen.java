package com.example.dailytasksamplepoc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dailytasksamplepoc.R;
import com.example.dailytasksamplepoc.adapter.MyAdapter;
import com.example.dailytasksamplepoc.model.Datum;
import com.example.dailytasksamplepoc.utils.Constant;
import com.example.dailytasksamplepoc.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {


    RecyclerView rv_user;
    RecyclerView.Adapter adapter;
    ProgressDialog pDialog;
    private List<Datum> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        rv_user = findViewById(R.id.rv_user);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        rv_user.setLayoutManager( mLayoutManager );
        rv_user.setAdapter( adapter );

        listItems = new ArrayList<>();

        rv_user.addOnItemTouchListener(
                new RecyclerItemClickListener( getApplicationContext() ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view , int position) {
                        // TODO Handle item click
                        Toast.makeText(HomeScreen.this, listItems.get(position).getFirstName() + " " + listItems.get(position).getLastName(), Toast.LENGTH_SHORT).show();
                    }
                } )
        );

        getUser();
    }

    private void getUser() {
        pDialog = ProgressDialog.show( HomeScreen.this ,"" ,"Wait..." ,false ,false );

        StringRequest stringRequest = new StringRequest( Request.Method.GET ,
                Constant.GET_URL ,new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.dismiss();
                    JSONObject jsonObject = new JSONObject( response );

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject( i );
                        Datum item = new Datum();
                        item.setId( object.getInt( "id" ) );
                        item.setEmail( object.getString( "email" ) );
                        item.setFirstName( object.getString( "first_name" ) );
                        item.setLastName( object.getString( "last_name" ) );
                        item.setAvatar( object.getString( "avatar" ) );
                        listItems.add( item );
                    }
                    adapter = new MyAdapter( listItems ,getApplicationContext());
                    rv_user.setAdapter( adapter );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.d("Unnati ", "Volley Error : " + error.getMessage());
                        Toast.makeText(HomeScreen.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } );

        RequestQueue requestQueue = Volley.newRequestQueue( HomeScreen.this );
        requestQueue.add( stringRequest );
    }
}