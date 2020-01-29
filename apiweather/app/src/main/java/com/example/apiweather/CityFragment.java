package com.example.apiweather;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apiweather.Common.Common;
import com.example.apiweather.Model.WeatherResult;
import com.example.apiweather.Retrofit.IOpenWeatherMap;
import com.example.apiweather.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class CityFragment extends Fragment {

    private List<String> lstCity;
    private MaterialSearchBar searchBar;

    ImageView img_weather;
    TextView txt_city_name,txt_humidity,txt_sunrise,txt_sunset,txt_pressure,txt_temperature,txt_description,txt_date_time,txt_wind,txt_geo_cood;
    LinearLayout weather_panel;
    ProgressBar loading;

    View iteamView;
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static CityFragment instance;


    public static CityFragment getInstance(){
        if (instance==null)
            instance=new CityFragment();
        return instance;
    }

    public CityFragment(){
        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstaance();
        mService=retrofit.create(IOpenWeatherMap.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        iteamView=inflater.inflate(R.layout.fragment_city,container,false);



        // Inflate the layout for this fragment
        iteamView=inflater.inflate(R.layout.fragment_city,container,false);
        img_weather=iteamView.findViewById(R.id.img_weather3);
        txt_city_name=iteamView.findViewById(R.id.txt_city_name3);
        txt_humidity=iteamView.findViewById(R.id.txt_Humidity3);
        txt_sunrise=iteamView.findViewById(R.id.txt_sunrise3);
        txt_sunset=iteamView.findViewById(R.id.txt_sunset3);
        txt_pressure=iteamView.findViewById(R.id.txt_pressure3);
        txt_temperature=iteamView.findViewById(R.id.txt_temperature3);
        txt_description=iteamView.findViewById(R.id.txt_description3);
        txt_date_time=iteamView.findViewById(R.id.txt_date_time3);
        txt_wind=iteamView.findViewById(R.id.txt_wind3);
        txt_geo_cood=iteamView.findViewById(R.id.txt_geo_cood3);
        weather_panel=iteamView.findViewById(R.id.weather_panel3);
        loading=iteamView.findViewById(R.id.loading3);

        searchBar=iteamView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute();




        return iteamView;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackground() {
            lstCity=new ArrayList<>();

            try {
                StringBuilder builder=new StringBuilder();
                InputStream is=getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream=new GZIPInputStream(is);

                InputStreamReader reader=new InputStreamReader(gzipInputStream);
                BufferedReader in =new BufferedReader(reader);

                String readed;
                while ((readed=in.readLine())!=null)
                    builder.append(readed);
                lstCity=new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());


            }catch (Exception e){
                e.printStackTrace();

            }

            return lstCity;
        }


        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest=new ArrayList<>();
                    for (String search:listCity){
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            loading.setVisibility(View.GONE);

        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(cityName,
                Common.API_KEY,"metric").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                               @Override
                               public void accept(WeatherResult weatherResult) throws Exception {

                                   //Load image

                                   Picasso.with(getContext()).load(new StringBuilder("https://openweathermap.org/img/wn/").append(weatherResult.getWeather().get(0).getIcon())
                                           .append(".png").toString()).into(img_weather);

                                   //load information
                                   txt_city_name.setText(weatherResult.getName());
                                   txt_description.setText(new StringBuilder("weather in ")
                                           .append(weatherResult.getName()).toString());
                                   txt_temperature.setText(new StringBuilder(
                                           String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                                   txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                                   txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append( "hpa").toString());
                                   txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append( " %").toString());
                                   txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                                   txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                                   txt_geo_cood.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());

                                   //Display panel

                                   weather_panel.setVisibility(View.VISIBLE);
                                   loading.setVisibility(View.GONE);

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(getActivity(),""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           }


                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
