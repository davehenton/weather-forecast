package com.weatherforecast.features.dailyforecast.presentation;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.weatherforecast.R;
import com.weatherforecast.core.WeatherForecastApplication;
import com.weatherforecast.core.structure.BaseActivity;
import com.weatherforecast.features.common.data.model.Forecast;
import com.weatherforecast.features.dailyforecast.data.model.DailyForecast;
import com.weatherforecast.features.dailyforecast.di.DaggerDailyForecastFeatureComponent;
import com.weatherforecast.features.dailyforecast.di.DailyForecastPresentationModule;
import com.weatherforecast.features.dailyforecast.di.DailyForecastUseCaseModule;

import java.util.List;

public class DailyForecastActivity extends BaseActivity<DailyForecastPresenter> implements DailyForecastContract.View {

    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_LOCATION = "extra_location";

    public static Intent newIntent(@NonNull final Context context, final long id, @NonNull final String location) {
        return new Intent(context, DailyForecastActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_LOCATION, location);
    }

    @Override
    public void initialiseDependencyInjector() {
        DaggerDailyForecastFeatureComponent.builder()
                .applicationComponent(((WeatherForecastApplication) getApplication()).applicationComponent())
                .dailyForecastPresentationModule(new DailyForecastPresentationModule(this))
                .dailyForecastUseCaseModule(new DailyForecastUseCaseModule())
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_forecast_activity);

        extractExtrasAndLoadDailyForecast();
    }

    private void extractExtrasAndLoadDailyForecast() {
        final long id = getIntent().getLongExtra(EXTRA_ID, -1L);
        final String location = getIntent().getStringExtra(EXTRA_LOCATION);

        presenter.loadLocationForecast(id, location);
    }

    @Override
    public LifecycleOwner provideLifecycleOwner() {
        return this;
    }

    @Override
    public DailyForecastDataHolder provideForecastsDataHolder() {
        return ViewModelProviders.of(this).get(DailyForecastDataHolder.class);
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
