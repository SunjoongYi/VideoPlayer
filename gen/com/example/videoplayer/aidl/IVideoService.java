/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Git\\VideoPlayer\\src\\com\\example\\videoplayer\\aidl\\IVideoService.aidl
 */
package com.example.videoplayer.aidl;
public interface IVideoService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.videoplayer.aidl.IVideoService
{
private static final java.lang.String DESCRIPTOR = "com.example.videoplayer.aidl.IVideoService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.videoplayer.aidl.IVideoService interface,
 * generating a proxy if needed.
 */
public static com.example.videoplayer.aidl.IVideoService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.videoplayer.aidl.IVideoService))) {
return ((com.example.videoplayer.aidl.IVideoService)iin);
}
return new com.example.videoplayer.aidl.IVideoService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getString:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getString();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setAudio:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.setAudio(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentTime:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentTime();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.videoplayer.aidl.IVideoService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String getString() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getString, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setAudio(java.lang.String path, int currentTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
_data.writeInt(currentTime);
mRemote.transact(Stub.TRANSACTION_setAudio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getCurrentTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getString = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setAudio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getCurrentTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public java.lang.String getString() throws android.os.RemoteException;
public void setAudio(java.lang.String path, int currentTime) throws android.os.RemoteException;
public int getCurrentTime() throws android.os.RemoteException;
}
