package com.sillador.strecs.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

public class CustomSQLiteDialect extends Dialect {

    public CustomSQLiteDialect() {
//        super(DatabaseVersion.make(3));
        registerColumnTypes();
    }

    private void registerColumnTypes() {
//        registerColumnType(SqlTypes.VARCHAR, "text");
//        registerColumnType(SqlTypes.INTEGER, "integer");
//        registerColumnType(SqlTypes.BIGINT, "bigint");
//        registerColumnType(SqlTypes.FLOAT, "float");
//        registerColumnType(SqlTypes.DOUBLE, "double");
//        registerColumnType(SqlTypes.BOOLEAN, "boolean");
//        registerColumnType(SqlTypes.DATE, "date");
//        registerColumnType(SqlTypes.TIMESTAMP, "datetime");
//        registerColumnType(SqlTypes.BLOB, "blob");
    }

//    @Override
//    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
//        super.initializeFunctionRegistry(functionContributions);
//
//        SqmFunctionRegistry registry = functionContributions.getFunctionRegistry();
//
//        // Register commonly used SQLite functions
//        registry.register("lower", new StandardSQLFunction("lower"));
//        registry.register("upper", new StandardSQLFunction("upper"));
//        registry.register("length", new StandardSQLFunction("length"));
//        registry.register("strftime", new StandardSQLFunction("strftime"));
//
//
//    }




    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }


    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);

//         Register correct JDBC type for INTEGER
        typeContributions.getTypeConfiguration()
                .getJdbcTypeRegistry()
                .addDescriptor(IntegerJdbcType.INSTANCE);
    }


//    @Override
//    public void initializeFunctionRegistry(SqmFunctionRegistry functionRegistry) {
//        super.initializeFunctionRegistry(functionRegistry);
//        functionRegistry.register("lower", new StandardSQLFunction("lower"));
//        functionRegistry.register("upper", new StandardSQLFunction("upper"));
//        functionRegistry.register("length", new StandardSQLFunction("length"));
//        functionRegistry.register("strftime", new StandardSQLFunction("strftime"));
//    }

//    @Override
//    public void contributeTypes(TypeContributions typeContributions, org.hibernate.service.ServiceRegistry serviceRegistry) {
//        BasicTypeRegistry basicTypeRegistry = typeContributions.getTypeConfiguration().getBasicTypeRegistry();
//        // Optional: register custom type mappings if needed
//        super.contributeTypes(typeContributions, serviceRegistry);
//    }




//    @Override
//    public LimitHandler getLimitHandler() {
//        return LimitOffsetLimitHandler.NO_LIMIT;
//    }


//
//    @Override
//    public String getDropForeignKeyString() {
//        return "1";
//    }
//
//    @Override
//    public String getAddForeignKeyConstraintString(String cn, String[] fk, String t, String[] pk, boolean r) {
//        return "1";
//    }
//
//    @Override
//    public String getAddPrimaryKeyConstraintString(String constraintName) {
//        return "1";
//    }
//


}
