package com.weatherforecast.features.search.presentation;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.weatherforecast.R;
import com.weatherforecast.core.WeatherForecastApplication;
import com.weatherforecast.core.structure.BaseActivity;
import com.weatherforecast.features.common.data.model.Forecast;
import com.weatherforecast.features.search.data.model.DailyForecast;
import com.weatherforecast.features.search.di.DaggerForecastSearchFeatureComponent;
import com.weatherforecast.features.search.di.ForecastSearchPresentationModule;
import com.weatherforecast.features.search.di.ForecastSearchUseCaseModule;

import java.util.List;

public class ForecastSearchActivity extends BaseActivity<ForecastSearchPresenter> implements ForecastSearchContract.View {

    @Override
    public void initialiseDependencyInjector() {
        DaggerForecastSearchFeatureComponent.builder()
                .applicationComponent(((WeatherForecastApplication) getApplication()).applicationComponent())
                .forecastSearchPresentationModule(new ForecastSearchPresentationModule(this))
                .forecastSearchUseCaseModule(new ForecastSearchUseCaseModule())
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_search_activity);

        initialiseTestTrigger();
    }

    private void initialiseTestTrigger() {
        findViewById(R.id.forecast_test).setOnClickListener(
                view -> presenter.loadLocationForecast(2643743L, "London,UK"));
    }

    @Override
    public LifecycleOwner provideLifecycleOwner() {
        return this;
    }

    @Override
    public ForecastsDataHolder provideForecastsDataHolder() {
        return ViewModelProviders.of(this).get(ForecastsDataHolder.class);
    }

    @Override
    public void showErrorLoadingDailyForecast() {
        Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDailyForecasts(@NonNull final List<DailyForecast> dailyForecasts) {
        Log.d("TEST", String.valueOf(dailyForecasts.size()));
        for (final DailyForecast dailyForecast : dailyForecasts) {
            for (final Forecast forecast : dailyForecast.forecasts()) {
                Log.d("TEST", "KEY " + dailyForecast.date() + " / VALUE " + String.valueOf(forecast.date()));
            }
        }
    }
}
