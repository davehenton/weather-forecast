package com.weatherforecast.features.search.presentation;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.weatherforecast.core.data.usecase.UseCase;
import com.weatherforecast.features.common.data.model.City;
import com.weatherforecast.features.search.data.Weather;
import com.weatherforecast.features.search.usecase.FetchLocationsSearchedUseCase;
import com.weatherforecast.features.search.usecase.FetchWeatherUseCase;

import java.util.List;

import static android.support.annotation.VisibleForTesting.PRIVATE;

@SuppressWarnings("ConstantConditions")
public class SearchPresenter implements SearchContract.Action {

    private final SearchContract.View view;
    private final FetchWeatherUseCase fetchWeatherUseCase;
    private final FetchLocationsSearchedUseCase fetchSearchesUseCase;

    public SearchPresenter(@NonNull final SearchContract.View view,
                           @NonNull final FetchWeatherUseCase fetchWeatherUseCase,
                           @NonNull final FetchLocationsSearchedUseCase fetchSearchesUseCase) {
        this.view = view;
        this.fetchWeatherUseCase = fetchWeatherUseCase;
        this.fetchSearchesUseCase = fetchSearchesUseCase;
    }

    @Override
    public void loadWeatherForLocation(@NonNull final String location) {
        final SearchDataHolder holder = view.provideSearchDataHolder();
        loadRemoteWeather(location, holder);
    }

    @Override
    public void loadLocationsSearched() {
        final SearchDataHolder holder = view.provideSearchDataHolder();
        if (holder.locationSearchesData() != null && holder.locationSearchesData().getValue() != null) {
            handleLocationsSearchedData(holder.locationSearchesData().getValue());
            return;
        }

        loadLocalSearches(holder);
    }

    @VisibleForTesting(otherwise = PRIVATE)
    void loadRemoteWeather(@NonNull final String location, @NonNull final SearchDataHolder holder) {
        final LiveData<Weather> data = fetchWeatherUseCase.executeLive(location,
                holder::addSubscription,
                error -> view.showErrorLoadingWeather(),
                new UseCase.DefaultOnComplete());

        final LifecycleOwner owner = view.provideLifecycleOwner();
        if (holder.weatherData() != null) {
            holder.weatherData().removeObservers(owner);
        }
        holder.weatherData(data);
        data.observe(owner, this::handleWeatherData);
    }

    @VisibleForTesting
    void loadLocalSearches(@NonNull final SearchDataHolder holder) {
        final LiveData<List<City>> data = fetchSearchesUseCase.executeLive(null,
                holder::addSubscription,
                error -> view.showErrorLoadingWeather(),
                new UseCase.DefaultOnComplete());

        final LifecycleOwner owner = view.provideLifecycleOwner();
        if (holder.locationSearchesData() != null) {
            holder.locationSearchesData().removeObservers(owner);
        }
        holder.locationSearchesData(data);
        data.observe(owner, this::handleLocationsSearchedData);
    }

    @VisibleForTesting(otherwise = PRIVATE)
    void handleWeatherData(@NonNull final Weather weather) {
        view.showWeather(weather);
    }

    @VisibleForTesting(otherwise = PRIVATE)
    void handleLocationsSearchedData(@NonNull final List<City> cities) {
        view.showLocationsSearched(cities);
    }
}
