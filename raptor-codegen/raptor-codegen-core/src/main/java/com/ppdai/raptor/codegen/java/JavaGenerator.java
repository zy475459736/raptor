/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ppdai.raptor.codegen.java;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.protobuf.WireFormat;
import com.ppdai.framework.raptor.annotation.RaptorField;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.raptor.codegen.java.option.InterfaceMetaInfo;
import com.ppdai.raptor.codegen.java.option.MessageMetaInfo;
import com.ppdai.raptor.codegen.java.option.Method;
import com.ppdai.raptor.codegen.java.option.MethodMetaInfo;
import com.ppdai.raptor.codegen.java.util.CaseFormatUtil;
import com.squareup.javapoet.*;
import com.squareup.wire.*;
import com.squareup.wire.ProtoAdapter.EnumConstantNotFoundException;
import com.squareup.wire.internal.Internal;
import com.squareup.wire.schema.*;
import okio.ByteString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.squareup.wire.schema.Options.*;
import static javax.lang.model.element.Modifier.*;

/**
 * Generates Java source code that matches proto definitions.
 * <p>
 * <p>This can map type names from protocol buffers (like {@code uint32}, {@code string}, or {@code
 * squareup.protos.person.Person} to the corresponding Java names (like {@code int}, {@code
 * java.lang.String}, or {@code com.squareup.protos.person.Person}).
 */
public final class JavaGenerator {
    static final ProtoMember FIELD_DEPRECATED = ProtoMember.get(FIELD_OPTIONS, "deprecated");
    static final ProtoMember ENUM_DEPRECATED = ProtoMember.get(ENUM_VALUE_OPTIONS, "deprecated");
    static final ProtoMember PACKED = ProtoMember.get(FIELD_OPTIONS, "packed");

    static final ClassName BYTE_STRING = ClassName.get(ByteString.class);
    static final ClassName STRING = ClassName.get(String.class);
    static final ClassName LIST = ClassName.get(List.class);
    static final ClassName ADAPTER = ClassName.get(ProtoAdapter.class);
    static final ClassName ENUM_ADAPTER = ClassName.get(EnumAdapter.class);
    static final ClassName NULLABLE = ClassName.get("android.support.annotation", "Nullable");

    private static final Ordering<Field> TAG_ORDERING = Ordering.from(new Comparator<Field>() {
        @Override
        public int compare(Field o1, Field o2) {
            return Integer.compare(o1.tag(), o2.tag());
        }
    });

    private static final Map<ProtoType, ClassName> BUILT_IN_TYPES_MAP =
            ImmutableMap.<ProtoType, ClassName>builder()
                    .put(ProtoType.BOOL, (ClassName) TypeName.BOOLEAN.box())
                    .put(ProtoType.BYTES, ClassName.get(ByteString.class))
                    .put(ProtoType.DOUBLE, (ClassName) TypeName.DOUBLE.box())
                    .put(ProtoType.FLOAT, (ClassName) TypeName.FLOAT.box())
                    .put(ProtoType.FIXED32, (ClassName) TypeName.INT.box())
                    .put(ProtoType.FIXED64, (ClassName) TypeName.LONG.box())
                    .put(ProtoType.INT32, (ClassName) TypeName.INT.box())
                    .put(ProtoType.INT64, (ClassName) TypeName.LONG.box())
                    .put(ProtoType.SFIXED32, (ClassName) TypeName.INT.box())
                    .put(ProtoType.SFIXED64, (ClassName) TypeName.LONG.box())
                    .put(ProtoType.SINT32, (ClassName) TypeName.INT.box())
                    .put(ProtoType.SINT64, (ClassName) TypeName.LONG.box())
                    .put(ProtoType.STRING, ClassName.get(String.class))
                    .put(ProtoType.UINT32, (ClassName) TypeName.INT.box())
                    .put(ProtoType.UINT64, (ClassName) TypeName.LONG.box())
                    .put(FIELD_OPTIONS, ClassName.get("com.google.protobuf", "MessageOptions"))
                    .put(ENUM_OPTIONS, ClassName.get("com.google.protobuf", "FieldOptions"))
                    .put(MESSAGE_OPTIONS, ClassName.get("com.google.protobuf", "EnumOptions"))
                    .build();

    private static final Map<ProtoType, WireFormat.FieldType> FIELD_TYPE_MAP =
            ImmutableMap.<ProtoType, WireFormat.FieldType>builder()
                    .put(ProtoType.DOUBLE, WireFormat.FieldType.DOUBLE)
                    .put(ProtoType.FLOAT, WireFormat.FieldType.FLOAT)
                    .put(ProtoType.INT64, WireFormat.FieldType.INT64)
                    .put(ProtoType.UINT64, WireFormat.FieldType.UINT64)
                    .put(ProtoType.INT32, WireFormat.FieldType.INT32)
                    .put(ProtoType.FIXED64, WireFormat.FieldType.FIXED64)
                    .put(ProtoType.FIXED32, WireFormat.FieldType.FIXED32)
                    .put(ProtoType.BOOL, WireFormat.FieldType.BOOL)
                    .put(ProtoType.STRING, WireFormat.FieldType.STRING)
//                    .put(ProtoType.GROUP, WireFormat.FieldType.GROUP)  //not support group
                    .put(ProtoType.BYTES, WireFormat.FieldType.BYTES)
                    .put(ProtoType.UINT32, WireFormat.FieldType.UINT32)
                    .put(ProtoType.SFIXED32, WireFormat.FieldType.SFIXED32)
                    .put(ProtoType.SFIXED64, WireFormat.FieldType.SFIXED64)
                    .put(ProtoType.SINT32, WireFormat.FieldType.SINT32)
                    .put(ProtoType.SINT64, WireFormat.FieldType.SINT64)
                    .build();


    private static final String URL_CHARS = "[-!#$%&'()*+,./0-9:;=?@A-Z\\[\\]_a-z~]";
    private final Schema schema;
    private final ImmutableMap<ProtoType, ClassName> nameToJavaName;
    private final Profile profile;
    private final boolean emitAndroid;
    /**
     * Preallocate all of the names we'll need for {@code type}. Names are allocated in precedence
     * order, so names we're stuck with (serialVersionUID etc.) occur before proto field names are
     * assigned.
     * <p>
     * <p>Name allocations are computed once and reused because some types may be needed when
     * generating other types.
     */
    private final LoadingCache<MessageType, NameAllocator> nameAllocators
            = CacheBuilder.newBuilder().build(new CacheLoader<MessageType, NameAllocator>() {
        @Override
        public NameAllocator load(MessageType type) throws Exception {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("serialVersionUID", "serialVersionUID");
            nameAllocator.newName("ADAPTER", "ADAPTER");
            nameAllocator.newName("MESSAGE_OPTIONS", "MESSAGE_OPTIONS");
            if (emitAndroid) {
                nameAllocator.newName("CREATOR", "CREATOR");
            }
            Set<String> collidingNames = collidingFieldNames(type.fieldsAndOneOfFields());
            for (Field field : type.fieldsAndOneOfFields()) {
                String suggestion = collidingNames.contains(field.name())
                        ? field.qualifiedName()
                        : field.name();
                nameAllocator.newName(CaseFormatUtil.determineFormat(suggestion).to(LOWER_CAMEL, suggestion), field);
            }
            return nameAllocator;
        }
    });
    private final boolean emitCompact;

    private JavaGenerator(Schema schema, Map<ProtoType, ClassName> nameToJavaName, Profile profile,
                          boolean emitAndroid, boolean emitCompact) {
        this.schema = schema;
        this.nameToJavaName = ImmutableMap.copyOf(nameToJavaName);
        this.profile = profile;
        this.emitAndroid = emitAndroid;
        this.emitCompact = emitCompact;
    }

    public static JavaGenerator get(Schema schema) {
        Map<ProtoType, ClassName> nameToJavaName = new LinkedHashMap<>();
        nameToJavaName.putAll(BUILT_IN_TYPES_MAP);

        for (ProtoFile protoFile : schema.protoFiles()) {
            String javaPackage = javaPackage(protoFile);
            putAll(nameToJavaName, javaPackage, null, protoFile.types());

            for (Service service : protoFile.services()) {
                ClassName className = ClassName.get(javaPackage, service.type().simpleName());
                nameToJavaName.put(service.type(), className);
            }
        }

        return new JavaGenerator(schema, nameToJavaName, new Profile(), false, false);
    }

    private static void putAll(Map<ProtoType, ClassName> wireToJava, String javaPackage,
                               ClassName enclosingClassName, List<Type> types) {
        for (Type type : types) {
            ClassName className = enclosingClassName != null
                    ? enclosingClassName.nestedClass(type.type().simpleName())
                    : ClassName.get(javaPackage, type.type().simpleName());
            wireToJava.put(type.type(), className);
            putAll(wireToJava, javaPackage, className, type.nestedTypes());
        }
    }

    private static String javaPackage(ProtoFile protoFile) {
        String javaPackage = protoFile.javaPackage();
        if (javaPackage != null) {
            return javaPackage;
        } else if (protoFile.packageName() != null) {
            return protoFile.packageName();
        } else {
            return "";
        }
    }

    static TypeName listOf(TypeName type) {
        return ParameterizedTypeName.get(LIST, type);
    }

    static TypeName adapterOf(TypeName messageType) {
        return ParameterizedTypeName.get(ADAPTER, messageType);
    }

    /**
     * A grab-bag of fixes for things that can go wrong when converting to javadoc.
     */
    static String sanitizeJavadoc(String documentation) {
        // Remove trailing whitespace on each line.
        documentation = documentation.replaceAll("[^\\S\n]+\n", "\n");
        documentation = documentation.replaceAll("\\s+$", "");
        documentation = documentation.replaceAll("\\*/", "&#42;/");
        // Rewrite '@see <url>' to use an html anchor tag
        documentation = documentation.replaceAll(
                "@see (http:" + URL_CHARS + "+)", "@see <a href=\"$1\">$1</a>");
        return documentation;
    }

    public JavaGenerator withProfile(Profile profile) {
        return new JavaGenerator(schema, nameToJavaName, profile, emitAndroid, emitCompact);
    }

    public Schema schema() {
        return schema;
    }

    /**
     * Returns the Java type for {@code protoType}.
     *
     * @throws IllegalArgumentException if there is no known Java type for {@code protoType}, such as
     *                                  if that type wasn't in this generator's schema.
     */
    public TypeName typeName(ProtoType protoType) {
        TypeName profileJavaName = profile.getTarget(protoType);
        if (profileJavaName != null) return profileJavaName;
        TypeName candidate = nameToJavaName.get(protoType);
        checkArgument(candidate != null, "unexpected type %s", protoType);
        return candidate;
    }

    /**
     * Returns the Java type of the abstract adapter class generated for a corresponding {@code
     * protoType}. Returns null if {@code protoType} is not using a custom proto adapter.
     */
    public ClassName abstractAdapterName(ProtoType protoType) {
        TypeName profileJavaName = profile.getTarget(protoType);
        if (profileJavaName == null) return null;

        ClassName javaName = nameToJavaName.get(protoType);
        return javaName.peerClass("Abstract" + javaName.simpleName() + "Adapter");
    }

    private CodeBlock singleAdapterFor(Field field) {
        return field.type().isMap()
                ? CodeBlock.of("$N", field.name())
                : singleAdapterFor(field.type());
    }

    private CodeBlock singleAdapterFor(ProtoType type) {
        CodeBlock.Builder result = CodeBlock.builder();
        if (type.isScalar()) {
            result.add("$T.$L", ADAPTER, type.simpleName().toUpperCase(Locale.US));
        } else if (type.isMap()) {
            throw new IllegalArgumentException("Cannot create single adapter for map type " + type);
        } else {
            AdapterConstant adapterConstant = profile.getAdapter(type);
            if (adapterConstant != null) {
                result.add("$T.$L", adapterConstant.className, adapterConstant.memberName);
            } else {
                result.add("$T.ADAPTER", typeName(type));
            }
        }
        return result.build();
    }

    private CodeBlock adapterFor(Field field) {
        CodeBlock.Builder result = singleAdapterFor(field).toBuilder();
        if (field.isPacked()) {
            result.add(".asPacked()");
        } else if (field.isRepeated()) {
            result.add(".asRepeated()");
        }
        return result.build();
    }

    public boolean isEnum(ProtoType type) {
        return schema.getType(type) instanceof EnumType;
    }

    EnumConstant enumDefault(ProtoType type) {
        EnumType wireEnum = (EnumType) schema.getType(type);
        return wireEnum.constants().get(0);
    }

    /**
     * Returns the full name of the class generated for {@code type}.
     */
    public ClassName generatedTypeName(Type type) {
        ClassName abstractAdapterName = abstractAdapterName(type.type());
        return abstractAdapterName != null
                ? abstractAdapterName
                : (ClassName) typeName(type.type());
    }

    /**
     * Returns the generated code for {@code type}, which may be a top-level or a nested type.
     */
    public TypeSpec generateType(ProtoFile protoFile, Type type) {
        if (type instanceof MessageType) {
            AdapterConstant adapterConstant = profile.getAdapter(type.type());
            if (adapterConstant != null) {
                return generateAbstractAdapter((MessageType) type);
            }
            //noinspection deprecation: Only deprecated as a public API.
            return generateMessage(protoFile, (MessageType) type);
        }
        if (type instanceof EnumType) {
            //noinspection deprecation: Only deprecated as a public API.
            return generateEnum((EnumType) type);
        }
        if (type instanceof EnclosingType) {
            return generateEnclosingType(protoFile, (EnclosingType) type);
        }
        throw new IllegalStateException("Unknown type: " + type);
    }

    /**
     * @deprecated Use {@link #generateType)}
     */
    @Deprecated
    public TypeSpec generateEnum(EnumType type) {
        ClassName javaType = (ClassName) typeName(type.type());

        TypeSpec.Builder builder = TypeSpec.enumBuilder(javaType.simpleName())
                .addModifiers(PUBLIC)
                .addSuperinterface(WireEnum.class);

        if (!type.documentation().isEmpty()) {
            builder.addJavadoc("$L\n", sanitizeJavadoc(type.documentation()));
        }

        // Output Private tag field
        builder.addField(TypeName.INT, "value", PRIVATE, FINAL);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        constructorBuilder.addStatement("this.value = value");
        constructorBuilder.addParameter(TypeName.INT, "value");

        // Enum constant options, each of which requires a constructor parameter and a field.
        Set<ProtoMember> allOptionFieldsBuilder = new LinkedHashSet<>();
        for (EnumConstant constant : type.constants()) {
            for (ProtoMember protoMember : constant.options().map().keySet()) {
                Field optionField = schema.getField(protoMember);
                if (allOptionFieldsBuilder.add(protoMember)) {
                    TypeName optionJavaType = typeName(optionField.type());
                    builder.addField(optionJavaType, optionField.name(), PUBLIC, FINAL);
                    constructorBuilder.addParameter(optionJavaType, optionField.name());
                    constructorBuilder.addStatement("this.$L = $L", optionField.name(), optionField.name());
                }
            }
        }
        ImmutableList<ProtoMember> allOptionMembers = ImmutableList.copyOf(allOptionFieldsBuilder);
        String enumArgsFormat = "$L" + Strings.repeat(", $L", allOptionMembers.size());
        builder.addMethod(constructorBuilder.build());

        MethodSpec.Builder fromValueBuilder = MethodSpec.methodBuilder("fromValue")
                .addJavadoc("Return the constant for {@code value} or null.\n")
                .addModifiers(PUBLIC, STATIC)
                .returns(javaType)
                .addParameter(int.class, "value")
                .beginControlFlow("switch (value)");

        Set<Integer> seenTags = new LinkedHashSet<>();
        for (EnumConstant constant : type.constants()) {
            Object[] enumArgs = new Object[allOptionMembers.size() + 1];
            enumArgs[0] = constant.tag();
            for (int i = 0; i < allOptionMembers.size(); i++) {
                ProtoMember protoMember = allOptionMembers.get(i);
                Field field = schema.getField(protoMember);
                Object value = constant.options().map().get(protoMember);
                enumArgs[i + 1] = value != null
                        ? fieldInitializer(field.type(), value)
                        : null;
            }

            TypeSpec.Builder constantBuilder = TypeSpec.anonymousClassBuilder(enumArgsFormat, enumArgs);
            if (!constant.documentation().isEmpty()) {
                constantBuilder.addJavadoc("$L\n", sanitizeJavadoc(constant.documentation()));
            }

            if ("true".equals(constant.options().get(ENUM_DEPRECATED))) {
                constantBuilder.addAnnotation(Deprecated.class);
            }

            builder.addEnumConstant(constant.name(), constantBuilder.build());

            // Ensure constant case tags are unique, which might not be the case if allow_alias is true.
            if (seenTags.add(constant.tag())) {
                fromValueBuilder.addStatement("case $L: return $L", constant.tag(), constant.name());
            }
        }

        builder.addMethod(fromValueBuilder.addStatement("default: return null")
                .endControlFlow()
                .build());

        // Enum type options.
        FieldSpec options = optionsField(ENUM_OPTIONS, "ENUM_OPTIONS", type.options());
        if (options != null) {
            builder.addField(options);
        }

        // Public Getter
        builder.addMethod(MethodSpec.methodBuilder("getValue")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(TypeName.INT)
                .addStatement("return value")
                .build());

        return builder.build();
    }

    /**
     * @deprecated Use {@link #generateType(ProtoFile, Type)}
     */
    @Deprecated
    public TypeSpec generateMessage(ProtoFile protoFile, MessageType type) {
        NameAllocator nameAllocator = nameAllocators.getUnchecked(type);

        ClassName javaType = (ClassName) typeName(type.type());

        TypeSpec.Builder builder = TypeSpec.classBuilder(javaType.simpleName());
        builder.addModifiers(PUBLIC, FINAL);

        AnnotationSpec raptorMessage = MessageMetaInfo.readFrom(protoFile, type).generateMessageSpec();
        builder.addAnnotation(raptorMessage);

        if (javaType.enclosingClassName() != null) {
            builder.addModifiers(STATIC);
        }

        if (!type.documentation().isEmpty()) {
            builder.addJavadoc("$L\n", sanitizeJavadoc(type.documentation()));
        }

        builder.addField(FieldSpec.builder(TypeName.LONG, nameAllocator.get("serialVersionUID"))
                .addModifiers(PRIVATE, STATIC, FINAL)
                .initializer("$LL", 0L)
                .build());

        FieldSpec messageOptions = optionsField(
                MESSAGE_OPTIONS, nameAllocator.get("MESSAGE_OPTIONS"), type.options());
        if (messageOptions != null) {
            builder.addField(messageOptions);
        }

        for (Field field : type.fieldsAndOneOfFields()) {
            String fieldName = nameAllocator.get(field);
            String optionsFieldName = "FIELD_OPTIONS_" + fieldName.toUpperCase(Locale.US);
            FieldSpec fieldOptions = optionsField(FIELD_OPTIONS, optionsFieldName, field.options());
            if (fieldOptions != null) {
                builder.addField(fieldOptions);
            }
        }

        for (Field field : type.fieldsAndOneOfFields()) {
            TypeName fieldJavaType = fieldType(field);

            if ((field.type().isScalar() || isEnum(field.type()))
                    && !field.isRepeated()
                    && !field.isPacked()) {
                builder.addField(defaultField(nameAllocator, field, fieldJavaType));
            }

            String fieldName = nameAllocator.get(field);
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(fieldJavaType, fieldName, PRIVATE);
            fieldBuilder.addAnnotation(wireFieldAnnotation(field, type.oneOfs()));
            if (!field.documentation().isEmpty()) {
                fieldBuilder.addJavadoc("$L\n", sanitizeJavadoc(field.documentation()));
            }
            if (field.isExtension()) {
                fieldBuilder.addJavadoc("Extension source: $L\n", field.location().withPathOnly());
            }
            if (field.isDeprecated()) {
                fieldBuilder.addAnnotation(Deprecated.class);
            }
            if (emitAndroid && field.isOptional()) {
                fieldBuilder.addAnnotation(NULLABLE);
            }
            builder.addField(fieldBuilder.build());
            builder.addMethod(buildGetMethod(fieldJavaType, fieldName));
            builder.addMethod(buildSetMethod(fieldJavaType, fieldName));
        }

        //防止产生两个无参构造函数
        if (CollectionUtils.isNotEmpty(type.fieldsAndOneOfFields())) {
            builder.addMethod(noArgumentConstructor());
        }
        builder.addMethod(messageFieldsAndUnknownFieldsConstructor(nameAllocator, type));

        if (!emitCompact) {
            builder.addMethod(messageToString(nameAllocator, type));
        }

        for (Type nestedType : type.nestedTypes()) {
            builder.addType(generateType(protoFile, nestedType));
        }

        return builder.build();
    }

    private MethodSpec noArgumentConstructor() {
        MethodSpec.Builder result = MethodSpec.constructorBuilder();
        result.addModifiers(PUBLIC);
        return result.build();
    }

    private MethodSpec buildSetMethod(TypeName fieldJavaType, String fieldName) {
        return MethodSpec.methodBuilder("set" + LOWER_CAMEL.to(UPPER_CAMEL, fieldName))
                .addModifiers(PUBLIC)
                .addParameter(fieldJavaType, fieldName)
                .addStatement("this.$L=$L", fieldName, fieldName)
                .build();
    }

    private MethodSpec buildGetMethod(TypeName fieldJavaType, String fieldName) {
        return MethodSpec.methodBuilder("get" + LOWER_CAMEL.to(UPPER_CAMEL, fieldName))
                .addModifiers(PUBLIC)
                .returns(fieldJavaType)
                .addStatement("return this.$L", fieldName).build();
    }

    private TypeSpec generateEnclosingType(ProtoFile protoFile, EnclosingType type) {
        ClassName javaType = (ClassName) typeName(type.type());

        TypeSpec.Builder builder = TypeSpec.classBuilder(javaType.simpleName())
                .addModifiers(PUBLIC, FINAL);
        if (javaType.enclosingClassName() != null) {
            builder.addModifiers(STATIC);
        }

        String documentation = type.documentation();
        if (!documentation.isEmpty()) {
            documentation += "\n\n<p>";
        }
        documentation += "<b>NOTE:</b> This type only exists to maintain class structure"
                + " for its nested types and is not an actual message.\n";
        builder.addJavadoc(documentation);

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE)
                .addStatement("throw new $T()", AssertionError.class)
                .build());

        for (Type nestedType : type.nestedTypes()) {
            builder.addType(generateType(protoFile, nestedType));
        }

        return builder.build();
    }

    /**
     * Returns an abstract adapter for {@code type}.
     */
    public TypeSpec generateAbstractAdapter(MessageType type) {
        NameAllocator nameAllocator = nameAllocators.getUnchecked(type);
        ClassName adapterTypeName = abstractAdapterName(type.type());
        ClassName typeName = (ClassName) typeName(type.type());
        TypeSpec.Builder adapter = messageAdapter(
                nameAllocator, type, typeName, adapterTypeName, null).toBuilder();

        if (adapterTypeName.enclosingClassName() != null) {
            adapter.addModifiers(STATIC);
        }

        for (Type nestedType : type.nestedTypes()) {
            if (profile.getAdapter(nestedType.type()) == null) {
                throw new IllegalArgumentException("Missing custom proto adapter for "
                        + nestedType.type().enclosingTypeOrPackage() + "." + nestedType.type().simpleName()
                        + " when enclosing proto has custom proto adapter.");
            }
            if (nestedType instanceof MessageType) {
                adapter.addType(generateAbstractAdapter((MessageType) nestedType));
            }
        }

        return adapter.build();
    }

    /**
     * Returns the set of names that are not unique within {@code fields}.
     */
    private Set<String> collidingFieldNames(ImmutableList<Field> fields) {
        Set<String> fieldNames = new LinkedHashSet<>();
        Set<String> collidingNames = new LinkedHashSet<>();
        for (Field field : fields) {
            if (!fieldNames.add(field.name())) {
                collidingNames.add(field.name());
            }
        }
        return collidingNames;
    }

    private TypeSpec messageAdapter(NameAllocator nameAllocator, MessageType type, ClassName javaType,
                                    ClassName adapterJavaType, ClassName builderType) {
        boolean useBuilder = builderType != null;
        TypeSpec.Builder adapter = TypeSpec.classBuilder(adapterJavaType.simpleName())
                .superclass(adapterOf(javaType));

        if (useBuilder) {
            adapter.addModifiers(PRIVATE, STATIC, FINAL);
        } else {
            adapter.addModifiers(PUBLIC, ABSTRACT);
        }

        adapter.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super($T.LENGTH_DELIMITED, $T.class)", FieldEncoding.class, javaType)
                .build());

        if (!useBuilder) {
            MethodSpec.Builder fromProto = MethodSpec.methodBuilder("fromProto")
                    .addModifiers(PUBLIC, ABSTRACT)
                    .returns(javaType);

            for (Field field : type.fieldsAndOneOfFields()) {
                TypeName fieldType = fieldType(field);
                String fieldName = nameAllocator.get(field);
                fromProto.addParameter(fieldType, fieldName);
                adapter.addMethod(MethodSpec.methodBuilder(fieldName)
                        .addModifiers(PUBLIC, ABSTRACT)
                        .addParameter(javaType, "value")
                        .returns(fieldType)
                        .build());
            }

            adapter.addMethod(fromProto.build());
        }

        adapter.addMethod(messageAdapterEncodedSize(nameAllocator, type, javaType, useBuilder));
        adapter.addMethod(messageAdapterEncode(nameAllocator, type, javaType, useBuilder));
        adapter.addMethod(messageAdapterDecode(nameAllocator, type, javaType, useBuilder, builderType));
        adapter.addMethod(messageAdapterRedact(nameAllocator, type, javaType, useBuilder, builderType));

        for (Field field : type.fieldsAndOneOfFields()) {
            if (field.type().isMap()) {
                TypeName adapterType = adapterOf(fieldType(field));
                adapter.addField(FieldSpec.builder(adapterType, field.name(), PRIVATE, FINAL)
                        .initializer("$T.newMapAdapter($L, $L)", ADAPTER,
                                singleAdapterFor(field.type().keyType()),
                                singleAdapterFor(field.type().valueType()))
                        .build());
            }
        }

        return adapter.build();
    }

    private MethodSpec messageAdapterEncodedSize(NameAllocator nameAllocator, MessageType type,
                                                 TypeName javaType, boolean useBuilder) {
        MethodSpec.Builder result = MethodSpec.methodBuilder("encodedSize")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(javaType, "value");

        result.addCode("$[");
        String leading = "return";
        for (Field field : type.fieldsAndOneOfFields()) {
            int fieldTag = field.tag();
            String fieldName = nameAllocator.get(field);
            CodeBlock adapter = adapterFor(field);
            result.addCode("$L $L.encodedSizeWithTag($L, ", leading, adapter, fieldTag)
                    .addCode((useBuilder ? "value.$L" : "$L(value)"), fieldName)
                    .addCode(")");
            leading = "\n+";
        }
        if (useBuilder) {
            result.addCode("$L value.unknownFields().size()", leading);
        }
        result.addCode(";$]\n", leading);

        return result.build();
    }

    private MethodSpec messageAdapterEncode(NameAllocator nameAllocator, MessageType type,
                                            TypeName javaType, boolean useBuilder) {
        MethodSpec.Builder result = MethodSpec.methodBuilder("encode")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(ProtoWriter.class, "writer")
                .addParameter(javaType, "value")
                .addException(IOException.class);

        for (Field field : type.fieldsAndOneOfFields()) {
            int fieldTag = field.tag();
            CodeBlock adapter = adapterFor(field);
            result.addCode("$L.encodeWithTag(writer, $L, ", adapter, fieldTag)
                    .addCode((useBuilder ? "value.$L" : "$L(value)"), nameAllocator.get(field))
                    .addCode(");\n");
        }

        if (useBuilder) {
            result.addStatement("writer.writeBytes(value.unknownFields())");
        }

        return result.build();
    }

    private MethodSpec messageAdapterDecode(NameAllocator nameAllocator, MessageType type,
                                            TypeName javaType, boolean useBuilder, ClassName builderJavaType) {
        MethodSpec.Builder result = MethodSpec.methodBuilder("decode")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(javaType)
                .addParameter(ProtoReader.class, "reader")
                .addException(IOException.class);

        List<Field> fields = TAG_ORDERING.sortedCopy(type.fieldsAndOneOfFields());

        if (useBuilder) {
            result.addStatement("$1T builder = new $1T()", builderJavaType);
        } else {
            for (Field field : fields) {
                result.addStatement("$T $N = $L",
                        fieldType(field), nameAllocator.get(field), initialValue(field));
            }
        }

        result.addStatement("long token = reader.beginMessage()");
        result.beginControlFlow("for (int tag; (tag = reader.nextTag()) != -1;)");
        result.beginControlFlow("switch (tag)");

        for (Field field : fields) {
            int fieldTag = field.tag();

            if (isEnum(field.type())) {
                result.beginControlFlow("case $L:", fieldTag);
                result.beginControlFlow("try");
                result.addCode(decodeAndAssign(field, nameAllocator, useBuilder));
                result.addCode(";\n");
                if (useBuilder) {
                    result.nextControlFlow("catch ($T e)", EnumConstantNotFoundException.class);
                    result.addStatement("builder.addUnknownField(tag, $T.VARINT, (long) e.value)",
                            FieldEncoding.class);
                    result.endControlFlow(); // try/catch
                } else {
                    result.nextControlFlow("catch ($T ignored)", EnumConstantNotFoundException.class);
                    result.endControlFlow(); // try/catch
                }
                result.addStatement("break");
                result.endControlFlow(); // case
            } else {
                result.addCode("case $L: $L; break;\n",
                        fieldTag, decodeAndAssign(field, nameAllocator, useBuilder));
            }
        }

        result.beginControlFlow("default:");
        if (useBuilder) {
            result.addStatement("$T fieldEncoding = reader.peekFieldEncoding()", FieldEncoding.class)
                    .addStatement("$T value = fieldEncoding.rawProtoAdapter().decode(reader)", Object.class)
                    .addStatement("builder.addUnknownField(tag, fieldEncoding, value)");
        } else {
            result.addStatement("reader.skip()");
        }
        result.endControlFlow(); // default

        result.endControlFlow(); // switch
        result.endControlFlow(); // for
        result.addStatement("reader.endMessage(token)");

        if (useBuilder) {
            result.addStatement("return builder.build()");
        } else {
            result.addCode("return fromProto(");
            boolean first = true;
            for (Field field : type.fieldsAndOneOfFields()) {
                if (!first) result.addCode(", ");
                result.addCode("$N", nameAllocator.get(field));
                first = false;
            }
            result.addCode(");\n");
        }

        return result.build();
    }

    private CodeBlock decodeAndAssign(Field field, NameAllocator nameAllocator, boolean useBuilder) {
        String fieldName = nameAllocator.get(field);
        CodeBlock decode = CodeBlock.of("$L.decode(reader)", singleAdapterFor(field));
        if (field.isRepeated()) {
            return useBuilder
                    ? CodeBlock.of("builder.$L.add($L)", fieldName, decode)
                    : CodeBlock.of("$L.add($L)", fieldName, decode);
        } else if (field.type().isMap()) {
            return useBuilder
                    ? CodeBlock.of("builder.$L.putAll($L)", fieldName, decode)
                    : CodeBlock.of("$L.putAll($L)", fieldName, decode);
        } else {
            return useBuilder
                    ? CodeBlock.of("builder.$L($L)", fieldName, decode)
                    : CodeBlock.of("$L = $L", fieldName, decode);
        }
    }

    private MethodSpec messageAdapterRedact(NameAllocator nameAllocator, MessageType type,
                                            ClassName javaType, boolean useBuilder, ClassName builderJavaType) {
        MethodSpec.Builder result = MethodSpec.methodBuilder("redact")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(javaType)
                .addParameter(javaType, "value");

        int redactedFieldCount = 0;
        List<String> requiredRedacted = new ArrayList<>();
        for (Field field : type.fieldsAndOneOfFields()) {
            if (field.isRedacted()) {
                redactedFieldCount++;
                if (field.isRequired()) {
                    requiredRedacted.add(nameAllocator.get(field));
                }
            }
        }

        if (!useBuilder) {
            result.addStatement((redactedFieldCount == 0) ? "return value" : "return null");
            return result.build();
        }

        if (!requiredRedacted.isEmpty()) {
            boolean isPlural = requiredRedacted.size() != 1;
            result.addStatement("throw new $T($S)", UnsupportedOperationException.class,
                    (isPlural ? "Fields" : "Field") + " '" + Joiner.on("', '").join(requiredRedacted) + "' "
                            + (isPlural ? "are" : "is") + " required and cannot be redacted.");
            return result.build();
        }

        result.addStatement("$1T builder = value.newBuilder()", builderJavaType);

        for (Field field : type.fieldsAndOneOfFields()) {
            String fieldName = nameAllocator.get(field);
            if (field.isRedacted()) {
                if (field.isRepeated()) {
                    result.addStatement("builder.$N = $T.emptyList()", fieldName, Collections.class);
                } else if (field.type().isMap()) {
                    result.addStatement("builder.$N = $T.emptyMap()", fieldName, Collections.class);
                } else {
                    result.addStatement("builder.$N = null", fieldName);
                }
            } else if (!field.type().isScalar() && !isEnum(field.type())) {
                if (field.isRepeated()) {
                    CodeBlock adapter = singleAdapterFor(field);
                    result.addStatement("$T.redactElements(builder.$N, $L)", Internal.class, fieldName,
                            adapter);
                } else if (field.type().isMap()) {
                    // We only need to ask the values to redact themselves if the type is a message.
                    if (!field.type().valueType().isScalar() && !isEnum(field.type().valueType())) {
                        CodeBlock adapter = singleAdapterFor(field.type().valueType());
                        result.addStatement("$T.redactElements(builder.$N, $L)", Internal.class, fieldName,
                                adapter);
                    }
                } else {
                    CodeBlock adapter = adapterFor(field);
                    if (!field.isRequired()) {
                        result.addCode("if (builder.$N != null) ", fieldName);
                    }
                    result.addStatement("builder.$1N = $2L.redact(builder.$1N)", fieldName, adapter);
                }
            }
        }

        result.addStatement("builder.clearUnknownFields()");

        result.addStatement("return builder.build()");
        return result.build();
    }

    // Example:
    //
    // public static final FieldOptions FIELD_OPTIONS_FOO = new FieldOptions.Builder()
    //     .setExtension(Ext_custom_options.count, 42)
    //     .build();
    //
    private FieldSpec optionsField(ProtoType optionsType, String fieldName, Options options) {
        TypeName optionsJavaType = typeName(optionsType);

        CodeBlock.Builder initializer = CodeBlock.builder();
        initializer.add("$[new $T.Builder()", optionsJavaType);

        boolean empty = true;
        for (Map.Entry<ProtoMember, ?> entry : options.map().entrySet()) {
            if (entry.getKey().equals(FIELD_DEPRECATED) || entry.getKey().equals(PACKED)) {
                continue;
            }

            Field optionField = schema.getField(entry.getKey());
            initializer.add("\n.$L($L)", fieldName(optionsType, optionField),
                    fieldInitializer(optionField.type(), entry.getValue()));
            empty = false;
        }
        initializer.add("\n.build()$]");
        if (empty) return null;

        return FieldSpec.builder(optionsJavaType, fieldName)
                .addModifiers(PUBLIC, STATIC, FINAL)
                .initializer(initializer.build())
                .build();
    }

    private String fieldName(ProtoType type, Field field) {
        MessageType messageType = (MessageType) schema.getType(type);
        NameAllocator names = nameAllocators.getUnchecked(messageType);
        return names.get(field);
    }

    private TypeName fieldType(Field field) {
        ProtoType type = field.type();
        if (type.isMap()) {
            return ParameterizedTypeName.get(ClassName.get(Map.class),
                    typeName(type.keyType()),
                    typeName(type.valueType()));
        }
        TypeName messageType = typeName(type);
        if (messageType.equals(BYTE_STRING)) {
            messageType = ArrayTypeName.of(byte.class);
        }
        return field.isRepeated() ? listOf(messageType) : messageType;
    }

    // Example:
    //
    // public static final Integer DEFAULT_OPT_INT32 = 123;
    //
    private FieldSpec defaultField(NameAllocator nameAllocator, Field field, TypeName fieldType) {
        String defaultFieldName = "DEFAULT_" + nameAllocator.get(field).toUpperCase(Locale.US);
        return FieldSpec.builder(fieldType, defaultFieldName, PUBLIC, STATIC, FINAL)
                .initializer(defaultValue(field))
                .build();
    }

    // Example:
    //
    // @WireField(
    //   tag = 1,
    //   type = INT32
    // )
    //
    private AnnotationSpec wireFieldAnnotation(Field field, ImmutableList<OneOf> oneOVES) {
        AnnotationSpec.Builder result = AnnotationSpec.builder(RaptorField.class);

        ProtoType type = field.type();
        WireFormat.FieldType wireType = getWireType(type);
        result.addMember("fieldType", "$T.$L", WireFormat.FieldType.class, wireType);
        if (type.isMap()) {
            result.addMember("keyType", "$T.$L", WireFormat.FieldType.class, getWireType(type.keyType()));
        }

        int tag = field.tag();
        result.addMember("order", String.valueOf(tag));
        result.addMember("name", "$S", field.name());

        if (type.isMap()) {
            result.addMember("isMap", "true");
        }

        if (field.isRepeated()) {
            result.addMember("repeated", "true");
        }

        for (OneOf oneOF : oneOVES) {
            if (oneOF.fields().contains(field)) {
                result.addMember("oneof", "$S", oneOF.name());
            }
        }

        return result.build();
    }

    private WireFormat.FieldType getWireType(ProtoType type) {
        checkNotNull(type);
        if (type.isScalar()) {
            WireFormat.FieldType fieldType = FIELD_TYPE_MAP.get(type);
            Objects.nonNull(fieldType);
            return fieldType;
        } else if (type.isMap()) {
            return getWireType(type.keyType());
        } else if (isEnum(type)) {
            return WireFormat.FieldType.ENUM;
        } else {
            return WireFormat.FieldType.MESSAGE;
        }
    }


    // Example:
    //
    // public SimpleMessage(int optional_int32, long optional_int64, ByteString unknownFields) {
    //   super(ADAPTER, unknownFields);
    //   this.optional_int32 = optional_int32;
    //   this.optional_int64 = optional_int64;
    // }
    //
    private MethodSpec messageFieldsAndUnknownFieldsConstructor(
            NameAllocator nameAllocator, MessageType type) {
        NameAllocator localNameAllocator = nameAllocator.clone();

        MethodSpec.Builder result = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC);

        for (OneOf oneOf : type.oneOfs()) {
            if (oneOf.fields().size() < 2) continue;
            CodeBlock.Builder fieldNamesBuilder = CodeBlock.builder();
            boolean first = true;
            for (Field field : oneOf.fields()) {
                if (!first) fieldNamesBuilder.add(", ");
                fieldNamesBuilder.add("$N", localNameAllocator.get(field));
                first = false;
            }
            CodeBlock fieldNames = fieldNamesBuilder.build();
            result.beginControlFlow("if ($T.countNonNull($L) > 1)", Internal.class, fieldNames);
            result.addStatement("throw new IllegalArgumentException($S)",
                    "at most one of " + fieldNames + " may be non-null");
            result.endControlFlow();
        }
        for (Field field : type.fieldsAndOneOfFields()) {
            TypeName javaType = fieldType(field);
            String fieldName = localNameAllocator.get(field);
            ParameterSpec.Builder param = ParameterSpec.builder(javaType, fieldName);
            if (emitAndroid && field.isOptional()) {
                param.addAnnotation(NULLABLE);
            }
            result.addParameter(param.build());
            if (field.isRepeated() || field.type().isMap()) {
                result.addStatement("this.$1L = $2T.immutableCopyOf($1S, $1L)", fieldName,
                        Internal.class);
            } else {
                result.addStatement("this.$1L = $1L", fieldName);
            }
        }

        return result.build();
    }

    private MethodSpec messageToString(NameAllocator nameAllocator, MessageType type) {
        NameAllocator localNameAllocator = nameAllocator.clone();

        MethodSpec.Builder result = MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class);

        String builderName = localNameAllocator.newName("builder");
        result.addStatement("$1T $2N = new $1T()", StringBuilder.class, builderName);

        for (Field field : type.fieldsAndOneOfFields()) {
            String fieldName = nameAllocator.get(field);
            if (field.isRepeated() || field.type().isMap()) {
                result.addCode("if ($N != null  && !$N.isEmpty()) ", fieldName,fieldName);
            } else if (!field.isRequired()) {
                result.addCode("if ($N != null) ", fieldName);
            }
            if (field.isRedacted()) {
                result.addStatement("$N.append(\", $N=██\")", builderName, field.name());
            } else {
                result.addStatement("$N.append(\", $N=\").append($L)", builderName, field.name(),
                        fieldName);
            }
        }

        result.addStatement("return builder.replace(0, 2, \"$L{\").append('}').toString()",
                type.type().simpleName());

        return result.build();
    }


    /**
     * Returns the initial value of {@code field}, or null if it is doesn't have one.
     */
    private CodeBlock initialValue(Field field) {
        if (field.isPacked() || field.isRepeated()) {
            return CodeBlock.of("$T.newMutableList()", Internal.class);
        } else if (field.type().isMap()) {
            return CodeBlock.of("$T.newMutableMap()", Internal.class);
        } else {
            return null;
        }
    }

    private CodeBlock defaultValue(Field field) {
        Object defaultValue = field.getDefault();

        if (defaultValue == null && isEnum(field.type())) {
            defaultValue = enumDefault(field.type()).name();
        }

        if (field.type().isScalar() || defaultValue != null) {
            return fieldInitializer(field.type(), defaultValue);
        }

        throw new IllegalStateException("Field " + field + " cannot have default value");
    }

    private CodeBlock fieldInitializer(ProtoType type, Object value) {
        TypeName javaType = typeName(type);

        if (value instanceof List) {
            CodeBlock.Builder builder = CodeBlock.builder();
            builder.add("$T.asList(", Arrays.class);
            boolean first = true;
            for (Object o : (List<?>) value) {
                if (!first) builder.add(",");
                first = false;
                builder.add("\n$>$>$L$<$<", fieldInitializer(type, o));
            }
            builder.add(")");
            return builder.build();

        } else if (value instanceof Map) {
            CodeBlock.Builder builder = CodeBlock.builder();
            builder.add("new $T.Builder()", javaType);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                ProtoMember protoMember = (ProtoMember) entry.getKey();
                Field field = schema.getField(protoMember);
                CodeBlock valueInitializer = fieldInitializer(field.type(), entry.getValue());
                builder.add("\n$>$>.$L($L)$<$<", fieldName(type, field), valueInitializer);
            }
            builder.add("\n$>$>.build()$<$<");
            return builder.build();

        } else if (javaType.equals(TypeName.BOOLEAN.box())) {
            return CodeBlock.of("$L", value != null ? value : false);

        } else if (javaType.equals(TypeName.INT.box())) {
            return CodeBlock.of("$L", value != null
                    ? new BigDecimal(String.valueOf(value)).intValue()
                    : 0);

        } else if (javaType.equals(TypeName.LONG.box())) {
            return CodeBlock.of("$LL", value != null
                    ? Long.toString(new BigDecimal(String.valueOf(value)).longValue())
                    : 0L);

        } else if (javaType.equals(TypeName.FLOAT.box())) {
            return CodeBlock.of("$Lf", value != null ? String.valueOf(value) : 0f);

        } else if (javaType.equals(TypeName.DOUBLE.box())) {
            return CodeBlock.of("$Ld", value != null ? String.valueOf(value) : 0d);

        } else if (javaType.equals(STRING)) {
            return CodeBlock.of("$S", value != null ? (String) value : "");

        } else if (javaType.equals(BYTE_STRING)) {
            if (value == null) {
                return CodeBlock.of("null");
            } else {
                return CodeBlock.of("$T.decodeBase64($S)", ByteString.class,
                        ByteString.of(String.valueOf(value).getBytes(Charsets.ISO_8859_1)).base64());
            }

        } else if (isEnum(type) && value != null) {
            return CodeBlock.of("$T.$L", javaType, value);

        } else {
            throw new IllegalStateException(type + " is not an allowed scalar type");
        }
    }

    public TypeSpec generateService(ProtoFile protoFile, Service service) {
        ClassName apiName = (ClassName) typeName(service.type());

        TypeSpec.Builder typeBuilder = TypeSpec.interfaceBuilder(apiName.simpleName());
        typeBuilder.addModifiers(PUBLIC);
        InterfaceMetaInfo interfaceMetaInfo = InterfaceMetaInfo.readFrom(protoFile, service);
        typeBuilder.addAnnotations(interfaceMetaInfo.generateAnnotations());


        if (!service.documentation().isEmpty()) {
            typeBuilder.addJavadoc("$L\n", service.documentation());
        }

        for (Rpc rpc : service.rpcs()) {
            ProtoType requestType = rpc.requestType();
            TypeName requestJavaType = typeName(requestType);

            ProtoType responseType = rpc.responseType();
            TypeName responseJavaType = typeName(responseType);

            MethodSpec.Builder rpcBuilder = MethodSpec.methodBuilder(rpc.name());
            MethodMetaInfo methodMetaInfo = MethodMetaInfo.readFrom(rpc);

            rpcBuilder.addAnnotation(serviceAnnotation(rpc, apiName, interfaceMetaInfo));
            rpcBuilder.addAnnotation(methodMetaInfo.generateRaptorMethod());

            rpcBuilder.addModifiers(PUBLIC, ABSTRACT);
            rpcBuilder.returns(responseJavaType);

            ParameterSpec request = ParameterSpec.builder(requestJavaType, "request").build();
            rpcBuilder.addParameter(request);

            if (!rpc.documentation().isEmpty()) {
                rpcBuilder.addJavadoc("$L\n", rpc.documentation());
            }

            typeBuilder.addMethod(rpcBuilder.build());
        }

        return typeBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private AnnotationSpec serviceAnnotation(Rpc rpc, ClassName className, InterfaceMetaInfo interfaceMetaInfo) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RequestMapping.class);

        MethodMetaInfo methodMetaInfo = MethodMetaInfo.readFrom(rpc);

        String path = methodMetaInfo.getPath();
        if (Objects.isNull(path)) {
            if (Objects.isNull(interfaceMetaInfo.getServicePath())) {
                builder.addMember("path", "$S", defaultRequestPath(rpc, className));
            }
        } else {
            builder.addMember("path", "$S", path);
        }

        Method method = methodMetaInfo.getMethod();
        if (Objects.nonNull(method)) {
            builder.addMember("method", "$T.$L", RequestMethod.class, method.getName());
        } else {
            if (Objects.isNull(path)) {
                //没有指定method,没有path ,默认POST
                builder.addMember("method", "$T.$L", RequestMethod.class, "POST");
            } else {
                //没有指定method,但是有path ,默认GET
                builder.addMember("method", "$T.$L", RequestMethod.class, "GET");

            }
        }

        return builder.build();
    }

    private Object defaultRequestPath(Rpc rpc, ClassName className) {
        ArrayList<String> params = Lists.newArrayList(URLParamType.basePath.getValue(), className.reflectionName(), rpc.name());
        return RaptorConstants.PATH_SEPARATOR + StringUtils.join(params, RaptorConstants.PATH_SEPARATOR);
    }


}
