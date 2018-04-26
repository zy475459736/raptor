package com.ppdai.raptor.codegen2.java.util;

public class FieldExtendSpec {
    boolean constructorField = false;
    boolean generateGetterAndSetter = false;
    boolean partOfHashCode = true;

    public boolean isConstructorField() {
        return constructorField;
    }

    public void setConstructorField(boolean constructorField) {
        this.constructorField = constructorField;
    }

    public boolean isGenerateGetterAndSetter() {
        return generateGetterAndSetter;
    }

    public void setGenerateGetterAndSetter(boolean generateGetterAndSetter) {
        this.generateGetterAndSetter = generateGetterAndSetter;
    }

    public boolean isPartOfHashCode() {
        return partOfHashCode;
    }

    public void setPartOfHashCode(boolean partOfHashCode) {
        this.partOfHashCode = partOfHashCode;
    }

    public boolean isInitWithEmptyValue() {
        return initWithEmptyValue;
    }

    public void setInitWithEmptyValue(boolean initWithEmptyValue) {
        this.initWithEmptyValue = initWithEmptyValue;
    }

    boolean initWithEmptyValue = false;
}
