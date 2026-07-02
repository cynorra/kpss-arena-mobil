package com.cynorra.kpssarena.data.local.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.cynorra.kpssarena.data.local.entity.MistakeEntity;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MistakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMistake(MistakeEntity mistake);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMistakes(List<MistakeEntity> mistakes);
    @Update
    Completable updateMistake(MistakeEntity mistake);
    @Delete
    Completable deleteMistake(MistakeEntity mistake);
    @Query("SELECT * FROM mistakes ORDER BY created_at DESC")
    Flowable<List<MistakeEntity>> getAllMistakes();
    @Query("SELECT * FROM mistakes WHERE category = :category ORDER BY created_at 
    Flowable<List<MistakeEntity>> getMistakesByCategory(String category);
    @Query("SELECT * FROM mistakes WHERE is_retried = 0 ORDER BY created_at DESC")
    Flowable<List<MistakeEntity>> getUnretriedMistakes();
    @Query("SELECT * FROM mistakes WHERE question_id = :questionId")
    Single<MistakeEntity> getMistakeById(String questionId);
    @Query("SELECT COUNT(*) FROM mistakes")
    Single<Integer> getMistakeCount();
    @Query("SELECT COUNT(*) FROM mistakes WHERE category = :category")
    Single<Integer> getMistakeCountByCategory(String category);
    @Query("SELECT COUNT(*) FROM mistakes WHERE is_retried = 1 AND retry_correct = 
    Single<Integer> getCorrectedMistakeCount();
    @Query("UPDATE mistakes SET is_retried = 1, retry_correct = :isCorrect WHERE questi
    Completable markAsRetried(String questionId, boolean isCorrect);
    @Query("DELETE FROM mistakes")

    Completable deleteAllMistakes();
}
