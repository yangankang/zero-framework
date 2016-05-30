package com.yoosal.orm.mapping;

public abstract class AbstractModelCheck implements ModelCheck {
    private WordConvert wordConvert;

    enum SuppertConvert {
        H2U
    }

    @Override
    public void setWordConvert(WordConvert convert) {
        this.wordConvert = convert;
    }

    @Override
    public void setWordConvert(String key) {
        if (key.equalsIgnoreCase(SuppertConvert.H2U.toString())) {
            this.wordConvert = new H2UConvert();
        }
    }

    @Override
    public String convert() {
        if (wordConvert == null) {
            return this.getName();
        }
        return wordConvert.convert(this.getName());
    }

    @Override
    public boolean compare(String columnOrTableName) {
        String convertName = this.getName();
        if (columnOrTableName.equalsIgnoreCase(convertName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean compareAndSet(String columnOrTableName) {
        boolean isTrue = this.compare(columnOrTableName);
        if (isTrue) {
            this.setMappingName(columnOrTableName);
            return true;
        }
        return false;
    }


    protected abstract String getName();

    protected abstract void setMappingName(String name);
}
