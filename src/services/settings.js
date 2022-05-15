import {NativeEventEmitter, NativeModules} from 'react-native';

const {LockModule} = NativeModules;

const eventEmitter = new NativeEventEmitter(NativeModules.LockModule);

export function startScan(callback) {
  const eventListener = eventEmitter.addListener(
    'BluetoothScanResult',
    callback,
  );
  // TODO: call unsubscribe methods
  // https://reactnative.dev/docs/native-modules-android#sending-events-to-javascript
  return LockModule.startScan();
}

export function stopScan() {
  return LockModule.stopScan();
}

export function getBluetoothDeviceUuid() {
  // TODO: fix
  return LockModule.getBluetoothDeviceUuid();
}

export function setBluetoothDeviceUuid(value) {
  return LockModule.setBluetoothDeviceUuid(value);
}

export function enable() {
  return LockModule.enable();
}

export function disable() {
  return LockModule.disable();
}

export function lock() {
  return LockModule.lock();
}
