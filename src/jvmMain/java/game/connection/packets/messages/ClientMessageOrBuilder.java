// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.proto

// Protobuf Java Version: 3.25.3
package game.connection.packets.messages;

public interface ClientMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:connection.ClientMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>.connection.ClientMessage.InputM input = 2;</code>
   * @return Whether the input field is set.
   */
  boolean hasInput();
  /**
   * <code>.connection.ClientMessage.InputM input = 2;</code>
   * @return The input.
   */
  game.connection.packets.messages.ClientMessage.InputM getInput();
  /**
   * <code>.connection.ClientMessage.InputM input = 2;</code>
   */
  game.connection.packets.messages.ClientMessage.InputMOrBuilder getInputOrBuilder();
}