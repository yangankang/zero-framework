package com.yoosal.orm.dialect;

import com.yoosal.orm.annotation.DefaultValue;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.query.OrderBy;
import com.yoosal.orm.query.Wheres;

import java.util.*;

public class SQLChain {
    private static final int DEFAULT_LENGTH = 255;

    public SQLChain setChain(SQLChain chain) {
        if (chain != null) {
            this.commands.addAll(chain.commands);
        }
        return this;
    }

    private enum Command {
        CREATE, TABLE, IF, NOT, EXISTS, PRIMARY, KEY, AUTO_INCREMENT, NULL, DEFAULT,
        INDEX, ALTER, ADD, INSERT, INTO, VALUES, UPDATE, SET, WHERE, AND, OR, ENGINE,
        CHARSET, IN, LIKE, BY, ORDER, ASC, DESC, LIMIT, DELETE, FROM, SELECT, COUNT,
        ON, AS, LEFT, JOIN
    }

    private List commands = new ArrayList();

    public SQLChain setOperation(Wheres.Logic operation) {
        if (operation == Wheres.Logic.AND) {
            this.and();
        }
        if (operation == Wheres.Logic.OR) {
            this.or();
        }
        return this;
    }

    public SQLChain setOrderBy(OrderBy.Type orderBy) {
        if (orderBy == OrderBy.Type.ASC) {
            this.asc();
        }
        if (orderBy == OrderBy.Type.DESC) {
            this.desc();
        }
        return this;
    }

    public SQLChain select() {
        commands.add(Command.SELECT);
        return this;
    }

    public SQLChain on() {
        commands.add(Command.ON);
        return this;
    }

    public SQLChain count() {
        commands.add(Command.COUNT);
        return this;
    }

    public SQLChain delete() {
        commands.add(Command.DELETE);
        return this;
    }

    public SQLChain from() {
        commands.add(Command.FROM);
        return this;
    }

    public SQLChain left() {
        commands.add(Command.LEFT);
        return this;
    }

    public SQLChain join() {
        commands.add(Command.JOIN);
        return this;
    }

    public SQLChain limit() {
        commands.add(Command.LIMIT);
        return this;
    }

    public SQLChain asc() {
        commands.add(Command.ASC);
        return this;
    }

    public SQLChain desc() {
        commands.add(Command.DESC);
        return this;
    }

    public SQLChain by() {
        commands.add(Command.BY);
        return this;
    }

    public SQLChain order() {
        commands.add(Command.ORDER);
        return this;
    }

    public SQLChain charset() {
        commands.add(Command.CHARSET);
        return this;
    }

    public SQLChain like() {
        commands.add(Command.LIKE);
        return this;
    }

    public SQLChain in() {
        commands.add(Command.IN);
        return this;
    }

    public SQLChain engine() {
        commands.add(Command.ENGINE);
        return this;
    }

    public SQLChain where() {
        commands.add(Command.WHERE);
        return this;
    }

    public SQLChain and() {
        commands.add(Command.AND);
        return this;
    }

    public SQLChain or() {
        commands.add(Command.OR);
        return this;
    }

    public SQLChain set() {
        commands.add(Command.SET);
        return this;
    }

    public SQLChain update() {
        commands.add(Command.UPDATE);
        return this;
    }

    public SQLChain create() {
        commands.add(Command.CREATE);
        return this;
    }

    public SQLChain table() {
        commands.add(Command.TABLE);
        return this;
    }

    public SQLChain ifCommand() {
        commands.add(Command.IF);
        return this;
    }


    public SQLChain nullCommand() {
        commands.add(Command.NULL);
        return this;
    }

    public SQLChain not() {
        commands.add(Command.NOT);
        return this;
    }

    public SQLChain exists() {
        commands.add(Command.EXISTS);
        return this;
    }

    public SQLChain alter() {
        commands.add(Command.ALTER);
        return this;
    }

    public SQLChain as() {
        commands.add(Command.AS);
        return this;
    }

    public SQLChain setValue(Object value) {
        commands.add(String.valueOf(value));
        return this;
    }

    public SQLChain setQuestion() {
        commands.add("?");
        return this;
    }

    public SQLChain setValueList(List values) {
        for (int i = 0; i < values.size(); i++) {
            Object v = values.get(i);
            if (i >= (values.size() - 1)) {
                this.setValue(v);
            } else {
                this.setValue(v).setSplit();
            }
        }
        return this;
    }

    public SQLChain setValueMap(Map values, Wheres.Operation operation, Wheres.Logic logic) {
        Iterator iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            this.setValue(entry.getKey());
            commands.add(Wheres.getOperation(operation));
            this.setValue(entry.getValue());

            if (iterator.hasNext()) {
                if (logic == Wheres.Logic.AND) {
                    this.and();
                } else {
                    this.or();
                }
            }
        }
        return this;
    }

    public long size() {
        return commands.size();
    }

    public SQLChain setBegin() {
        commands.add("(");
        return this;
    }

    public SQLChain setEnd() {
        commands.add(")");
        return this;
    }

    public SQLChain setEquals() {
        commands.add("=");
        return this;
    }

    public SQLChain setALL() {
        commands.add("*");
        return this;
    }

    public SQLChain primary() {
        commands.add(Command.PRIMARY);
        return this;
    }

    public SQLChain key() {
        commands.add(Command.KEY);
        return this;
    }

    public SQLChain autoIncrement() {
        commands.add(Command.AUTO_INCREMENT);
        return this;
    }

    public SQLChain defaultCommand() {
        commands.add(Command.DEFAULT);
        return this;
    }

    public SQLChain index() {
        commands.add(Command.INDEX);
        return this;
    }

    public SQLChain add() {
        commands.add(Command.ADD);
        return this;
    }

    public SQLChain insert() {
        commands.add(Command.INSERT);
        return this;
    }

    public SQLChain into() {
        commands.add(Command.INTO);
        return this;
    }

    public SQLChain values() {
        commands.add(Command.VALUES);
        return this;
    }

    public SQLChain setSplit() {
        commands.add(",");
        return this;
    }

    public SQLChain setMark() {
        commands.add("'");
        return this;
    }

    public SQLChain setSpace() {
        commands.add(" ");
        return this;
    }

    public SQLChain matchColumn(ColumnModel cm, SQLDialect dialect, boolean isLast, boolean matchKey) {
        long len = cm.getLength();
        String columnName = cm.getColumnName();
        Class clazz = cm.getJavaType();
        String columnType = dialect.getType(clazz);
        boolean isPrimaryKey = cm.isPrimaryKey();
        boolean isAllowNull = cm.isAllowNull();
        DefaultValue defaultValue = cm.getDefaultValue();

        if (isPrimaryKey && cm.isAutoIncrement()) {
            columnType = dialect.getType(Integer.class);
        }


        if (len <= 0 && clazz.isAssignableFrom(String.class) && !isPrimaryKey) {
            len = DEFAULT_LENGTH;
        }
        this.setValue(columnName).setSpace();
        this.setValue(columnType);
        if (len > 0) {
            this.setBegin().setValue(len).setEnd();
        }

        if (matchKey) {
            if (cm.isPrimaryKey()) {
                this.primary().key();
            }

            if (cm.isKey()) {
                this.key();
            }
        }

        if (cm.isAutoIncrement()) {
            this.autoIncrement();
        }

        if (!isAllowNull) {
            this.not().nullCommand();
        }

        if (defaultValue != null && defaultValue.enable()) {
            if (clazz.equals(Integer.class)) {
                this.defaultCommand().setValue(defaultValue.intValue());
            }
            if (clazz.equals(String.class)) {
                this.defaultCommand().setMark().setValue(defaultValue.stringValue()).setMark();
            }
        }

        if (!isLast) {
            this.setSplit();
        }

        return this;
    }

    public SQLChain removeLastCommand() {
        commands.remove(commands.size() - 1);
        return this;
    }

    public SQLChain append(SQLChain chain) {
        List cm = chain.getCommands();
        this.commands.addAll(cm);
        return this;
    }

    public List getCommands() {
        return commands;
    }

    public String toString() {
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < commands.size(); i++) {
            Object object = commands.get(i);
            if (((object instanceof Command) || (i < commands.size() - 1 && commands.get(i + 1) instanceof Command))
                    && (i < commands.size() - 1 && commands.get(i + 1) != "(")) {
                sql.append(String.valueOf(object) + " ");
            } else {
                sql.append(String.valueOf(object));
            }
        }
        return sql.toString();
    }
}
