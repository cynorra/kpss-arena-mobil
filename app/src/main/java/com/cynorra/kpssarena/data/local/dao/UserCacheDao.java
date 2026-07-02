package com.cynorra.kpssarena.data.local.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.cynorra.kpssarena.data.local.entity.UserCacheEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
@Dao
public interface UserCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(UserCacheEntity user);
    @Query("SELECT * FROM user_cache WHERE uid = :uid")
    Single<UserCacheEntity> getUserByUid(String uid);
    @Query("DELETE FROM user_cache WHERE uid = :uid")
    Completable deleteUser(String uid);
    @Query("DELETE FROM user_cache")
    Completable clearAll();
}
