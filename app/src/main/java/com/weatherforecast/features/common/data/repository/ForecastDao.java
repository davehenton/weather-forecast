package com.weatherforecast.features.common.data.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import com.weatherforecast.features.common.data.entity.ForecastEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final List<ForecastEntity> forecasts);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM forecast INNER JOIN city ON city.id = forecast.cityId WHERE city.id = :cityId")
    Flowable<List<ForecastEntity>> findForecastForCity(final long cityId);

}
