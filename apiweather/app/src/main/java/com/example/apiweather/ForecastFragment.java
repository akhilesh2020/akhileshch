package com.example.apiweather;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apiweather.Adapter.WeatherForecastAdapter;
import com.example.apiweather.Common.Common;
import com.example.apiweather.Model.WeatherForecastResult;
import com.example.apiweather.Model.WeatherResult;
import com.example.apiweather.Retrofit.IOpenWeatherMap;
import com.example.apiweather.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mservice;

    TextView txt_city_name,txt_geo_coord;
    RecyclerView recycler_forecast;

    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if (instance==null)
            instance=new ForecastFragment();

        return instance;
    }

    public ForecastFragment() {

        compositeDisposable =new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstaance();
        mservice=retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View iteamView=inflater.inflate(R.layout.fragment_forecast,container,false);

        txt_city_name=iteamView.findViewById(R.id.txt_city_name2);
        txt_geo_coord=iteamView.findViewById(R.id.txt_geo_cood2);

        recycler_forecast=iteamView.findViewById(R.id.recycle_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));

        getForecastWeatherInformation();



        return iteamView;
    }

    private void getForecastWeatherInformation() {
        compositeDisposable.add(mservice.getForecastWeatherByLatLng(
                String.valueOf(Common.current_Location.getLatitude()),
                String.valueOf(Common.current_Location.getLongitude()),
                Common.API_KEY,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForcastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());

                    }
                })


        );

    }

    private void displayForcastWeather(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        txt_geo_coord.setText(new StringBuilder("[").append(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter=new WeatherForecastAdapter(getContext(),weatherForecastResult);
        recycler_forecast.setAdapter(adapter);

    }

}
