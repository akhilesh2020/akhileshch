package com.example.apiweather;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragement extends Fragment {

    ImageView img_weather;
    TextView txt_city_name,txt_humidity,txt_sunrise,txt_sunset,txt_pressure,txt_temperature,txt_description,txt_date_time,txt_wind,txt_geo_cood;
    LinearLayout weather_panel;
    ProgressBar loading;

    View iteamView;
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static TodayWeatherFragement instance;


    public static TodayWeatherFragement getInstance(){
        if (instance==null)
            instance=new TodayWeatherFragement();
        return instance;
    }
    public TodayWeatherFragement() {

        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstaance();
        mService=retrofit.create(IOpenWeatherMap.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        iteamView=inflater.inflate(R.layout.fragment_today_weather_fragement,container,false);
        img_weather=iteamView.findViewById(R.id.img_weather);
        txt_city_name=iteamView.findViewById(R.id.txt_city_name);
        txt_humidity=iteamView.findViewById(R.id.txt_Humidity);
        txt_sunrise=iteamView.findViewById(R.id.txt_sunrise);
        txt_sunset=iteamView.findViewById(R.id.txt_sunset);
        txt_pressure=iteamView.findViewById(R.id.txt_pressure);
        txt_temperature=iteamView.findViewById(R.id.txt_temperature);
        txt_description=iteamView.findViewById(R.id.txt_description);
        txt_date_time=iteamView.findViewById(R.id.txt_date_time);
        txt_wind=iteamView.findViewById(R.id.txt_wind);
        txt_geo_cood=iteamView.findViewById(R.id.txt_geo_cood);
        weather_panel=iteamView.findViewById(R.id.weather_panel);
        loading=iteamView.findViewById(R.id.loading);


        getWeatherInformation();
        return iteamView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_Location.getLatitude()),
                String.valueOf(Common.current_Location.getLongitude()),
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
