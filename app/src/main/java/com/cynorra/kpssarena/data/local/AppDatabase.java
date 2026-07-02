package com.cynorra.kpssarena.data.local;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.cynorra.kpssarena.data.local.dao.MistakeDao;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.data.local.entity.MistakeEntity;
import com.cynorra.kpssarena.data.local.entity.UserCacheEntity;

@Database(
        entities = {
                MistakeEntity.class,
                UserCacheEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MistakeDao mistakeDao();
    public abstract UserCacheDao userCacheDao();
}
