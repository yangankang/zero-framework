package com.yoosal.orm.transaction;

import com.yoosal.orm.core.SessionOperation;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManger {
    private static final Map<SessionOperation, Boolean> transactions = new ConcurrentHashMap<SessionOperation, Boolean>();

    public static Transaction getStandAloneTransaction(SessionOperation sessionOperation, Connection connection) {
        Boolean hasOne = transactions.get(sessionOperation);
        Transaction transaction = null;
        if (hasOne != null && hasOne) {
            transaction = new StandAloneTransaction(sessionOperation, connection, false);
        } else {
            transactions.put(sessionOperation, true);
            transaction = new StandAloneTransaction(sessionOperation, connection);
        }
        return transaction;
    }

    public static boolean isInBreakTransaction(SessionOperation sessionOperation) {
        return transactions.get(sessionOperation) == null ? false : transactions.get(sessionOperation);
    }

    public static void breakTransaction(SessionOperation sessionOperation) {
        transactions.remove(sessionOperation);
    }
}
