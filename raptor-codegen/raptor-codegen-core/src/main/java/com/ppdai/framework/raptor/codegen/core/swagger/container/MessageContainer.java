package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.DescriptorProtosTagNumbers;
import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-27.
 * 存放从proto文件中提取的message类型
 */
public class MessageContainer {

    private Map<String, MessageType> messageTypeMap = new LinkedHashMap<>();


    public void addMessageProto(String packageName, String className, String parent, DescriptorProtos.DescriptorProto descriptorProto, List<Integer> parentPath) {
        MessageType messageType = new MessageType();
        messageType.setName((parent != null ? parent + ProtobufConstant.PACKAGE_SEPARATOR : "") + descriptorProto.getName());
        // TODO: 2018/3/6 packageName 为空的话会多一个点
        messageType.setFullyQualifiedPathName(packageName + ProtobufConstant.PACKAGE_SEPARATOR + messageType.getName());
        messageType.setFullyQualifiedClassName(StringUtils.join(new String[]{packageName, className, messageType.getName()}, ProtobufConstant.PACKAGE_SEPARATOR));
        messageType.setClassName(className);
//        Map<String, FieldType> fieldTypeMap = new LinkedHashMap<>();
        List<FieldType> fieldTypeList = new ArrayList<>();
        messageType.setFields(fieldTypeList);
        messageType.setPackageName(packageName);
        List<DescriptorProtos.FieldDescriptorProto> fieldList = descriptorProto.getFieldList();
        for (int i = 0; i < fieldList.size(); i++) {
            DescriptorProtos.FieldDescriptorProto ffdp = fieldList.get(i);
            List<Integer> currentPath = new ArrayList<>(parentPath);
            currentPath.add(DescriptorProtosTagNumbers.DescriptorProto.FIELD);
            currentPath.add(i);

            FieldType fieldType = new FieldType();
            fieldType.setName(ffdp.getName());
            fieldType.setType(ffdp.getType());
            fieldType.setLabel(ffdp.getLabel());
            String fullyQualifiedPathName = ffdp.getTypeName().replaceAll("^\\.", "");
            fieldType.setFullyQualifiedPathName(fullyQualifiedPathName);
            fieldType.setMessage(messageType.getFullyQualifiedPathName());
            fieldType.setPath(currentPath);
            fieldTypeList.add(fieldType);
        }

        messageTypeMap.put(messageType.getFullyQualifiedPathName(), messageType);
    }

    public MessageType findMessageTypeByFullyQualifiedPathName(String fullyQualifiedPathName) {
        return messageTypeMap.get(fullyQualifiedPathName);
    }

    public Collection<MessageType> getMessageTypeList() {
        return messageTypeMap.values();
    }

    public void merge(MessageContainer messageContainer) {
        Map<String, MessageType> messageTypeMap = messageContainer.messageTypeMap;
        this.messageTypeMap.putAll(messageTypeMap);
    }
}
