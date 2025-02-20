package vn.tapbi.zazip.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

import vn.tapbi.zazip.data.model.Account;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM account ")
    List<Account> getAllAccount();

    @Query("SELECT * FROM account WHERE accountName= :nameAccount")
    Account getAccountByName(String nameAccount);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account accounts);

    @Query("DELETE FROM account WHERE id = :id")
    void deleteById(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateById(Account account);

    @Query("UPDATE account SET accountName=:accountName WHERE idAccount=:idAccount")
    void updateByIdAccount(String idAccount, String accountName);

    @Query("SELECT * FROM account WHERE type= :typeAccount")
    Account fetchAccountByType(String typeAccount);

    @Query("SELECT * FROM account WHERE idAccount=:idAccount")
    List<Account> getAccountByIdAccount(String idAccount);

}
