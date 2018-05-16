// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: com/ppdai/framework/raptor/proto/helloworld.proto
package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.RaptorMessage;
import com.squareup.wire.*;
import com.squareup.wire.internal.Internal;
import okio.ByteString;

import java.io.IOException;

@RaptorMessage(
    version = "version.0.1",
    protoFile = "helloworld"
)
public final class Cat extends Message<Cat, Cat.Builder> {
  public static final ProtoAdapter<Cat> ADAPTER = new ProtoAdapter_Cat();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_COLOR = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  private String color;

  public Cat() {
    super(ADAPTER, ByteString.EMPTY);
  }

  public Cat(String color) {
    this(color, ByteString.EMPTY);
  }

  public Cat(String color, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.color = color;
  }

  public String getColor() {
    return this.color;
  }

  public void setColor(String color) {
    this.color=color;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.color = color;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Cat)) return false;
    Cat o = (Cat) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(color, o.color);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (color != null ? color.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (color != null) builder.append(", color=").append(color);
    return builder.replace(0, 2, "Cat{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Cat, Builder> {
    public String color;

    public Builder() {
    }

    public Builder color(String color) {
      this.color = color;
      return this;
    }

    @Override
    public Cat build() {
      return new Cat(color, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Cat extends ProtoAdapter<Cat> {
    public ProtoAdapter_Cat() {
      super(FieldEncoding.LENGTH_DELIMITED, Cat.class);
    }

    @Override
    public int encodedSize(Cat value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.color)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Cat value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.color);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Cat decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.color(ProtoAdapter.STRING.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public Cat redact(Cat value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
